package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.json.JSONObject;

public class JavaLstnr_Initial extends JavaExtendedListener {
	private String fileName;
	private List<FunctionModel> functions;


	JavaLstnr_Initial(String filePath) {
		this.fileName = filePath;
		this.functions = new ArrayList<>();
	}

    @Override 
    public void enterMethodDeclarator(Java9Parser.MethodDeclaratorContext ctx) {  //see gramBaseListener for allowed functions
      	
	    functions.add(new FunctionModel(ctx.getText()));
    }

    public JSONObject getParsedCode() {
    	JSONObject parsedCode = new JSONObject();

    	parsedCode.put("functions", this.functions);
    	
    	return parsedCode;
    }

 }