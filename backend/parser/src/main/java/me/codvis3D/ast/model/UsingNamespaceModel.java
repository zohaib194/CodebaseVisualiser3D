package me.codvis.ast;

import org.json.JSONObject;

public class UsingNamespaceModel extends Model {
	private String name;
	private int lineNr;

	UsingNamespaceModel(String name, int lineNr){
		this.name = name;
		this.lineNr = lineNr;
	}

	public String getName(){
		return this.name;
	}

	public int getLineNr(){
		return this.lineNr;
	}

	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("line_number", this.lineNr);

		return parsedCode;
	}
}