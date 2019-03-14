package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class for abstracting a code function.
 */
public class FunctionModel extends Model {
	private String name;
	private String declaratorId;
	private String scope;
	private int lineStart;
	private int lineEnd;
	private List<String> calls;
	private List<VariableModel> variables;
	private List<VariableModel> parameters;

	FunctionModel(String name) {
		this.name = name;
		this.calls = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.parameters = new ArrayList<>();
	}

	/**
	 * Constructs the object, setting the function name.
	 *
	 * @param name The name
	 */
	FunctionModel(String name, String declarator) {
		this.name = name;
		this.scope = "";
		this.declaratorId = declarator;
		this.calls = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.parameters = new ArrayList<>();
	}

	/**
	 * Gets the scope.
	 *
	 * @return The scope.
	 */
	public String getScope() {
		return this.scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope The scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the line start.
	 *
	 * @param lineStart The line start
	 */
	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	/**
	 * Gets the line start.
	 *
	 * @return The line start.
	 */
	public int getLineStart() {
		return this.lineStart;
	}

	/**
	 * Sets the line end.
	 *
	 * @param lineEnd The line end
	 */
	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}

	/**
	 * Adds a call.
	 *
	 * @param functionCall The function call
	 */
	public void addCall(String functionCall) {
		this.calls.add(functionCall);
	}

	/**
	 * Gets the line end.
	 *
	 * @return The line end.
	 */
	public int getLineEnd() {
		return this.lineEnd;
	}

	/**
	 * Adds a variable.
	 *
	 * @param variable The variable
	 */
	public void addVariable(VariableModel variable) {
		this.variables.add(variable);
	}

	/**
	 * Adds a parameter.
	 *
	 * @param parameter The parameter
	 */
	public void addParameter(VariableModel parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters The parameters
	 */
	public void setParameters(List<VariableModel> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		if (data instanceof String) {
			this.addCall((String) data);
		} else if (data instanceof VariableModel) {
			this.addVariable((VariableModel) data);
		} else {
			System.out.println("Error adding data in function model: " + data.getClass());
			System.exit(1);
		}

	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return The parsed code.
	 */
	@Override
	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("declrator_id", this.declaratorId);
		parsedCode.put("scope", this.scope);
		parsedCode.put("start_line", this.lineStart);
		parsedCode.put("end_line", this.lineEnd);
		parsedCode.put("calls", this.calls);

		/* List<JSONObject> */JSONArray parsedVariables = this.convertClassListJsonObjectList(this.variables);
		if (parsedVariables != null) {
			parsedCode.put("variables", parsedVariables);
		}

		/* List<JSONObject> */JSONArray parsedParameters = this.convertClassListJsonObjectList(this.parameters);
		if (parsedParameters != null) {
			parsedCode.put("parameters", parsedParameters);
		}

		return parsedCode;
	}
}