package me.codvis.ast;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

/**
 * Class for abstracting a code namespace.
 */
public class NamespaceModel extends Model {
	private String name;
	private List<FunctionModel> functions;
	private List<NamespaceModel> namespaces;
	private List<UsingNamespaceModel> usingNamespaces;

	/**
	 * Constructs the namespace, setting the name.
	 *
	 * @param      name  The name
	 */
	public NamespaceModel(String name){
		this.name = name;
		this.functions = new ArrayList<>();
		this.namespaces = new ArrayList<>();
		this.usingNamespaces = new ArrayList<>();
	}

	/**
	 * Adds a function.
	 *
	 * @param      function  The function
	 */
	public void addFunction(FunctionModel function){
		this.functions.add(function);
	}

	/**
	 * Adds a namespace.
	 *
	 * @param      namespace  The namespace
	 */
	public void addNamespace(NamespaceModel namespace){
		this.namespaces.add(namespace);
	}

	/**
	 * Adds an using namespace.
	 *
	 * @param      namespace  The namespace
	 */
	public void addUsingNamespace(UsingNamespaceModel namespace){
		this.usingNamespaces.add(namespace);
	}

	/**
	 * Gets the name.
	 *
	 * @return     The name.
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Sets the functions.
	 *
	 * @param      functions  The functions
	 */
	public void setFunctions(List<FunctionModel> functions){
		this.functions = functions;
	}

	/**
	 * Sets the inner namespaces.
	 *
	 * @param      namespaces  The namespaces
	 */
	public void setNamespaces(List<NamespaceModel> namespaces){
		this.namespaces = namespaces;
	}

	/**
	 * Sets the inner using namespaces.
	 *
	 * @param      namespaces  The namespaces
	 */
	public void setUsingNamespaces(List<UsingNamespaceModel> namespaces){
		this.usingNamespaces = namespaces;
	}

	/**
	 * Adds a model in current scope.
	 *
	 * @param      model       The model
	 * @param      scopeStack  The scope stack identifying current scope position
	 *
	 * @return     index in list for its type where model was added for current scope. If not a list it will return 0.
	 */
	@Override
	protected <T extends Model> int addModelInCurrentScope(T model, Stack<ModelIdentifier> scopeStack){
		ModelIdentifier modelIdentifier;

		int index = 0;

		if (scopeStack.size() > 0) {
			modelIdentifier = scopeStack.pop();

			switch (modelIdentifier.modelType) {
				case "functions":
					index = this.functions.get(modelIdentifier.modelIndex).addModelInCurrentScope(model, scopeStack);
					break;
				case "namespaces":
					index = this.namespaces.get(modelIdentifier.modelIndex).addModelInCurrentScope(model, scopeStack);
					break;
				case "usingNamespaces":
					index = this.usingNamespaces.get(modelIdentifier.modelIndex).addModelInCurrentScope(model, scopeStack);
					break;
				default:
					System.out.println("Error adding model in current scope");
					System.exit(1);

			}
		} else {
			if (model instanceof FunctionModel) {
				this.addFunction((FunctionModel)model);
				index = this.usingNamespaces.size() -1;

			}else if (model instanceof  NamespaceModel) {
				this.addNamespace((NamespaceModel)model);
				index = this.usingNamespaces.size() -1;

			}else if (model instanceof UsingNamespaceModel) {
				this.addUsingNamespace((UsingNamespaceModel)model);
				index = this.usingNamespaces.size() -1;

			}else{
				System.out.println("Error adding model in current scope");
				System.exit(1);
			}

		}

		return index;
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
	@Override
	public JSONObject getParsedCode(){
		JSONObject parsedCode = new JSONObject();

		parsedCode.put("name", this.name);

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

		return parsedCode;
	}
}