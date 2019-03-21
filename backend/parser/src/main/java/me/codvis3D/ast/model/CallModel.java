package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONObject;

/**
 * Class for call model.
 */
public class CallModel extends Model{
	private String identifier;
	private List<String> scopeIdentifier;

	/**
	 * Constructs the object.
	 */
	CallModel(){
		this.identifier = "";
		this.scopeIdentifier = new ArrayList<>();
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
	 * Gets the scope identifier.
	 *
	 * @return     The scope identifier.
	 */
	public List<String> getScopeIdentifier(){
		return this.scopeIdentifier;
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
	 * Sets the scope identifier.
	 *
	 * @param      scopeIdentifier  The scope identifier
	 */
	public void setScopeIdentifier(List<String> scopeIdentifier){
		this.scopeIdentifier = scopeIdentifier;
	}

	/**
	 * Adds a scope identifier.
	 *
	 * @param      identifier  The identifier
	 */
	public void addScopeIdentifier(String identifier){
		this.scopeIdentifier.add(identifier);
	}

	/**
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){
		System.out.println("Error adding data in call model");
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

		parsedCode.put("Scope", this.scopeIdentifier);
		return parsedCode;
	}

}