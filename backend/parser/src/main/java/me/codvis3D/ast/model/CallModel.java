package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class for call model.
 */
public class CallModel extends Model{
	private String identifier;
	private List<ScopeModel> scopes;

	/**
	 * Constructs the object.
	 */
	CallModel(){
		this.identifier = "";
		this.scopes = new ArrayList<>();
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
	 * Gets the scopes.
	 *
	 * @return     The scopes.
	 */
	public List<ScopeModel> getScopes(){
		return this.scopes;
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
	 * Sets the scopes.
	 *
	 * @param      scopes  The scopes
	 */
	public void setScopes(List<ScopeModel> scopes){
		this.scopes = scopes;
	}

	/**
	 * Adds a scope.
	 *
	 * @param      scope  The scope
	 */
	public void addScope(ScopeModel scope){
		this.scopes.add(scope);
	}

	/**
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){
		System.err.println("Error adding data in call model");
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

		JSONArray parsedScopes = this.convertClassListJsonObjectList(this.scopes);
		if (parsedScopes != null) {
			parsedCode.put("scopes", parsedScopes);
		}

		return parsedCode;
	}

}