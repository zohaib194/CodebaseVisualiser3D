package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONObject;

/**
 * Class for call model.
 */
public class ScopeModel extends Model{
	private String identifier;
	private String type;

	/**
	 * Constructs the object.
	 */
	ScopeModel(String identifier, String type){
		this.identifier = identifier;
		this.type = type;
	}

	/**
	 * Gets the identifier.
	 *
	 * @return     The identifier.
	 */
	public String getIdentifier(){
		return this.identifier;
	}

	/**
	 * Gets the type.
	 *
	 * @return     The type.
	 */
	public String getType(){
		return this.type;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param      identifier  The identifier
	 */
	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}

	/**
	 * Sets the type.
	 *
	 * @param      type  The type
	 */
	public void setType(String type){
		this.type = type;
	}
	/**
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){
		System.err.println("Error adding data in scope model");
		System.exit(1);

	}

	/**
	 * Gets the parsed code as a JSONObject.
	 *
	 * @return     The parsed code.
	 */
	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("identifier", this.identifier);

		parsedCode.put("type", this.type);
		return parsedCode;
	}

}