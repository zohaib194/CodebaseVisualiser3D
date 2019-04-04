package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.json.JSONObject;

public class VariableListModel extends Model{
	private String type;
	private List<String> names;

	/**
	 * Constructs the object.
	 */
	VariableListModel(){
		this.type = "";
		this.names = new ArrayList<>();
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
	 * Sets the type.
	 *
	 * @param      type  The type
	 */
	public void setType(String type){
		this.type = type;
	}

	/**
	 * Gets the names.
	 *
	 * @return     The names.
	 */
	public List<String> getNames(){
		return this.names;
	}

	/**
	 * Sets the names.
	 *
	 * @param      names  The names
	 */
	public void setNames(List<String> names){
		this.names = names;
	}

	/**
	 * Adds a name.
	 *
	 * @param      name  The name
	 */
	public void addName(String name){
		this.names.add(name);
	}

	/**
	 * Concatenate type with modifiers.
	 *
	 * @param      modifier  The modifier
	 */
	public void applyModifierOnType(String modifier){
		this.type += modifier + " ";
	}

	/**
	 * Concatenate type with primitiv type.
	 *
	 * @param      type  The type
	 */
	public void applyUnnanTypeOnType(String type){
		this.type += type;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param      data  The data
	 */
	@Override
	protected <T> void addDataInModel(T data){
		System.err.println("Error adding data in variable list model " + data.getClass());
		System.exit(1);
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		return parsedCode;
	}
}