package me.codvis.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import me.codvis.ast.FunctionBodyModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.VariableModel;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

public class FileModelTest {
	private String name = "main.cpp";
	private FunctionModel function = new FunctionModel("function");
	private NamespaceModel namespace = new NamespaceModel("namespace");
	private UsingNamespaceModel usingNamespace = new UsingNamespaceModel("usingNamespace", 0);
	private VariableModel variable = new VariableModel("variable", "int");

	FileModel model = null;

	@BeforeEach
	public void setup() {
		model = new FileModel(name);
	}

	public void fillNamespaceModel() {
		if (model != null) {
			model.addDataInModel(function);
			model.addDataInModel(namespace);
			model.addDataInModel(usingNamespace);
			model.addDataInModel(variable);
		}
	}

	@Test
	@ExpectSystemExitWithStatus(1)
	public void testAddDataInModel() {

		assertEquals(0, model.getFunctions().size(), "Pre-existing functions");
		assertEquals(0, model.getNamespaces().size(), "Pre-existing namespaces");
		assertEquals(0, model.getUsingNamespaces().size(), "Pre-existing using namespaces");
		assertEquals(0, model.getVariables().size(), "Pre-existing variables");

		// Fill with data.
		fillNamespaceModel();

		assertEquals(1, model.getFunctions().size(), "Missing functions");
		assertEquals(1, model.getNamespaces().size(), "Missing namespaces");
		assertEquals(1, model.getUsingNamespaces().size(), "Missing using namespaces");
		assertEquals(1, model.getVariables().size(), "Missing variables");

		// Try adding objects with illegal types!
		model.addDataInModel(this);
	}

	@Test
	public void testGetParsedCode() {
		JSONObject jsonObj = model.getParsedCode();

		assertFalse(jsonObj.has("functions"), "Pre-existing functions field");
		assertFalse(jsonObj.has("namespaces"), "Pre-existing namespaces field");
		assertFalse(jsonObj.has("using_namespaces"), "Pre-existing using_namespaces field");
		assertFalse(jsonObj.has("variables"), "Pre-existing variables field");

		// Fill with data.
		fillNamespaceModel();

		jsonObj = model.getParsedCode();

		assertTrue(jsonObj.has("functions"), "Missing functions list");
		assertEquals(1, jsonObj.getJSONArray("functions").length(), "Incorrect nr of functions");
		assertTrue(jsonObj.has("namespaces"), "Missing namespaces list");
		assertEquals(1, jsonObj.getJSONArray("namespaces").length(), "Incorrect nr of namespaces");
		assertTrue(jsonObj.has("using_namespaces"), "Missing using_namespaces list");
		assertEquals(1, jsonObj.getJSONArray("using_namespaces").length(), "Incorrect nr of using_namespaces");
		assertTrue(jsonObj.has("variables"), "Missing variables list");
		assertEquals(1, jsonObj.getJSONArray("variables").length(), "Incorrect nr of variables");

	}
}