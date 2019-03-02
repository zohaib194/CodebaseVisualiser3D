package me.codvis.ast;

import org.json.JSONObject;

/**
 * Interface for java result interface.
 */
public interface JavaResultInterface{

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode();
 }