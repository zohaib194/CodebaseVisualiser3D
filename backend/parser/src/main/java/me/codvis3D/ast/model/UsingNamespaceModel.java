package me.codvis.ast;

import java.util.Stack;

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
	 * Adds a model in current scope.
	 *
	 * @param      model       The model
	 * @param      scopeStack  The scope stack identifying current scope position
	 *
	 * @return     index in list for its type where model was added for current scope. If not a list it will return 0.
	 */
	@Override
	protected <T> void addDataInModel(T model){

		System.out.println("Error using namespace is currently not a scope as scopeStack indicated");
		System.exit(1);		
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