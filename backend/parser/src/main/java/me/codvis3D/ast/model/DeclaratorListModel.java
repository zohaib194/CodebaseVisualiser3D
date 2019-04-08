package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONObject;
import org.json.JSONArray;

import me.codvis.ast.ClassModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.VariableModel;

/**
 * Class for declarator list model.
 */
public class DeclaratorListModel extends Model {
	private String type;
	List<String> variables;
	List<String> functions;

	/**
	 * Constructs the object.
	 */
	DeclaratorListModel() {
		this.type = "";
		this.functions = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	/**
	 * Gets the type.
	 *
	 * @return     The type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 *
	 * @param      type  The type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the functions.
	 *
	 * @return     The functions.
	 */
	public List<String> getFunctions() {
		return this.functions;
	}

	/**
	 * Gets the variables.
	 *
	 * @return     The variables.
	 */
	public List<String> getVariables() {
		return this.variables;
	}

	/**
	 * Adds a function.
	 *
	 * @param      data  The data
	 */
	public void addFunction(String data) {
		this.functions.add(data);
	}

	/**
	 * Adds a variable.
	 *
	 * @param      data  The data
	 */
	public void addVariable(String data) {
		this.variables.add(data);
	}

	/**
	 * Gets the parsed code.
	 *
	 * @return     The parsed code.
	 */
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
			System.err.println("Error adding data in DeclaratorList Model " + data.getClass().getName());
			System.exit(1);
		}

	}
}