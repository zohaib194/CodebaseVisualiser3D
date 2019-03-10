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

	    // Set this function model with name, lineStart and lineEnd.
      	FunctionModel functionModel = new FunctionModel(input.getText(interval), ctx.methodHeader().methodDeclarator().identifier().getText());
      	functionModel.setLineStart(ctx.methodBody().start.getLine());
      	functionModel.setLineEnd(ctx.methodBody().stop.getLine());
	    functionModel.setParameters(
	    	fetchParameters(
	    		ctx.methodHeader().methodDeclarator()
	    	)
	    );

	   	this.scopeStack.peek().addDataInModel(functionModel);
	    this.enterScope(functionModel);
    }

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterMethodDeclaration.
     *
     * @param      ctx   The parsing context
     */
    @Override 
    public void exitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) { 
    	this.exitScope();
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
			System.out.println("Unhandeled using dirctive");
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
	 * Listener for parsing variables from scope.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override 
	public void enterBlockStatement(Java9Parser.BlockStatementContext ctx) { 
		String name = "";
		String type = "";
		if(ctx.localVariableDeclarationStatement() != null){

			type = fetchVariableModifiers(ctx.localVariableDeclarationStatement().localVariableDeclaration().variableModifier());
			type += ctx.localVariableDeclarationStatement().localVariableDeclaration().unannType().getText();
			
			List<Java9Parser.VariableDeclaratorContext> variableDeclList = ctx.localVariableDeclarationStatement().localVariableDeclaration().variableDeclaratorList().variableDeclarator();
			
			for (Iterator<Java9Parser.VariableDeclaratorContext> i = variableDeclList.iterator(); i.hasNext();) {
				Java9Parser.VariableDeclaratorContext variable = i.next();
				name = variable.variableDeclaratorId().getText();
	   			this.scopeStack.peek().addDataInModel(new VariableModel(name, type));
			}
		}
	}

	/**
	 * Fetches parameters.
	 *
	 * @param      ctx   The parsing context
	 *
	 * @return     List of parameters within given method declarator.
	 */
	private List<VariableModel> fetchParameters(Java9Parser.MethodDeclaratorContext ctx) { 
		Java9Parser.FormalParametersContext formalParams = null;
		Java9Parser.LastFormalParameterContext lastFormalParam = null;
		Java9Parser.ReceiverParameterContext receiverParam = null;
		List<VariableModel> parameters = new ArrayList<>();
		String type = "";
		String name = "";

		if(ctx.formalParameterList() == null) {
			return new ArrayList<>();
		}

		formalParams = ctx.formalParameterList().formalParameters();
		lastFormalParam = ctx.formalParameterList().lastFormalParameter();
		receiverParam = ctx.formalParameterList().receiverParameter();
		
		// Parse formal parameters.
		if(formalParams != null) {
			if(formalParams.receiverParameter() != null){
				type = formalParams.receiverParameter().unannType().getText();
				if(formalParams.receiverParameter().identifier() != null){
					name = formalParams.receiverParameter().identifier().getText() + " this";
				} else {
					name += "this";
				}
   				parameters.add(new VariableModel(name, type));
			}

			List<Java9Parser.FormalParameterContext> params = formalParams.formalParameter();
			for (Iterator<Java9Parser.FormalParameterContext> i = params.iterator(); i.hasNext();) {
				Java9Parser.FormalParameterContext parameter = i.next();

				type = fetchVariableModifiers(parameter.variableModifier());
				type += parameter.unannType().getText();
				name = parameter.variableDeclaratorId().getText();

   				parameters.add(new VariableModel(name, type));
			}

		}

		// Parse the last parameters (single parameters.)
		if(lastFormalParam != null){
			if(lastFormalParam.formalParameter() != null){
				type = fetchVariableModifiers(lastFormalParam.formalParameter().variableModifier());
				type += lastFormalParam.formalParameter().unannType().getText();
				name = lastFormalParam.formalParameter().variableDeclaratorId().getText(); 
			} else {
				type = fetchVariableModifiers(lastFormalParam.variableModifier());
				type += lastFormalParam.unannType().getText();
				name = lastFormalParam.variableDeclaratorId().getText();
			}

   			parameters.add(new VariableModel(name, type));
		}

		// Parse receiver parameters (this).
		if(receiverParam != null){
			type = receiverParam.unannType().getText();
			if(receiverParam.identifier() != null){
				name = receiverParam.identifier().getText() + " this";
			} else {
				name += "this";
			}
   			parameters.add(new VariableModel(name, type));
		}

		return parameters;
	}

	/**
	 * Fetches variable modifiers.
	 *
	 * @param      variableModifiers  The variable modifiers parsing context.
	 *
	 * @return     a string containing all modifiers with whitespaces.
	 */
	private String fetchVariableModifiers(List<Java9Parser.VariableModifierContext> variableModifiers) {
		String modifiers = "";
		
		for (Iterator<Java9Parser.VariableModifierContext> j = variableModifiers.iterator(); j.hasNext();) {
			Java9Parser.VariableModifierContext variableModifier = j.next();
			modifiers += variableModifier.getText() + " ";
		}

		return modifiers;
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