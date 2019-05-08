package me.codvis.ast;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Stack;


public class CppExtendedListenerTest {
	public CppExtendedListener cppExtendedListener;

	public CppExtendedListenerTest() throws NoSuchFieldException {
		this.cppExtendedListener = new CppExtendedListener();
		this.cppExtendedListener.getClass().getDeclaredField("scopeStack").setAccessible(true);
	}

	@Test
	public void testEnterScope(){
		this.cppExtendedListener.enterScope(new Model());
		this.cppExtendedListener.enterScope(new Model());
		this.cppExtendedListener.enterScope(new Model());
		this.cppExtendedListener.enterScope(new Model());

		assertEquals(4, this.cppExtendedListener.scopeStack.size());
	}

	@Test
	public void testExitScope(){
		this.cppExtendedListener.enterScope(new NamespaceModel("NamespaceModelTest"));
		this.cppExtendedListener.enterScope(new FunctionModel("FunctionModelTest"));


		Model model = this.cppExtendedListener.exitScope();
		assertTrue(model instanceof FunctionModel);

		model = this.cppExtendedListener.exitScope();
		assertTrue(model instanceof NamespaceModel);

	}


	@Test
    public void testGetParsedCode() {
    	assertTrue(this.cppExtendedListener.getParsedCode() == null);
    }
 }