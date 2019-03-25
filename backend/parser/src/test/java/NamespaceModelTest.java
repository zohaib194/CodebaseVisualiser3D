package me.codvis.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import me.codvis.ast.FunctionModel;
import me.codvis.ast.NamespaceModel;
import me.codvis.ast.UsingNamespaceModel;
import me.codvis.ast.VariableModel;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

public class NamespaceModelTest {
	private String namespaceName = "namespace";
	private String functionName = "function";
	private String usingNamespaceName = "usingnamespace";
	private String callName = "call";
	private String variableName = "variable";

	NamespaceModel model = null;

	@BeforeEach
	public void setup() {
		model = new NamespaceModel(namespaceName);
	}

	public void fillNamespaceModel() {
		if (model != null) {
			model.addDataInModel(new FunctionModel(functionName));
			model.addDataInModel(new NamespaceModel(namespaceName));
			model.addDataInModel(new UsingNamespaceModel(usingNamespaceName, 0));
			model.addDataInModel("test()");
			model.addDataInModel(new VariableModel(variableName, "int"));
		}
	}

	@Test
	@ExpectSystemExitWithStatus(1)
	public void testAddDataInModel() {
		assertEquals(0, model.getFunctions().size(), "Preexisting functions in empty model");
		assertEquals(0, model.getNamespaces().size(), "Preexisting namespace in empty model");
		assertEquals(0, model.getUsingNamespaces().size(), "Preexisting using namespace in empty model");
		assertEquals(0, model.getCalls().size(), "Preexisting call in empty model");
		assertEquals(0, model.getVariables().size(), "Preexisting variable in empty model");

		// Fill with data.
		fillNamespaceModel();

		assertEquals(1, model.getFunctions().size(), "Preexisting functions in empty model");
		assertEquals(1, model.getNamespaces().size(), "Preexisting namespace in empty model");
		assertEquals(1, model.getUsingNamespaces().size(), "Preexisting using namespace in empty model");
		assertEquals(1, model.getCalls().size(), "Preexisting call in empty model");
		assertEquals(1, model.getVariables().size(), "Preexisting variable in empty model");

		// Try adding objects with illegal types!
		model.addDataInModel(this);
	}

	@Test
	public void testGetParsedCode() {
		String[] names = { "functions", "namespaces", "using_namespaces", "calls", "variables" };
		String[] negMsg = { "Function list shouldn't exsist", "Namespace list shouldn't exsist",
				"Using_namespace list shouldn't exsist", "Call list shouldn't exsist",
				"Variable list shouldn't exsist" };

		JSONObject jsonObj = model.getParsedCode();

		// Check all field if they DON'T exsit.
		for (int i = 0; i < names.length; i++) {
			assertFalse(jsonObj.has(names[i]), negMsg[i]);
		}

		// Fill with data.
		fillNamespaceModel();

		jsonObj = model.getParsedCode();

		assertEquals(1, jsonObj.getJSONArray("functions").toList().size(), "Incorrect nr of functions");
		assertEquals(1, jsonObj.getJSONArray("namespaces").toList().size(), "Incorrect nr of namespaces");
		assertEquals(1, jsonObj.getJSONArray("using_namespaces").toList().size(), "Incorrect nr of using_namespaces");
		assertEquals(1, jsonObj.getJSONArray("calls").toList().size(), "Incorrect nr of calls");
		assertEquals(1, jsonObj.getJSONArray("variables").toList().size(), "Incorrect nr of variables");
	}
}