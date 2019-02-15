package me.codvis.ast;


public class FunctionModel {
	private String name;

	FunctionModel(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

}