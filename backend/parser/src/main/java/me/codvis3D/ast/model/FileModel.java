package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import me.codvis.ast.FunctionModel;
import me.codvis.ast.NamespaceModel;
import me.codvis.ast.UsingNamespaceModel;
import me.codvis.ast.VariableModel;
import me.codvis.ast.ClassModel;

/**
 * Class for file model.
 */
public class FileModel extends Model {
	private String fileName;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;
	private List<String> includes;
	private List<VariableModel> variables;
	private List<ClassModel> classes;

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
		this.includes = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.classes = new ArrayList<>();
	}

	/**
	 * Adds a function.
	 *
	 * @param      function  The function
	 */
	public void addFunction(FunctionModel function) {
		this.functions.add(function);
	}

	/**
	 * Adds a namespace.
	 *
	 * @param      namespace  The namespace
	 */
	public void addNamespace(NamespaceModel namespace) {
		this.namespaces.add(namespace);
	}

	/**
	 * Adds an using namespace.
	 *
	 * @param      namespace  The namespace
	 */
	public void addUsingNamespace(UsingNamespaceModel namespace) {
		this.usingNamespaces.add(namespace);
	}

	/**
	 * Adds a variable.
	 *
	 * @param      variable  The variable
	 */
	public void addVariable(VariableModel variable) {
		this.variables.add(variable);
	}

	/**
	 * Adds a class.
	 *
	 * @param      clazz  The clazz
	 */
	public void addClass(ClassModel clazz) {
		this.classes.add(clazz);
	}

	/**
	 * Adds an include.
	 *
	 * @param      include  The include
	 */
	public void addInclude(String include){
		this.includes.add(include);
	}

	/**
	 * Gets the variables.
	 *
	 * @return     The variables.
	 */
	public List<VariableModel> getVariables() {
		return this.variables;
	}

	/**
	 * Sets the functions.
	 *
	 * @param      functions  The functions
	 */
	public void setFunctions(List<FunctionModel> functions) {
		this.functions = functions;
	}

	/**
	 * Sets the classes.
	 *
	 * @param      classes  The classes
	 */
	public void setClasses(List<ClassModel> classes) {
		this.classes = classes;
	}

	/**
	 * Gets the filename.
	 *
	 * @return     The filename.
	 */
	public String getFilename() {
		return this.fileName;
	}

	/**
	 * Gets the functions.
	 *
	 * @return     The functions.
	 */
	public List<FunctionModel> getFunctions() {
		return this.functions;
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return     The namespaces.
	 */
	public List<NamespaceModel> getNamespaces() {
		return this.namespaces;
	}

	/**
	 * Gets the using namespaces.
	 *
	 * @return     The using namespaces.
	 */
	public List<UsingNamespaceModel> getUsingNamespaces() {
		return this.usingNamespaces;
	}

	/**
	 * Gets the classes.
	 *
	 * @return     The classes.
	 */
	public List<ClassModel> getClasses() {
		return this.classes;
	}

	/**
	 * Adds a data in model.
	 *
	 * @param      data  The data
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

		} else if (data instanceof ClassModel) {
			this.addClass((ClassModel) data);

		} else if (data instanceof String){
			this.addInclude((String) data);
		} else{
			System.err.println("Error adding data in file model");
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

		JSONArray parsedFunctions = this.convertClassListJsonObjectList(this.functions);
		if (parsedFunctions != null) {
			parsedCode.put("functions", parsedFunctions);
		}

		JSONArray parsedNamespaces = this.convertClassListJsonObjectList(this.namespaces);
		if (parsedNamespaces != null) {
			parsedCode.put("namespaces", parsedNamespaces);
		}

		JSONArray parsedUsingNamespaces = this.convertClassListJsonObjectList(this.usingNamespaces);
		if (parsedUsingNamespaces != null) {
			parsedCode.put("using_namespaces", parsedUsingNamespaces);
		}

		JSONArray parsedVariables = this.convertClassListJsonObjectList(this.variables);
		if (parsedVariables != null) {
			parsedCode.put("variables", parsedVariables);
		}

		JSONArray parsedClasses = this.convertClassListJsonObjectList(this.classes);
		if (parsedClasses != null) {
			parsedCode.put("classes", parsedClasses);
		}

		if (this.includes.size() > 0 ) {
			parsedCode.put("includes", this.includes);
		}
		return parsedCode;
	}

}