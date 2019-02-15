package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
//import org.antlr.v4.runtime.*;

import org.json.JSONObject;

public class CppLstnr_BodyExtraction extends CppExtendedListener {
	private FileModel fileModel;

	CppLstnr_BodyExtraction(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {  

    	// Get the interval where the function name and body exists.
	    Interval nameInterval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());
	    Interval bodyInterval = new Interval(ctx.functionbody().start.getStopIndex(), ctx.functionbody().stop.getStopIndex());
	    
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