package me.codvis.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import me.codvis.ast.VariableModel;

public class VariableModelTest {
	private String varType = "int";
	private String varName = "i";

	@Test
	public void testHasFunctions() {
		VariableModel var = new VariableModel();

		assertFalse(var.hasType(), "Wrong type: " + var.hasType() + " from empty VariableModel");
		assertFalse(var.hasName(), "Wrong name: " + var.hasName() + " from empty VariableModel");

		var = new VariableModel(varName, varType);

		assertTrue(var.hasType(), "Wrong type: " + var.hasType() + " from filled VariableModel");
		assertTrue(var.hasName(), "Wrong name: " + var.hasName() + " from filled VariableModel");
	}

	@Test
	public void testGetParsedCode() {
		VariableModel var = new VariableModel();

		JSONObject jsonObj = var.getParsedCode();

		assertNotEquals(varType, jsonObj.getString("type"), "Non empty type for empty VariableModel");
		assertNotEquals(varType, jsonObj.getString("name"), "Non empty name for empty VariableModel");

		var = new VariableModel(varName, varType);

		jsonObj = var.getParsedCode();

		assertEquals(varType, jsonObj.getString("type"), "Incorrect type for VariableModel");
		assertEquals(varName, jsonObj.getString("name"), "Incorrect name for VariableModel");
	}
}