package me.codvis.ast;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class for abstracting a code namespace.
 */
public class NamespaceModel extends Model {
	private String name;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;
	private List<VariableModel> variables;
	private List<ClassModel> classes;
	private List<String> includes;
	/**
	 * Constructs the namespace, setting the name.
	 *
	 * @param name The name
	 */
	public NamespaceModel(String name) {
		this.name = name;
		this.functions = new ArrayList<>();
		this.namespaces = new ArrayList<>();
		this.usingNamespaces = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.classes = new ArrayList<>();
		this.includes = new ArrayList<>();
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
	 * Adds an class.
	 *
	 * @param clazz The class
	 */
	public void addClass(ClassModel clazz) {
		this.classes.add(clazz);
	}

	/**
	 * Gets the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the functions.
	 *
	 * @return     The functions.
	 */
	public List<FunctionModel> getFunctions(){
		return this.functions;
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return     The namespaces.
	 */
	public List<NamespaceModel> getNamespaces(){
		return this.namespaces;
	}

	/**
	 * Gets the using namespaces.
	 *
	 * @return     The using namespaces.
	 */
	public List<UsingNamespaceModel> getUsingNamespaces(){
		return this.usingNamespaces;
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
	 * Sets the inner namespaces.
	 *
	 * @param namespaces The namespaces
	 */
	public void setNamespaces(List<NamespaceModel> namespaces) {
		this.namespaces = namespaces;
	}

	/**
	 * Sets the inner using namespaces.
	 *
	 * @param namespaces The namespaces
	 */
	public void setUsingNamespaces(List<UsingNamespaceModel> namespaces) {
		this.usingNamespaces = namespaces;
	}

	/**
	 * Sets the classes list.
	 *
	 * @param classes The classes
	 */
	public void setClasses(List<ClassModel> classes) {
		this.classes = classes;
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
	 * Adds an include.
	 *
	 * @param      include  The include
	 */
	public void addInclude(String include) {
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
	 * Sets the variables.
	 *
	 * @param      variables  The variables
	 */
	public void setVariables(List<VariableModel> variables) {
		this.variables = variables;
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

		} else if (data instanceof ClassModel) {
			this.addClass((ClassModel) data);

		} else if (data instanceof String){
			this.addInclude((String) data);

		} else {

			System.err.println("Error adding data in namespace model");
			System.exit(1);
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return The parsed code.
	 */
	@Override
	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);

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