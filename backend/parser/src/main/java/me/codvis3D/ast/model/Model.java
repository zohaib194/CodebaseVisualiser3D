package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

public class Model {

	protected <T extends Model> List<JSONObject> convertClassListJsonObjectList(List<T> objectList, String objectNames){
		if (objectList.size() > 0) {
			List<JSONObject> parsedObjects = new ArrayList<>();

			for (T object : objectList) {
				JSONObject parsedObject = new JSONObject();
				parsedObject.put(objectNames, object.getParsedCode());
				parsedObjects.add(parsedObject);
			}

			return parsedObjects;
		}
		return null;
	}

    public JSONObject getParsedCode() {
    	return null;
    }
}