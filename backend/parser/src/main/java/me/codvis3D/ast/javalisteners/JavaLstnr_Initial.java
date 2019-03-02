package me.codvis.ast;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;
import org.antlr.v4.runtime.ParserRuleContext;

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

	    // Set this function model with name, lineStart and lineEnd.
      	FunctionModel functionModel = new FunctionModel(ctx.methodHeader().methodDeclarator().getText());
      	functionModel.setLineStart(ctx.methodBody().start.getLine());
      	functionModel.setLineEnd(ctx.methodBody().stop.getLine());
	    

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