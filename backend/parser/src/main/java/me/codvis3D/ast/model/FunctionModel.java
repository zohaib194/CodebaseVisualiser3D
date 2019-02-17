package me.codvis.ast;

import org.json.JSONObject;

public class FunctionModel extends Model{
	private String name;
	private String body;
	private String namespace;

	FunctionModel(String name){
		this.name = name;
	}

	public void setNamespace(String namespace){
		this.namespace = namespace;
	}


	public String getName(){
		return this.name;
	}

	public void setBody(String body){
		this.body = body;
	}

	public String getBody(){
		return this.body;
	}
	
	public String getNamespace(){
		return this.namespace;
	}

	@Override
	public JSONObject getParsedCode(){	
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("body", this.body);
		return parsedCode;
	}

}