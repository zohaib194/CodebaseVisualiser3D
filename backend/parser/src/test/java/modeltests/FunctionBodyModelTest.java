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

public class FunctionBodyModelTest {
	private String name = "f()";
	private String paramName = "i";
	private String paramType = "int";
	private VariableModel parameter = new VariableModel(paramName, paramType);
	private FunctionBodyModel functionBody = new FunctionBodyModel();

	FunctionModel model = null;

	@BeforeEach
	public void setup() {
		model = new FunctionModel(name, name);
	}

	public void fillNamespaceModel() {
		if (model != null) {
			model.addDataInModel(functionBody);
			model.addDataInModel(parameter);
		}
	}

	@Test
	@ExpectSystemExitWithStatus(1)
	public void testAddDataInModel() {
		assertEquals(null, model.getFunctionBody(), "Pre-existing function body");
		assertEquals(0, model.getParameters().size(), "Pre-existing parameters");

		// Fill with data.
		fillNamespaceModel();

		assertNotEquals(null, model.getFunctionBody(), "Missing function body");
		assertEquals(1, model.getParameters().size(), "Missing parameters");

		// Try adding objects with illegal types!
		model.addDataInModel(this);
	}

	@Test
	public void testGetParsedCode() {
		JSONObject jsonObj = model.getParsedCode();

		assertFalse(jsonObj.has("function_body"), "Pre-existing function_body field");
		assertFalse(jsonObj.has("parameters"), "Pre-existing parameters field");

		// Fill with data.
		fillNamespaceModel();

		jsonObj = model.getParsedCode();

		assertTrue(jsonObj.has("function_body"), "Missing function_body field");
		assertNotEquals(null, jsonObj.getJSONObject("function_body"), "function_body field is null");
		assertTrue(jsonObj.has("parameters"), "Missing parameters field");
		assertEquals(1, jsonObj.getJSONArray("parameters").length(), "Incorrect nr of parameters");
	}
}