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
	private String namespace;
	private int lineStart;
	private int lineEnd;
	private List<String> calls;

	/**
	 * Constructs the object, setting the function name.
	 *
	 * @param      name  The name
	 */
	FunctionModel(String name){
		this.name = name;
		this.calls = new ArrayList<>();
	}

	/**
	 * Sets the namespace.
	 *
	 * @param      namespace  The namespace
	 */
	public void setNamespace(String namespace){
		this.namespace = namespace;
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
	 * Gets the namespace.
	 *
	 * @return     The namespace.
	 */
	public String getNamespace(){
		return this.namespace;
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
	 * Adds a call.
	 *
	 * @param      functionCall  The function call
	 */
	public void addCall(String functionCall){
		this.calls.add(functionCall);
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
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){

		if (data instanceof String){
			this.addCall((String) data);
		} else {
			System.out.println("Error adding data in model");
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
		parsedCode.put("start_line", this.lineStart);
		parsedCode.put("end_line", this.lineEnd);
		parsedCode.put("calls", this.calls);
		return parsedCode;
	}
}