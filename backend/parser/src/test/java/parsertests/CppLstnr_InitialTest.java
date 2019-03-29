package me.codvis.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Method;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import me.codvis.ast.ParserTestCase;
import me.codvis.ast.parser.CPP14Lexer;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.CppExtendedListener;
import me.codvis.ast.CppLstnr_Initial;

@TestInstance(Lifecycle.PER_CLASS)
public class CppLstnr_InitialTest {
	String filename = "main.cpp";
	private List<ParserTestCase> cases = new ArrayList<>();// Arrays.asList(// ,
	/*
	 * new ParserTestCase( "variable", "int a;", Array.toList(
	 * "{\"file\":{\"functions\":[{\"function\":{\"name\":\"int main()\",\"start_line\":0,\"function_body\":{},\"parameters\":[],\"end_line\":0}}]}}"
	 * ) Array.toList(
	 * "{\"file\":{\"functions\":[{\"function\":{\"name\":\"int main()\",\"start_line\":0,\"function_body\":{},\"parameters\":[],\"end_line\":0}}]}}"
	 * ) ),
	 */
	// );

	@BeforeAll
	public void setup() {
		ParserTestCase functiondef_basic = new ParserTestCase("functiondefinition", filename, "void test() {}");
		functiondef_basic.expected.add(
				"{\"file\":{\"functions\":[{\"function\":{\"declrator_id\":\"test\",\"name\":\"void test()\",\"start_line\":1,\"function_body\":{},\"end_line\":1}}],\"file_name\":\""
						+ filename + "\"}}");

		ParserTestCase functiondef_basic_cpp17 = new ParserTestCase("functiondefinition", filename,
				"auto heaven() -> void {}");
		functiondef_basic.expected.add(
				"{\"file\":{\"functions\":[{\"function\":{\"declrator_id\":\"heaven\",\"name\":\"auto heaven() -> void\",\"start_line\":1,\"function_body\":{},\"end_line\":1}}],\"file_name\":\""
						+ filename + "\"}}");

		/*
		 * ParserTestCase variable_basic = new ParserTestCase("variable_basic",
		 * filename, "int a;"); variable_basic.expected .add(
		 * "{\"file\":{\"variables\":[{\"variable\":{\"name\":\"a\",\"type\":\"int\"}}],\"file_name\":\""
		 * + filename + "\"}}");
		 * 
		 * ParserTestCase variable_list = new ParserTestCase("variable_list", filename,
		 * "int c, d = 1, e, f = 0;"); variable_list.expected.add(
		 * "{\"file\":{\"variables\":[{\"variable\":{\"name\":\"c\",\"type\":\"int\"}},{\"variable\":{\"name\":\"d\",\"type\":\"int\"}},{\"variable\":{\"name\":\"e\",\"type\":\"int\"}},{\"variable\":{\"name\":\"f\",\"type\":\"int\"}}],\"file_name\":\""
		 * + filename + "\"}}");
		 */

		// cases.add(variable_basic);
		// cases.add(variable_list);
		cases.add(functiondef_basic);
		cases.add(functiondef_basic_cpp17);
	}

	@Test
	public void test() {
		for (ParserTestCase caseData : this.cases) {
			System.out.println("---------- Case: " + caseData.listenername + " ----------");

			CPP14Lexer lexer = new CPP14Lexer(new ANTLRInputStream(caseData.input));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			CPP14Parser parser = new CPP14Parser(tokens);

			Method parserMethod = null;
			try {
				parserMethod = parser.getClass().getDeclaredMethod(caseData.listenername);
			} catch (NoSuchMethodException e) {
				assertTrue(false, "The given listener doesn't exsist within ParseTree class");
			}

			Object tree = null;
			try {
				tree = parserMethod.invoke(parser);
			} catch (Exception e) {
				assertTrue(false, "Failed to invoke method: " + e.toString());
			}

			CppExtendedListener listener = new CppLstnr_Initial(caseData.filename);

			ParseTreeWalker walker = new ParseTreeWalker();

			walker.walk(listener, (ParseTree) tree);

			for (String expected : caseData.expected) {
				assertEquals(expected, listener.getParsedCode().toString(), "Not equal");
			}

			for (String illegal : caseData.illegal) {
				assertNotEquals(illegal, listener.getParsedCode().toString(), "Shouldn't be equal");
			}
		}
	}
}

// {\"file\":{\"variables\":[{\"variable\":{\"variable\":{\"name\":\"a\",\"type\":\"int\"}}],\"file_name\":\""
// + filename + "\"}}
// {\"file\":{\"variables\":[{\"variable\":{\"variable\":{\"name\":\"b\",\"type\":\"int\"}}],\"file_name\":\""
// + filename + "\"}}
// {\"file\":{\"variables\":[{\"variable\":{\"name\":\"c\",\"type\":\"int\"}},{\"variable\":{\"name\":\"d\",\"type\":\"int\"}},{\"variable\":{\"name\":\"e\",\"type\":\"int\"}},{\"variable\":{\"name\":\"f\",\"type\":\"int\"}}],\"file_name\":\""
// + filename + "\"}}
// {"file":{"variables":[{"variable":{"name":"list","type":"std::vector<int>"}}],\"file_name\":\""
// + filename + "\"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}
// {"file":{"variables":[{"variable":{"name":"a","type":"int"}},{"variable":{"name":"b","type":"int"}},{"variable":{"name":"c","type":"int"}},{"variable":{"name":"d","type":"int"}},{"variable":{"name":"e","type":"int"}},{"variable":{"name":"f","type":"int"}},{"variable":{"name":"list","type":"std::vector<int>"}}],"file_name":"./../../../../src/test/java/variable.cpp"}}