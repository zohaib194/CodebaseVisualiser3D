package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14Lexer;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.*;

import org.json.JSONObject;

/**
 * Class for exstending listeners and parsing code requiered for initial 3D view for code abstraction.
 */
public class CppLstnr_Initial extends CppExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath for file being parsed.
	 *
	 * @param      filePath  The file path
	 */
	CppLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
		this.enterScope(this.fileModel);
	}

	/**
	 * Listener for parsing a method/function declaration. Adding function name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        // Get interval between function start and end of function name.
	    Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());

	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();

        FunctionModel functionModel = new FunctionModel(input.getText(interval));
       	functionModel.setLineStart(ctx.functionbody().start.getLine());
	    functionModel.setLineEnd(ctx.functionbody().stop.getLine());
	    this.enterScope(functionModel);

    }

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterFunctiondefinition.
     *
     * @param      ctx   The parsing context
     */
    @Override
    public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
    	Model model = this.exitScope();
		System.out.println("Exit Functiondefinition");

    	if(model instanceof FunctionModel){
	    	this.scopeStack.peek().addDataInModel((FunctionModel) model);
    	} else {
    		this.enterScope(model);
    		System.out.println("Could not find function model in scope stack on exitFunctiondefinition.");
	    	System.exit(1);
    	}
    }

    /**
     * Listener for parsing variables from scope.
     *
     * @param      ctx   The parsing context

    @Override
    public void enterStatement(CPP14Parser.StatementContext ctx) {
    	CPP14Parser.DeclarationstatementContext declStatement = ctx.declarationstatement();
    	CPP14Parser.BlockdeclarationContext blockdeclaration = null;
    	CPP14Parser.InitdeclaratorlistContext initDeclaratorlist = null;
    	String variableName = "";
    	String variableType = "";
    	if(declStatement != null){

	    	if(declStatement.blockdeclaration() != null){
	    		blockdeclaration = declStatement.blockdeclaration();

	    		if(blockdeclaration.simpledeclaration() != null){

	    			if(blockdeclaration.simpledeclaration().declspecifierseq() != null){
	    				variableType = blockdeclaration.simpledeclaration().declspecifierseq().getText();

		    			if(blockdeclaration.simpledeclaration().initdeclaratorlist() != null){
		    				initDeclaratorlist = blockdeclaration.simpledeclaration().initdeclaratorlist();

		    				while(initDeclaratorlist != null){
		    					if(initDeclaratorlist.initdeclarator().declarator() == null){
		    						break;
		    					}

	    						variableName = initDeclaratorlist.initdeclarator().declarator().getText();
	    						this.scopeStack.peek().addDataInModel(new VariableModel(variableName, variableType));
		    					initDeclaratorlist = initDeclaratorlist.initdeclaratorlist();
			    			}
		    			}
	    			}
	    		}
    		}
    	}
    }
*/
	@Override
	public void enterDeclarationstatement(CPP14Parser.DeclarationstatementContext ctx) {

	}


	@Override
	public void enterInitdeclarator(CPP14Parser.InitdeclaratorContext ctx) {
		this.enterScope(new VariableModel());
		System.out.println("Enter Initdeclarator");

		System.out.println(ctx.getText());
	}

	@Override
	public void exitInitdeclarator(CPP14Parser.InitdeclaratorContext ctx) {
		VariableModel variable = (VariableModel) this.exitScope();
		if (this.scopeStack.peek() instanceof FunctionBodyModel){
			this.scopeStack.peek().addDataInModel(variable);
			System.out.println("Exit Initdeclarator");

		}
	}

	@Override
	public void exitDeclarationstatement(CPP14Parser.DeclarationstatementContext ctx) {
	}


/*

	@Override
	public void enterInitializerlist(CPP14Parser.InitializerlistContext ctx) {
		this.enterScope(new VariableModel());
		System.out.println("Enter initializerlist");

		System.out.println(ctx.getText());

	}

	@Override
	public void exitInitializerlist(CPP14Parser.InitializerlistContext ctx) {
		VariableModel variable = (VariableModel) this.exitScope();
		if (this.scopeStack.peek() instanceof FunctionBodyModel){
			this.scopeStack.peek().addDataInModel(variable);
			System.out.println("Exit initializerlist");

		}

	}
*/

