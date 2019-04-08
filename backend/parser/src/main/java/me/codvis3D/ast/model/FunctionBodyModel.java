package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class for abstracting a code function.
 */
public class FunctionBodyModel extends Model {
	private List<CallModel> calls;
	private List<VariableModel> variables;

	/**
	 * Constructs the object.
	 */
	FunctionBodyModel() {
		this.calls = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	/**
	 * Adds a call.
	 *
	 * @param      call  The call
	 */
	private void addCall(CallModel call) {
		this.calls.add(call);
	}

	/**
	 * Adds a variable.
	 *
	 * @param      variable  The variable
	 */
	public void addVariable(VariableModel variable) {
		this.variables.add(variable);
	}

	/**
	 * Sets the variables.
	 *
	 * @param      variables  The variables
	 */
	public void setVariables(List<VariableModel> variables) {
		this.variables = variables;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		if (data instanceof CallModel) {
			CallModel call = ((CallModel) data);
			if(call.getIdentifier() == "" && call.getScopeIdentifier().size() == 0){
				return ;
			}

			this.addCall((CallModel) data);
		} else if (data instanceof VariableModel) {
			this.addVariable((VariableModel) data);

		} else {

			System.err.println("Error adding data in function body model: " + data.getClass());
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

		JSONArray parsedCalls = this.convertClassListJsonObjectList(this.calls);
		if (parsedCalls != null) {
			parsedCode.put("calls", parsedCalls);
		}

		JSONArray parsedVariables = this.convertClassListJsonObjectList(this.variables);
		if (parsedVariables != null) {
			parsedCode.put("variables", parsedVariables);
		}

		return parsedCode;
	}
}