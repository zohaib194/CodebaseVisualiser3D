package me.codvis.ast;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.json.JSONObject;

/**
 * Class for exstending Java9BaseListener and implementing reuslt interface.
 */
public class JavaExtendedListener extends Java9BaseListener implements JavaResultInterface{

	/**
	 * Gets the parsed code as JSON, expected to be overwritten by exstending classes.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {  
    	return null;
    }
 }