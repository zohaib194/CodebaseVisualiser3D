package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

public class FileModel {
	private String fileName;
	private List<FunctionModel> functions;

	FileModel(String fileName){
		this.fileName = fileName;
		this.functions = new ArrayList<>();
	}

	public void addFunction(FunctionModel function){
		this.functions.add(function);
	}

	public void setFunctions(List<FunctionModel> functions){
		this.functions = functions;
	}

	public List<FunctionModel> getFunctions(){
		return this.functions;
	}

	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("file", this.fileName);

		if (functions.size() > 0) {
			List<JSONObject> parsedFunctions = new ArrayList<>();

			for (FunctionModel function : this.functions ) {
				JSONObject parsedFunction = new JSONObject();
				parsedFunction.put("function", function.getParsedCode());
				parsedFunctions.add(parsedFunction);
			}

			parsedCode.put("functions", parsedFunctions);
		}

		return parsedCode;
	}

}