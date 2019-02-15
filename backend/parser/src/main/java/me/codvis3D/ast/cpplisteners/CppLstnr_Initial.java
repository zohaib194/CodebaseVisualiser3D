package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;

import org.json.JSONObject;

public class CppLstnr_Initial extends CppExtendedListener {
	private FileModel fileModel;

	CppLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {  
        
        // Get interval between function start and end of function name.
	    Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());
	    
	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();

	    fileModel.addFunction(new FunctionModel(input.getText(interval)));
    }

    public JSONObject getParsedCode() {  
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }
 }