package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.json.JSONObject;

/**
 * Class for abstracting a code function.
 */
public class FunctionModel extends Model{
	private String name;
	private String declaratorId;
	private String scope;
	private int lineStart;
	private int lineEnd;
	private List<VariableModel> parameters;
	private FunctionBodyModel functionBody;

	FunctionModel(String name){
		this.name = name;
		this.parameters = new ArrayList<>();
		this.functionBody = new FunctionBodyModel();
	}

	/**
	 * Constructs the object, setting the function name.
	 *
	 * @param      name  The name
	 */
	FunctionModel(String name, String declarator){
		this.name = name;
		this.scope = "";
		this.declaratorId = declarator;
		this.parameters = new ArrayList<>();
		this.functionBody = new FunctionBodyModel();
	}

	/**
	 * Gets the scope.
	 *
	 * @return     The scope.
	 */
	public String getScope(){
		return this.scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param      scope  The scope
	 */
	public void setScope(String scope){
		this.scope = scope;
	}

	/**
	 * Sets the declarator identifier.
	 *
	 * @param      declaratorId  The declarator identifier
	 */
	public void setDeclaratorId(String declaratorId){
		this.declaratorId = declaratorId;
	}

	/**
	 * Gets the name.
	 *
	 * @return     The name.
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Sets the line start.
	 *
	 * @param      lineStart  The line start
	 */
	public void setLineStart(int lineStart){
		this.lineStart = lineStart;
	}

	/**
	 * Gets the line start.
	 *
	 * @return     The line start.
	 */
	public int getLineStart(){
		return this.lineStart;
	}

	/**
	 * Sets the line end.
	 *
	 * @param      lineEnd  The line end
	 */
	public void setLineEnd(int lineEnd){
		this.lineEnd = lineEnd;
	}


	/**
	 * Gets the line end.
	 *
	 * @return     The line end.
	 */
	public int getLineEnd(){
		return this.lineEnd;
	}

	/**
	 * Adds a parameter.
	 *
	 * @param      parameter  The parameter
	 */
	public void addParameter(VariableModel parameter){
		this.parameters.add(parameter);
	}

	/**
	 * Sets the parameters.
	 *
	 * @param      parameters  The parameters
	 */
	public void setParameters(List<VariableModel> parameters){
		this.parameters = parameters;
	}

	/**
	 * Sets the function body.
	 *
	 * @param      body  The body
	 */
	public void setFunctionBody(FunctionBodyModel body){
		this.functionBody = body;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){
		if(data instanceof FunctionBodyModel){
			this.setFunctionBody((FunctionBodyModel) data);
		} else {
			System.out.println("Error adding data in function model " + data.getClass());
			System.exit(1);
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
	@Override
	public JSONObject getParsedCode(){
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

		if(this.functionBody != null){
			parsedCode.put("function_body", this.functionBody.getParsedCode());
		}

		return parsedCode;
	}
}