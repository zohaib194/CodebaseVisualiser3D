package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONObject;
import org.json.JSONArray;

import me.codvis.ast.ClassModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.VariableModel;

public class AccessSpecifierModel extends Model {
	private String name;
	List<ClassModel> classes;
	List<FunctionModel> functions;
	List<VariableModel> variables;

	AccessSpecifierModel(String name) {
		this.name = name;
		this.classes = new ArrayList<>();
		this.functions = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClassModel> getClasses() {
		return this.classes;
	}

	public List<FunctionModel> getFunctions() {
		return this.functions;
	}

	public List<VariableModel> getVariables() {
		return this.variables;
	}

	public void addClass(ClassModel data) {
		this.classes.add(data);
	}

	public void addFunction(FunctionModel data) {
		this.functions.add(data);
	}

	public void addVariable(VariableModel data) {
		this.variables.add(data);
	}

	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);

		JSONArray parsedClasses = this.convertClassListJsonObjectList(this.classes);
		if (parsedClasses != null) {
			parsedCode.put("classes", parsedClasses);
		}

		JSONArray parsedFunctions = this.convertClassListJsonObjectList(this.functions);
		if (parsedFunctions != null) {
			parsedCode.put("functions", parsedFunctions);
		}

		JSONArray parsedVariables = this.convertClassListJsonObjectList(this.variables);
		if (parsedVariables != null) {
			parsedCode.put("variables", parsedVariables);
		}

		return parsedCode;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		if (data instanceof ClassModel) {
			this.addClass((ClassModel) data);
		} else if (data instanceof FunctionModel) {
			this.addFunction((FunctionModel) data);
		} else if (data instanceof VariableModel) {
			this.addVariable((VariableModel) data);
		} else {
			System.err.println("Error adding data in accessspecifier model");
			System.exit(1);
		}

	}
}