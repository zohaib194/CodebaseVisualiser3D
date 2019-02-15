package me.codvis.ast;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;

import org.json.JSONObject;

public class JavaLstnr_BodyExtraction extends JavaExtendedListener {
	private FileModel fileModel;

	JavaLstnr_BodyExtraction(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
      	
      	// Get the interval where the function name and body exists.
	    Interval nameInterval = new Interval(ctx.start.getStartIndex(), ctx.methodHeader().stop.getStopIndex());
	    Interval bodyInterval = new Interval(ctx.methodBody().start.getStopIndex(), ctx.methodBody().stop.getStopIndex());

	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();

	    // Create a function model, set name and body.
	    FunctionModel function = new FunctionModel(input.getText(nameInterval));
        function.setBody(input.getText(bodyInterval));
	    fileModel.addFunction(function);
    }

    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

 }