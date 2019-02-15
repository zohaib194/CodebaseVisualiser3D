package me.codvis.ast;

import org.json.JSONObject;

public class NamespaceModel extends Model {
	private String name;

	NamespaceModel(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		return parsedCode;
	}
}