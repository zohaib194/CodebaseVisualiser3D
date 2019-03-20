package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONObject;
import org.json.JSONArray;

import me.codvis.ast.ClassModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.VariableModel;

public class DeclaratorListModel extends Model {
	private String type;
	List<String> variables;
	List<String> functions;

	DeclaratorListModel() {
		this.type = "";
		this.functions = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getFunctions() {
		return this.functions;
	}

	public List<String> getVariables() {
		return this.variables;
	}

	public void addFunction(String data) {
		this.functions.add(data);
	}

	public void addVariable(String data) {
		this.variables.add(data);
	}

	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		return parsedCode;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		if (data instanceof String) {
			this.setType((String) data);
		} else {
			System.out.println("Error adding data in DeclaratorList Model " + data.getClass().getName());
			System.exit(1);
		}

	}
}