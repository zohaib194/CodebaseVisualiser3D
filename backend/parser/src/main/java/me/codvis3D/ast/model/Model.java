package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class for abstracting a code unit.
 */
public class Model {

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	protected <T> void addDataInModel(T data) {
		System.out.println("addDataInModel not implemented");
		System.exit(1);
	}

	/**
	 * Converts a list of objects extending model, to a list of JSON objects giving
	 * same JSON id to each object.
	 *
	 * @param objectList  The object list to convert to JSONObject.
	 * @param objectNames The object names to give as id in JSON to give to each
	 *                    object.
	 *
	 * @return List of given objects as JSONObject.
	 */
	protected <T extends Model> JSONArray convertClassListJsonObjectList(List<T> objectList) {
		if (objectList.size() > 0) {
			JSONArray jsonArray = new JSONArray();
			for (T obj : objectList) {
				jsonArray.put(obj.getParsedCode());
			}
			return jsonArray;
		}
		return null;
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return The parsed code as JSONObject.
	 */
	public JSONObject getParsedCode() {
		return null;
	}
}