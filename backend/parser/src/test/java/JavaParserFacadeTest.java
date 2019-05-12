package me.codvis.ast;

import me.codvis.ast.parser.Java9Lexer;
import me.codvis.ast.parser.Java9Parser;
import me.codvis.ast.parser.Java9BaseListener;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;


@RunWith(JUnitPlatform.class)
@TestInstance(Lifecycle.PER_CLASS)
public class JavaParserFacadeTest {
	private JavaParserFacade javaParserFacade;
	private List<ParserFacadeTestCase> testCases;
	private static File testDataDirectory;
	@RegisterExtension
	CustomSystemOutExtension sysOut = new CustomSystemOutExtension();

	JavaParserFacadeTest(){
		this.javaParserFacade = new JavaParserFacade();
		this.testCases = new ArrayList<>();

		this.testCases.add(
			new ParserFacadeTestCase(
				"Valid_Single_Function_test",
				"ValidFunctionTest.java",
				"Initial",
				"public class HelloWorld {\n\tpublic static void main(String[] args) {\n\t\tSystem.out.println(\"Hello, World\");\t}}",
				"{\"file\":{\"file_name\":\"/tmp/TestData/ValidFunctionTest.java\",\"classes\":[{\"access_specifiers\":[{\"functions\":"+
				"[{\"return_type\":\"void\",\"declrator_id\":\"main\",\"scope\":\"\",\"name\":\"void main(String[] args)\","+
				"\"start_line\":2,\"function_body\":{\"calls\":[{\"identifier\":\"println(\\\"Hello, World\\\")\",\"scopes\":"+
				"[{\"identifier\":\"System\",\"type\":\"class\"},{\"identifier\":\"out\",\"type\":\"class\"}]}]},\"parameters\""+
				":[{\"name\":\"args\",\"type\":\"String[]\"}],\"end_line\":3}],\"name\":\"private\"}],\"name\":\"HelloWorld\"}]}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Valid_Two_Functions_test",
				"ValidTwoFunctionsTest.java",
				"Initial",
				"public class HelloWorld {\n\t" +
				"public static final void boo (final String lol){\n\tif(true) {\n\treturn;\n\t}\n}\n" +
				"public static final void foo(){\n" + "\t boolean isOk; \n" + "\t}\n}" ,

				"{\"file\":{\"file_name\":\"/tmp/TestData/ValidTwoFunctionsTest.java\",\"classes\":[{\"access_specifiers\":[{\"functions\":"+
				"[{\"return_type\":\"void\",\"declrator_id\":\"boo\",\"scope\":\"\",\"name\":\"void boo (final String lol)\","+
				"\"start_line\":2,\"function_body\":{},\"parameters\":[{\"name\":\"lol\",\"type\":\"final String\"}],\"end_line\":"+
				"6},{\"return_type\":\"void\",\"declrator_id\":\"foo\",\"scope\":\"\",\"name\":\"void foo()\",\"start_line\":7,"+
				"\"function_body\":{\"variables\":[{\"name\":\"isOk\",\"type\":\"boolean\"}]},\"end_line\":9}],\"name\":"+
				"\"private\"}],\"name\":\"HelloWorld\"}]}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_Extended_ASCII_test",
				"InValidSysmbolTest.java",
				"Initial",
				"😂",
				"{\"file\":{\"file_name\":\"/tmp/TestData/InValidSysmbolTest.java\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_Extended_ASCII2_test",
				"InValidSysmbolTest2.java",
				"Initial",
				"void 使() {\n }",
				"{\"file\":{\"file_name\":\"/tmp/TestData/InValidSysmbolTest2.java\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_ParsingContext_test",
				"InValidParsingContext.java",
				"123123123",
				"void YOLO() {\n }",
				"[ERR2345235235235OR] Invalid context\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_NotImplemented_ParsingContext_test",
				"InValidNotImplementedParsingContext.java",
				"Hover",
				"void YOLO() {\n }",
				"[ERROR] no valid listener attacted.\n"
			)
		);
	}

	@BeforeAll
	public void setup(){
		try {
			testDataDirectory = new File("/tmp/TestData");
			testDataDirectory.mkdir();

			for (int i = 0; i < this.testCases.size(); i++) {
				File file = new File("/tmp/TestData/" + this.testCases.get(i).getFileName());
				file.createNewFile();

				//Write Content
				FileWriter writer = new FileWriter(file);
				writer.write(this.testCases.get(i).getContent());
				writer.close();
			}

		} catch (IOException io){
			io.printStackTrace();
		}
	}

	@Test
	public void testReadFile() throws IOException {

		for (int i = 0; i < this.testCases.size(); i++) {

			String fileContent = this.javaParserFacade.readFile(
				new File("/tmp/TestData/"+this.testCases.get(0).getFileName()),
				Charset.forName("UTF-8")
			);

			assertEquals(this.testCases.get(0).getContent(), fileContent, "File content is not equal for " + this.testCases.get(i).getName());
		}
	}

	@Test
	@ExpectSystemExitWithStatus(0)
	public void testParse() throws IOException {
		for (int i = 0; i < this.testCases.size(); i++) {

			this.javaParserFacade.parse(new File("/tmp/TestData/" + this.testCases.get(i).getFileName()), this.testCases.get(i).getParsingContext());
			assertEquals(this.testCases.get(i).getExpectedParsedResult(), this.sysOut.asString(), "File content is not equal for " + this.testCases.get(i).getName());
		}

	}

	@AfterAll
	public void deleteTestDataDirectory(){
		if(this.testDataDirectory.exists()){
	        File[] files = this.testDataDirectory.listFiles();
	        if(files != null){
	            for(int i=0; i<files.length; i++) {
	            	if (files[i].getPath().contains(".java")) {
	               		files[i].delete();
	            	}
	            }
	        }
	    }
	    this.testDataDirectory.delete();
	}
}