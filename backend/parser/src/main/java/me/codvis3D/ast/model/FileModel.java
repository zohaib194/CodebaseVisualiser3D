package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONObject;

/**
 * Class for file model.
 */
public class FileModel extends Model {
	private String fileName;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;
	private List<VariableModel> variables;

	/**
	 * Constructs the object, setting the filename.
	 *
	 * @param fileName The file name
	 */
	FileModel(String fileName) {
		this.fileName = fileName;
		this.functions = new ArrayList<>();
		this.namespaces = new ArrayList<>();
		this.usingNamespaces = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	/**
	 * Gets the filename.
	 *
	 * @return The filename.
	 */
	public String getFilename() {
		return this.fileName;
	}

	/**
	 * Gets the functions.
	 *
	 * @return The functions.
	 */
	public List<FunctionModel> getFunctions() {
		return this.functions;
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return The namespaces.
	 */
	public List<NamespaceModel> getNamespaces() {
		return this.namespaces;
	}

	/**
	 * Gets the using namespaces.
	 *
	 * @return The used namespaces.
	 */
	public List<UsingNamespaceModel> getUsingNamespaces() {
		return this.usingNamespaces;
	}

	/**
	 * Gets the using variables.
	 *
	 * @return The used variables.
	 */
	public List<VariableModel> getVariables() {
		return this.variables;
	}

	/**
	 * Sets the functions.
	 *
	 * @param functions The functions
	 */
	public void setFunctions(List<FunctionModel> functions) {
		this.functions = functions;
	}

	/**
	 * Sets the namespaces.
	 *
	 * @param namespaces The namespaces
	 */
	public void setNamespaces(List<NamespaceModel> namespaces) {
		this.namespaces = namespaces;
	}

	/**
	 * Sets the usingNamespaces.
	 *
	 * @param usingNamespaces The usingNamespaces
	 */
	public void setUsingNamespaces(List<NamespaceModel> usingNamespaces) {
		this.usingNamespaces = usingNamespaces;
	}

	/**
	 * Sets the variables.
	 *
	 * @param variables The variables
	 */
	public void setVariables(List<VariableModel> variables) {
		this.variables = variables;
	}

	/**
	 * Adds a function.
	 *
	 * @param function The function
	 */
	public void addFunction(FunctionModel function) {
		this.functions.add(function);
	}

	/**
	 * Adds a namespace.
	 *
	 * @param namespace The namespace
	 */
	public void addNamespace(NamespaceModel namespace) {
		this.namespaces.add(namespace);
	}

	/**
	 * Adds an using namespace.
	 *
	 * @param namespace The namespace
	 */
	public void addUsingNamespace(UsingNamespaceModel usingNamespace) {
		this.usingNamespaces.add(usingNamespace);
	}

	/**
	 * Adds a variable.
	 *
	 * @param variable The variable
	 */
	public void addVariable(VariableModel variable) {
		this.variables.add(variable);
	}

	/**
	 * Adds the data in model.
	 *
	 * @param data The data
	 */
	@Override
	protected <T> void addDataInModel(T data) {

		if (data instanceof FunctionModel) {
			this.addFunction((FunctionModel) data);

		} else if (data instanceof NamespaceModel) {
			this.addNamespace((NamespaceModel) data);

		} else if (data instanceof UsingNamespaceModel) {
			this.addUsingNamespace((UsingNamespaceModel) data);

		} else if (data instanceof VariableModel) {
			this.addVariable((VariableModel) data);

		} else {
			System.out.println(data);
			System.out.println("Error adding data in file model");
			System.exit(1);
		}

	}

	/**
	 * Gets the parsed code as a JSONObject.
	 *
	 * @return The parsed code.
	 */
	@Override
	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("file_name", this.fileName);

		List<JSONObject> parsedFunctions = this.convertClassListJsonObjectList(this.functions, "function");
		if (parsedFunctions != null) {
			parsedCode.put("functions", parsedFunctions);
		}

		List<JSONObject> parsedNamespaces = this.convertClassListJsonObjectList(this.namespaces, "namespace");
		if (parsedNamespaces != null) {
			parsedCode.put("namespaces", parsedNamespaces);
		}

		List<JSONObject> parsedUsingNamespaces = this.convertClassListJsonObjectList(this.usingNamespaces, "namespace");
		if (parsedUsingNamespaces != null) {
			parsedCode.put("using_namespaces", parsedUsingNamespaces);
		}

		List<JSONObject> parsedVariables = this.convertClassListJsonObjectList(this.variables, "variable");
		if (parsedVariables != null) {
			parsedCode.put("variables", parsedVariables);
		}

		return parsedCode;
	}

}