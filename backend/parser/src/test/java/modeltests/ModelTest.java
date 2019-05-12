package me.codvis.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.codvis.ast.FileModel;
import me.codvis.ast.FunctionBodyModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.Model;
import me.codvis.ast.NamespaceModel;
import me.codvis.ast.UsingNamespaceModel;
import me.codvis.ast.VariableModel;

public class ModelTest {
	int nrModels = 5;

	String fileName = "main.cpp";
	String functionName = "function";
	String namespaceName = "namespace";
	String usingNamespaceName = "using namespace";
	String variableName = "variable";

	List<FileModel> files = new ArrayList<>();

	Model model = null;

	@BeforeEach
	public void setup() {
		model = new Model();
	}

	public void fillLists() {
		for (int i = 0; i < nrModels; i++) {
			FileModel fileModel = new FileModel(fileName);
			FunctionModel functionModel = new FunctionModel(functionName);
			NamespaceModel namespaceModel = new NamespaceModel(namespaceName);
			UsingNamespaceModel usingNamespaceModel = new UsingNamespaceModel(usingNamespaceName, 0);
			VariableModel variableModel = new VariableModel(variableName, "int");

			fileModel.addDataInModel(functionModel);
			fileModel.addDataInModel(namespaceModel);
			fileModel.addDataInModel(usingNamespaceModel);
			fileModel.addDataInModel(variableModel);

			files.add(fileModel);
		}
	}

	@Test
	@ExpectSystemExitWithStatus(1)
	public void testAddDataInModel() {
		// Try adding objects with illegal types!
		model.addDataInModel(this);
	}

	@Test
	public void testConvertClassListJsonObjectList() {
		// Test empty list
		JSONArray jsonFile = model.convertClassListJsonObjectList(files);

		assertEquals(jsonFile, null, "Output list not null for empty list");

		// Fill file list and test it.
		fillLists();

		jsonFile = model.convertClassListJsonObjectList(files);

		assertTrue(jsonFile.getJSONObject(0).has("variables"), "Missing variables field");
		assertTrue(jsonFile.getJSONObject(0).has("functions"), "Missing functions field");
		assertTrue(jsonFile.getJSONObject(0).has("file_name"), "Missing file_name field");
		assertTrue(jsonFile.getJSONObject(0).has("using_namespaces"), "Missing using_namespaces field");
		assertTrue(jsonFile.getJSONObject(0).has("namespaces"), "Missing namespaces field");
	}
}