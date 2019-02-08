package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

public class CppListener extends CPP14BaseListener {
    @Override public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {  //see gramBaseListener for allowed functions
        System.out.println("Function name: " + ctx.getText());      //code that executes per rule
    }
 }