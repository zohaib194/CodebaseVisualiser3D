package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

public class NamespaceModel extends Model {
	private String name;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;

	NamespaceModel(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setFunctions(List<FunctionModel> functions){
		this.functions = functions;
	}

	public void setNamespaces(List<NamespaceModel> namespaces){
		this.namespaces = namespaces;
	}

	public void setUsingNamespaces(List<UsingNamespaceModel> namespaces){
		this.usingNamespaces = namespaces;
	}

	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);

		List<JSONObject> parsedFunctions = this.convertClassListJsonObjectList(this.functions, "function");
		if (parsedFunctions != null) {
			parsedCode.put("functions", parsedFunctions);
		}

		List<JSONObject> parsedNamespaces = this.convertClassListJsonObjectList(this.namespaces, "namespace");
		if (parsedNamespaces != null) {
			parsedCode.put("namespaces", parsedNamespaces);
		}

		List<JSONObject> parsedUsingNamespaces = this.convertClassListJsonObjectList(this.usingNamespaces, "namespace");
		if (parsedUsingNamespaces != null) {
			parsedCode.put("using_namespaces", parsedUsingNamespaces);
		}

		return parsedCode;
	}
}