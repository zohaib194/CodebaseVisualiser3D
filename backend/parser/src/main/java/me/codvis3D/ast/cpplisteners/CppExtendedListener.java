package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;

import org.json.JSONObject;

/**
 * Class for extending CPP14BaseListener and implementing result interface.
 * Class for extending Java9BaseListener and implementing reuslt interface.
 * 
 */
public class CppExtendedListener extends CPP14BaseListener implements CppResultInterface{

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {  
    	return null;
    }
 }