/*
	@Override
	public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
    	CPP14Parser.InitdeclaratorlistContext initDeclaratorlist = null;
		String variableName = "";
    	String variableType = "";

		System.out.println("simpledeclaration: "+ctx.getText());
		if(ctx.declspecifierseq() != null){
			variableType = ctx.declspecifierseq().getText();

			if(ctx.initdeclaratorlist() != null){
				initDeclaratorlist = ctx.initdeclaratorlist();

				while(initDeclaratorlist != null){
					if(initDeclaratorlist.initdeclarator().declarator() == null){
						break;
					}

					variableName = initDeclaratorlist.initdeclarator().declarator().getText();
					this.scopeStack.peek().addDataInModel(new VariableModel(variableName, variableType));
					initDeclaratorlist = initDeclaratorlist.initdeclaratorlist();
				}
			}
		}
	}
*/

    /**
	 * Listener for parsing a namespace declaration. Adding namespace to filemodel.
     *
     * @param      ctx   The parsing context
     */
    @Override
	public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
		NamespaceModel namespace = new NamespaceModel(ctx.Identifier().getText());

	    this.scopeStack.peek().addDataInModel(namespace);
	    this.enterScope(namespace);
	}

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterOriginalnamespacedefinition.
     *
     * @param      ctx   The parsing context
     */
	@Override
	public void exitOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
		this.exitScope();
	}

	/**
	 * Listener for parsing a using namespace declaration. Adding namespace to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) {
		UsingNamespaceModel usingNamespaceModel;
		if (ctx.nestednamespecifier() != null) {
			usingNamespaceModel = new UsingNamespaceModel(ctx.nestednamespecifier().getText() + ctx.namespacename().getText(), ctx.getStart().getLine());

		}else{
			usingNamespaceModel = new UsingNamespaceModel(ctx.namespacename().getText(), ctx.getStart().getLine());
		}

		this.scopeStack.peek().addDataInModel(usingNamespaceModel);
	}

	/**
	 * Listener for parsing function calls.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) {
	    this.scopeStack.peek().addDataInModel(ctx.getText());
	}

	@Override
	public void enterFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
		this.enterScope(new FunctionBodyModel());
	}

	@Override
	public void exitFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
		FunctionBodyModel functionBody = (FunctionBodyModel) this.exitScope();
		this.scopeStack.peek().addDataInModel(functionBody);
	}

	@Override
	public void enterDeclaratorid(CPP14Parser.DeclaratoridContext ctx) {
		Model model = this.exitScope();
		if(model instanceof FunctionModel){
			FunctionModel func = (FunctionModel) model;
			func.setDeclaratorId(ctx.getText());
			this.enterScope(func);
		} else {
			this.enterScope(model);
	    	System.out.println("Could not understand parent model for declaratorid. " + model.getClass());
	    //	System.exit(1);
		}
	}

	@Override
	public void enterQualifiedid(CPP14Parser.QualifiedidContext ctx) {
		if(ctx.nestednamespecifier() != null){
			Model model = this.exitScope();
			if(model instanceof FunctionModel) {
				FunctionModel func = (FunctionModel) model;
				System.out.println("QualifiedidContext:" + ctx.unqualifiedid().getText());
				func.setScope(ctx.nestednamespecifier().getText());
				this.enterScope(func);
			} else {
				this.enterScope(model);
			}
		}

	}

	@Override
	public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		this.enterScope(new VariableModel());
	}

	@Override
	public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel){
			FunctionModel func = (FunctionModel) model;
			func.addParameter(vm);
			this.enterScope(func);
		} else {
			this.enterScope(model);
	    	System.out.println("Could not understand parent model for parameter declaration.");
	    //	System.exit(1);
		}
	}

	@Override
	public void enterDeclarator(CPP14Parser.DeclaratorContext ctx) {
		System.out.println("\n\nDeclarator " + ctx.getText() + " " + this.scopeStack.peek().getClass() + "\n\n");
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
		}
	}

	@Override
	public void enterDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
		System.out.println("\n\nDeclspecifierseq " + ctx.getText() + " " + this.scopeStack.peek().getClass() + "\n\n");
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setType(ctx.getText());
			this.enterScope(vm);
		}
	}

	@Override
	public void enterAbstractdeclarator(CPP14Parser.AbstractdeclaratorContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel){
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
			System.out.println(this.scopeStack.size());
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

    /**
     * Gets the file model.
     *
     * @return     The file model.
     */
    public FileModel getFileModel(){
    	return this.fileModel;
    }
 }