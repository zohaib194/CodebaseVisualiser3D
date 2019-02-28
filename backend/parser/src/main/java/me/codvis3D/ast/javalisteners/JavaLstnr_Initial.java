package me.codvis.ast;

import java.util.List;
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
	private NamespaceModel namespace;

	/**
	 * Constructs the object, setting the filepath to file being parsed.
	 *
	 * @param      filePath  The file path
	 */
	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
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
	    
	    fileModel.addFunction(functionModel);
    }

    /**
     * Listener for parsing a package/namespace declaration. Adding package name to filemodel.
     *
     * @param      ctx   The parsing context
     */
	@Override
	public void enterPackageDeclaration(Java9Parser.PackageDeclarationContext ctx){
		this.namespace = new NamespaceModel(ctx.packageName().getText());
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
		if ( importSingle != null) {
			this.fileModel.addUsingNamespace(
				new UsingNamespaceModel(importSingle.typeName().getText(), importSingle.typeName().getStart().getLine())
			);
		} else if (importOnDemand != null) {
			this.fileModel.addUsingNamespace(
				new UsingNamespaceModel(importOnDemand.typeName().getText(), importOnDemand.typeName().getStart().getLine())
			);				

		} else if (importSingleType != null) {
			this.fileModel.addUsingNamespace(
				new UsingNamespaceModel(importSingleType.typeName().getText(), importSingleType.typeName().getStart().getLine())
			);		

		} else if (importTypeOnDemand != null) {
			this.fileModel.addUsingNamespace(
				new UsingNamespaceModel(importTypeOnDemand.packageOrTypeName().getText(), importTypeOnDemand.packageOrTypeName().getStart().getLine())
			);

		}
		
	}	

	/**
	 * Listener for parsing function calls.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override 
	public void enterMethodInvocation(Java9Parser.MethodInvocationContext ctx) { 
		System.out.println(ctx.getText() + "\n");
	}

	/**
	 * Gets the parsed code as JSONObject.
	 * Adding the file content as part of the package being declared at start of file.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		if (this.namespace != null) {
			
        	this.namespace.setFunctions(this.fileModel.getFunctions());
        	this.namespace.setNamespaces(this.fileModel.getNamespaces());
        	this.namespace.setUsingNamespaces(this.fileModel.getUsingNamespaces());

        	FileModel filemodel = new FileModel(this.fileModel.getFilename());

        	filemodel.addNamespace(this.namespace);
    		
    		return parsedCode.put("file", filemodel.getParsedCode());
		}
    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }
 }