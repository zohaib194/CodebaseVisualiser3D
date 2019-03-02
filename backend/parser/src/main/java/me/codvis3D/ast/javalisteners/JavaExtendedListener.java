package me.codvis.ast;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import java.util.Stack;

import org.json.JSONObject;

/**
 * Class for exstending Java9BaseListener and implementing reuslt interface.
 */
public class JavaExtendedListener extends Java9BaseListener implements JavaResultInterface{

	protected Stack<ModelIdentifier> scopeStack;

	/**
	 * Constructs the object.
	 */
	public JavaExtendedListener(){
		scopeStack = new Stack<ModelIdentifier>();
	}

	/**
	 * Enters a scope
	 *
	 * @param      scope  The scope
	 */
	protected void enterScope(ModelIdentifier scope){
		this.scopeStack.push(scope);
	}

	/**
	 * Exits last entered scope
	 *
	 * @return     the scope being exited
	 */
	protected ModelIdentifier exitScope(){
		return this.scopeStack.pop();
	}

	/**
	 * Gets the parsed code as JSON, expected to be overwritten by exstending classes.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {  
    	return null;
    }
 }