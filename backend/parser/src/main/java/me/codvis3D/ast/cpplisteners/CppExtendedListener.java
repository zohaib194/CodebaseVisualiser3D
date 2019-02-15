package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import org.json.JSONObject;

public class CppExtendedListener extends CPP14BaseListener implements CppResultInterface{

	// function for getting parse result outside of our listeners
    public JSONObject getParsedCode() {  
    	return null;
    }
 }