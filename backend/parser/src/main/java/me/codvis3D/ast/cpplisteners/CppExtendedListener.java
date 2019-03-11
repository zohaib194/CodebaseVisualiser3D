package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import java.util.Stack;

import org.json.JSONObject;

/**
 * Class for extending CPP14BaseListener and implementing result interface.
 */
public class CppExtendedListener extends CPP14BaseListener implements CppResultInterface{

	protected Stack<Model> scopeStack;

	/**
	 * Constructs the object.
	 */
	public CppExtendedListener(){
		scopeStack = new Stack<Model>();
	}

	/**
	 * Enters a scope
	 *
	 * @param      scope  The scope
	 */
	protected void enterScope(Model scope){
		this.scopeStack.push(scope);
	}

	/**
	 * Exits last scope
	 *
	 * @return     The scope being exited
	 */
	protected Model exitScope(){
		return this.scopeStack.pop();
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {  
    	return null;
    }
 }