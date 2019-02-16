package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import org.json.JSONObject;

public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;
	private NamespaceModel namespace;

	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterMethodDeclarator(Java9Parser.MethodDeclaratorContext ctx) {  //see gramBaseListener for allowed functions
      	
	    fileModel.addFunction(new FunctionModel(ctx.getText()));
    }

	@Override
	public void enterPackageDeclaration(Java9Parser.PackageDeclarationContext ctx){
		this.namespace = new NamespaceModel(ctx.packageName().getText());
	}

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