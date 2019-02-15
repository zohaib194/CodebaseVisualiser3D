package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

public class ClassModel extends Model{
	private String name;
	List<String> publicData;
	List<String> privateData;
	List<String> protectedData;


	ClassModel(String name){
		this.name = name;
		this.publicData = new ArrayList<>();
		this.privateData = new ArrayList<>();
		this.protectedData = new ArrayList<>();
	}

	public String getName(){
		return this.name;
	}

	public void addPublicData(String data){
		this.publicData.add(data);
	}

	public void addPrivateData(String data){
		this.privateData.add(data);
	}

	public void addProtectedData(String data){
		this.protectedData.add(data);
	}
	
	public void setPublicData(List<String> data){
		this.publicData = data;
	}

	public void setPrivateData(List<String> data){
		this.privateData = data;
	}

	public void setProtectedData(List<String> data){
		this.protectedData = data;
	}

	public List<String> getPublicData(){
		return this.publicData;
	}

	public List<String> getPrivateData(){
		return this.privateData;
	}

	public List<String> getProtectedData(){
		return this.protectedData;
	}

	public JSONObject getParsedCode(){	
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("public", this.publicData);
		parsedCode.put("private", this.privateData);
		parsedCode.put("protected", this.protectedData);
		//parsedCode.put("body", this.body);
		return parsedCode;
	}

}