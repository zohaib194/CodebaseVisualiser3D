package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;

public class CppListener extends CPP14BaseListener {

    @Override 
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {  
        
        // Get start and stop of function.
        int a = ctx.start.getStartIndex();
	    int b = ctx.stop.getStopIndex();

	    // Get the input stream of function definition rule.
	    Interval interval = new Interval(a,b);
	    CharStream input = ctx.start.getInputStream();

	    // Get the function contect and remove the definition.
	    String funcName = input.getText(interval);
	    funcName = funcName.substring(0, funcName.indexOf("{"));

        System.out.println("function_name: " + funcName);      
    }
 }