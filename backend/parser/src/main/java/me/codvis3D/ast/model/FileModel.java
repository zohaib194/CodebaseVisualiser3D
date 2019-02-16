package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

public class FileModel extends Model{
	private String fileName;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;

	FileModel(String fileName){
		this.fileName = fileName;
		this.functions = new ArrayList<>();
		this.namespaces = new ArrayList<>();
		this.usingNamespaces = new ArrayList<>();
	}

	public void addFunction(FunctionModel function){
		this.functions.add(function);
	}

	public void addNamespace(NamespaceModel namespace){
		this.namespaces.add(namespace);
	}

	public void addUsingNamespace(UsingNamespaceModel namespace){
		this.usingNamespaces.add(namespace);
	}

	public void setFunctions(List<FunctionModel> functions){
		this.functions = functions;
	}

	public String getFilename(){
		return this.fileName;
	}

	public List<FunctionModel> getFunctions(){
		return this.functions;
	}

	public List<NamespaceModel> getNamespaces(){
		return this.namespaces;
	}

	public List<UsingNamespaceModel> getUsingNamespaces(){
		return this.usingNamespaces;
	}

	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("file_name", this.fileName);

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