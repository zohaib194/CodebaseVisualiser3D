package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONObject;

import me.codvis.ast.ClassModel;
import me.codvis.ast.FunctionModel;
//import me.codvis.ast.VariableModel;

public class AccessSpecifierModel extends Model {
	private String name;
	List<ClassModel> classes;
	List<FunctionModel> functions;
	// List<VariableModel> variables;

	AccessSpecifierModel() {
		this.name = "";
		this.publicData = new ArrayList<>();
		this.privateData = new ArrayList<>();
		this.protectedData = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public List<String> getFunctions() {
		return this.functions;
	}

	/*
	 * public List<String> getVariables() { return this.variables; }
	 */

	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("classes", this.classes);
		parsedCode.put("functions", this.functions);
		// parsedCode.put("variables", this.variables);
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
			this.addCall((ClassModel) data);
		} else if (data instanceof FunctionModel) {
			this.addCall((FunctionModel) data);
		} /*
			 * else if (data instanceof VariableModel) { this.addCall((VariableModel) data);
			 * }
			 */ else {
			System.out.println("Error adding data in model");
			System.exit(1);
		}

	}
}