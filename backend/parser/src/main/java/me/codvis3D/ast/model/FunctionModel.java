package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.json.JSONObject;

import me.codvis.ast.FunctionBodyModel;
import me.codvis.ast.VariableModel;

/**
 * Class for abstracting a code function.
 */
public class FunctionModel extends Model {
	private String name;
	private String declaratorId;
	private String scope;
	private int lineStart;
	private int lineEnd;
	private List<VariableModel> parameters;
	private FunctionBodyModel functionBody;

	FunctionModel(String name) {
		this.name = name;
		this.parameters = new ArrayList<>();
		this.functionBody = null;
	}

	/**
	 * Constructs the object, setting the function name.
	 *
	 * @param name The name
	 */
	FunctionModel(String name, String declarator) {
		this.name = name;
		this.declaratorId = declarator;
		this.scope = "";
		this.parameters = new ArrayList<>();
		this.functionBody = null;
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
	 * Gets the scope.
	 *
	 * @return The scope.
	 */
	public String getScope() {
		return this.scope;
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
	 * Gets the line end.
	 *
	 * @return The line end.
	 */
	public int getLineEnd() {
		return this.lineEnd;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return The parameters.
	 */
	public List<VariableModel> getParameters() {
		return this.parameters;
	}

	/**
	 * Gets the function body.
	 *
	 * @return The function body.
	 */
	public FunctionBodyModel getFunctionBody() {
		return this.functionBody;
	}

	/**
	 * Sets the declarator identifier.
	 *
	 * @param declaratorId The declarator identifier
	 */
	public void setDeclaratorId(String declaratorId) {
		this.declaratorId = declaratorId;
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
	 * Sets the line start.
	 *
	 * @param lineStart The line start
	 */
	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
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
	 * Sets the parameters.
	 *
	 * @param parameters The parameters
	 */
	public void setParameters(List<VariableModel> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets the function body.
	 *
	 * @param body The body
	 */
	public void setFunctionBody(FunctionBodyModel body) {
		this.functionBody = body;
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
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {
		if (data instanceof FunctionBodyModel) {
			this.setFunctionBody((FunctionBodyModel) data);
		} else if (data instanceof VariableModel) {
			this.addParameter((VariableModel) data);
		} else {
			System.out.println("Error adding data in function model " + data.getClass());
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

		List<JSONObject> parsedParameters = this.convertClassListJsonObjectList(this.parameters, "parameter");
		if (parsedParameters != null) {
			parsedCode.put("parameters", parsedParameters);
		}

		if (this.functionBody != null) {
			parsedCode.put("function_body", this.functionBody.getParsedCode());
		}

		return parsedCode;
	}
}