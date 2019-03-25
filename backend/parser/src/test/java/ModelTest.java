package me.codvis.ast;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import me.codvis.ast.Model;

public class ModelTest {

	Model model = null;

	@BeforeEach
	public void setup() {
		model = new Model();
	}

	@Test
	@ExpectSystemExitWithStatus(1)
	public void testAddDataInModel() {
		// Try adding objects with illegal types!
		model.addDataInModel(this);
	}

	@Test
	@Disabled("Come back to this one!")
	public void testConvertClassListJsonObjectList() {
		//
	}
}