package me.codvis.ast;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Iterator;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;

import org.json.JSONObject;

/**
 * Class for exstending listeners and parsing code requiered for initial 3D view for code abstraction.
 */
public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath to file being parsed.
	 *
	 * @param      filePath  The file path
	 */
	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
		this.enterScope(this.fileModel);
	}

	/**
	 * Listener for parsing a method/function declaration. Adding function name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
    @Override
    public void enterMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {  //see gramBaseListener for allowed functions
    	// Get interval between function start and end of function name.
	    Interval interval = new Interval(ctx.methodHeader().start.getStartIndex(), ctx.methodHeader().stop.getStopIndex());

	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();
      	FunctionModel functionModel = new FunctionModel(input.getText(interval), ctx.methodHeader().methodDeclarator().identifier().getText());
    	functionModel.setLineStart(ctx.methodBody().start.getLine());
      	functionModel.setLineEnd(ctx.methodBody().stop.getLine());
	    this.enterScope(functionModel);
    }

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterMethodDeclaration.
     *
     * @param      ctx   The parsing context
     */
    @Override
    public void exitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
    	Model model = this.exitScope();

    	if(model instanceof FunctionModel){
	    	this.scopeStack.peek().addDataInModel((FunctionModel) model);
    	} else {
    		this.enterScope(model);
    		System.err.println("Could not understand parent model for method declaration.");
    	}
    }

	@Override
	public void enterMethodBody(Java9Parser.MethodBodyContext ctx) {
		this.enterScope(new FunctionBodyModel());
	}

	@Override
	public void exitMethodBody(Java9Parser.MethodBodyContext ctx) {
		FunctionBodyModel functionBody = (FunctionBodyModel) this.exitScope();
		this.scopeStack.peek().addDataInModel(functionBody);
	}


    /**
     * Listener for parsing a package/namespace declaration. Adding package name to filemodel.
     *
     * @param      ctx   The parsing context
     */
	@Override
	public void enterPackageDeclaration(Java9Parser.PackageDeclarationContext ctx){
		NamespaceModel namespace = new NamespaceModel(ctx.packageName().getText());

		this.scopeStack.peek().addDataInModel(namespace);
	    this.enterScope(namespace);
	}

	/**
	 * Listener for parsing a package/namespace import. Adding package name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterImportDeclaration(Java9Parser.ImportDeclarationContext ctx){
		Java9Parser.SingleStaticImportDeclarationContext importSingle = ctx.singleStaticImportDeclaration();
		Java9Parser.StaticImportOnDemandDeclarationContext importOnDemand = ctx.staticImportOnDemandDeclaration();
		Java9Parser.SingleTypeImportDeclarationContext importSingleType = ctx.singleTypeImportDeclaration();
		Java9Parser.TypeImportOnDemandDeclarationContext importTypeOnDemand = ctx.typeImportOnDemandDeclaration();

		UsingNamespaceModel usingNamespaceModel;

		if ( importSingle != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingle.typeName().getText(), importSingle.typeName().getStart().getLine());

		} else if (importOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importOnDemand.typeName().getText(), importOnDemand.typeName().getStart().getLine());

		} else if (importSingleType != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingleType.typeName().getText(), importSingleType.typeName().getStart().getLine());

		} else if (importTypeOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importTypeOnDemand.packageOrTypeName().getText(), importTypeOnDemand.packageOrTypeName().getStart().getLine());

		}else{
			System.err.println("Unhandeled using dirctive");
			return;
		}

	   	this.scopeStack.peek().addDataInModel(usingNamespaceModel);
	}

	/**
	 * Listener for parsing function calls.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterMethodInvocation(Java9Parser.MethodInvocationContext ctx) {
	    this.scopeStack.peek().addDataInModel(ctx.getText());
	}

	/**
	 * Listener for adding local variables in to the scope.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterLocalVariableDeclarationStatement(Java9Parser.LocalVariableDeclarationStatementContext ctx) {
		this.enterScope(new VariableListModel());
	}

	/**
	 * Listener for exiting the variable scope and add data in to parent model.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void exitLocalVariableDeclarationStatement(Java9Parser.LocalVariableDeclarationStatementContext ctx) {

		VariableListModel variableList = (VariableListModel) this.exitScope();

		if ( this.scopeStack.peek() instanceof FunctionBodyModel ||
		     this.scopeStack.peek() instanceof FileModel ||
		     this.scopeStack.peek() instanceof NamespaceModel) {

			for (Iterator<String> i = variableList.getNames().iterator(); i.hasNext();) {
			    String variableName = i.next();
				this.scopeStack.peek().addDataInModel(new VariableModel(variableName, variableList.getType()));
			}

		} else {
    		System.err.println("Could not understand parent model for simple declaration.");
    	}
	}

	/**
	 * Listener for adding formal parameters into the scope.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterFormalParameter(Java9Parser.FormalParameterContext ctx) {
		this.enterScope(new VariableModel());
	}

	/**
	 * Listener for exiting parameter scope and adding data into the parent model.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void exitFormalParameter(Java9Parser.FormalParameterContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel){
			FunctionModel func = (FunctionModel) model;
			func.addParameter(vm);
			this.enterScope(func);
		} else {
			this.enterScope(model);
	    	System.err.println("Could not understand parent model for formal parameter.");
		}

	}

	/**
	 * Listener for adding last formal parameter into the scope.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterLastFormalParameter(Java9Parser.LastFormalParameterContext ctx) {
		if(ctx.variableDeclaratorId() != null){
			this.enterScope(new VariableModel());
		}
	}

	/**
	 * Listener for exiting the last formal parameter scope and adding data into parent model.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void exitLastFormalParameter(Java9Parser.LastFormalParameterContext ctx) {
		if(ctx.variableDeclaratorId() != null){
			VariableModel vm = (VariableModel) this.exitScope();
			Model model = this.exitScope();
			if (model instanceof FunctionModel){
				FunctionModel func = (FunctionModel) model;
				func.addParameter(vm);
				this.enterScope(func);
			} else {
				this.enterScope(model);
		    	System.err.println("Could not understand parent model for last formal parameter.");
			}
		}
	}

	/**
	 * Listener for adding receiver parameter into the scope.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterReceiverParameter(Java9Parser.ReceiverParameterContext ctx) {
		this.enterScope(new VariableModel());
	}

	/**
	 * Listener for exiting the receiver parameter scope and adding data into the parent model.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void exitReceiverParameter(Java9Parser.ReceiverParameterContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel){
			FunctionModel func = (FunctionModel) model;
			if(vm.hasName()){
				func.addParameter(vm);
			} else {
				vm.setName("this");
				func.addParameter(vm);
			}
			this.enterScope(func);
		} else {
			this.enterScope(model);
	    	System.err.println("Could not understand parent model for receiver parameter.");
		}
	}

	/**
	 * Listener for parsing variable id.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterVariableDeclaratorId(Java9Parser.VariableDeclaratorIdContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.addName(ctx.getText());
			this.enterScope(vlm);
		} else {
	    	System.err.println("Could not understand parent model for identifier.");
		}
	}

	/**
	 * Listener for parsing primitive type.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterUnannType(Java9Parser.UnannTypeContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.applyUnnanTypeOnType(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.applyUnnanTypeOnType(ctx.getText());
			this.enterScope(vlm);
		} else {
	    	System.err.println("Could not understand parent model for unannType.");
		}
	}

	/**
	 * Listener for parsing variable modifiers.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterVariableModifier(Java9Parser.VariableModifierContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.applyModifierOnType(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.applyModifierOnType(ctx.getText());
			this.enterScope(vlm);
		} else {
	    	System.err.println("Could not understand parent model for variable modifier.");
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 * Adding the file content as part of the package being declared at start of file.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }
 }