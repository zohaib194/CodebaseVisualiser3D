package me.codvis.ast;

import org.json.JSONObject;

public interface JavaResultInterface{

	// function for getting parse result outside of our listeners
    public JSONObject getParsedCode();
 }