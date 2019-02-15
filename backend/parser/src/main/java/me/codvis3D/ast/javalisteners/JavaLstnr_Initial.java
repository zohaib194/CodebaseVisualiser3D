package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.json.JSONObject;

public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;

	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterMethodDeclarator(Java9Parser.MethodDeclaratorContext ctx) {  //see gramBaseListener for allowed functions
      	
	    fileModel.addFunction(new FunctionModel(ctx.getText()));
    }

    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

 }