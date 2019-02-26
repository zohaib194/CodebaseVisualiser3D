package me.codvis.ast;

import org.json.JSONObject;

/**
 * Class for abstracting code for used namespace.
 */
public class UsingNamespaceModel extends Model {
	private String name;
	private int lineNr;

	/**
	 * Constructs the object, setting name and line number.
	 *
	 * @param      name    The name of namespace being used
	 * @param      lineNr  The linenumber where neamespace is opened
	 */
	UsingNamespaceModel(String name, int lineNr){
		this.name = name;
		this.lineNr = lineNr;
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
	 * Gets the line nr.
	 *
	 * @return     The line nr.
	 */
	public int getLineNr(){
		return this.lineNr;
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("line_number", this.lineNr);

		return parsedCode;
	}
}