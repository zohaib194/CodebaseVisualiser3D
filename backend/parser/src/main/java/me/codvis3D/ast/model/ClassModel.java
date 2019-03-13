package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.json.JSONObject;

public class ClassModel extends Model {
	private String name;
	List<String> publicData;

	ClassModel() {
		this.name = "";
		this.publicData = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPublicData(String data) {
		this.publicData.add(data);
	}

	public void setPublicData(List<String> data) {
		this.publicData = data;
	}

	public List<String> getPublicData() {
		return this.publicData;
	}

	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);
		parsedCode.put("public", this.publicData);
		return parsedCode;
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		/*
		 * if (data instanceof String) { this.addCall((String) data); } else {
		 * System.out.println("Error adding data in model"); System.exit(1); }
		 */

	}
}