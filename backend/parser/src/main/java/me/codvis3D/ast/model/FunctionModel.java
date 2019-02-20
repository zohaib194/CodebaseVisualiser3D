package me.codvis.ast;

import org.json.JSONObject;

/**
 * Class for abstracting a code function.
 */
public class FunctionModel extends Model{
	private String name;
	private String namespace;

	/**
	 * Constructs the object, setting the function name.
	 *
	 * @param      name  The name
	 */
	FunctionModel(String name){
		this.name = name;
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
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
	@Override
	public JSONObject getParsedCode(){	
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		return parsedCode;
	}
}