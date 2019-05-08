package me.codvis.ast;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Stack;


public class JavaExtendedListenerTest {
	public JavaExtendedListener javaExtendedListener;

	public JavaExtendedListenerTest() throws NoSuchFieldException {
		this.javaExtendedListener = new JavaExtendedListener();
		this.javaExtendedListener.getClass().getDeclaredField("scopeStack").setAccessible(true);
	}

	@Test
	public void testEnterScope(){
		this.javaExtendedListener.enterScope(new Model());
		this.javaExtendedListener.enterScope(new Model());
		this.javaExtendedListener.enterScope(new Model());
		this.javaExtendedListener.enterScope(new Model());

		assertEquals(4, this.javaExtendedListener.scopeStack.size());
	}

	@Test
	public void testExitScope(){
		this.javaExtendedListener.enterScope(new NamespaceModel("NamespaceModelTest"));
		this.javaExtendedListener.enterScope(new FunctionModel("FunctionModelTest"));


		Model model = this.javaExtendedListener.exitScope();
		assertTrue(model instanceof FunctionModel);

		model = this.javaExtendedListener.exitScope();
		assertTrue(model instanceof NamespaceModel);

	}


	@Test
    public void testGetParsedCode() {
    	assertTrue(this.javaExtendedListener.getParsedCode() == null);
    }
 }