package me.codvis.ast;

import org.json.JSONObject;

public class FunctionModel {
	private String name;

	FunctionModel(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public JSONObject getParsedCode(){	
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		return parsedCode;
	}
}