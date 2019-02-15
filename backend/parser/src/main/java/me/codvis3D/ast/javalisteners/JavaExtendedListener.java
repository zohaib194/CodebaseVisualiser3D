package me.codvis.ast;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.json.JSONObject;

public class JavaExtendedListener extends Java9BaseListener implements JavaResultInterface{

	// function for getting parse result outside of our listeners
    public JSONObject getParsedCode() {  
    	return null;
    }
 }