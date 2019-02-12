package me.codvis.ast;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

public class JavaListener extends Java9BaseListener {

    @Override 
    public void enterMethodDeclarator(Java9Parser.MethodDeclaratorContext ctx) {  //see gramBaseListener for allowed functions
        System.out.println("function_name: " + ctx.getText());     //code that executes per rule
    }
 }