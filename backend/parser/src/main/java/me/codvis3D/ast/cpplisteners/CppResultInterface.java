package me.codvis.ast;

import org.json.JSONObject;

public interface CppResultInterface{

	// function for getting parse result outside of our listeners
    public JSONObject getParsedCode();
 }