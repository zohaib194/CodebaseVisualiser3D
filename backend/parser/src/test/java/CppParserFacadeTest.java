package me.codvis.ast;

import me.codvis.ast.parser.CPP14Lexer;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14BaseListener;

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
public class CppParserFacadeTest {
	private CppParserFacade cppParserFacade;
	private List<ParserFacadeTestCase> testCases;
	private static File testDataDirectory;
	@RegisterExtension
	CustomSystemOutExtension sysOut = new CustomSystemOutExtension();

	CppParserFacadeTest(){
		this.cppParserFacade = new CppParserFacade();
		this.testCases = new ArrayList<>();

		this.testCases.add(
			new ParserFacadeTestCase(
				"Valid_Single_Function_test",
				"ValidIntMainTest.cpp",
				"Initial",
				"\nint main(){\n"+ "\tint i; \n" +"}",
				"{\"file\":{\"functions\":[{\"function\":{\"declrator_id\":\"main\",\"name\":"+
				"\"int main()\",\"start_line\":2,\"function_body\":{\"variables\":[{\"variable\""+
				":{\"name\":\"i\",\"type\":\"int\"}}]},\"end_line\":4}}],\"file_name\":\"/tmp/TestData/ValidIntMainTest.cpp\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Valid_Two_Functions_test",
				"ValidTwoFunctionsTest.cpp",
				"Initial",
				"\nint main(){\n" + "\tint i; \n \tHelloWorld hw; \n" + "}\n\n" +
				"void foo(){\n" + "\t bool isOk; \n" + "}\n\n" ,
				"{\"file\":{\"functions\":[{\"function\":{\"declrator_id\":\"main\","+
				"\"name\":\"int main()\",\"start_line\":2,\"function_body\":{\"variables\"" +
				":[{\"variable\":{\"name\":\"i\",\"type\":\"int\"}},{\"variable\":{\"name\":" +
				"\"hw\",\"type\":\"HelloWorld\"}}]},\"end_line\":5}},{\"function\":{\"declrator_id\":" +
				"\"foo\",\"name\":\"void foo()\",\"start_line\":7,\"function_body\":{\"variables\":" +
				"[{\"variable\":{\"name\":\"isOk\",\"type\":\"bool\"}}]},\"end_line\":9}}],\"file_name\""+
				":\"/tmp/TestData/ValidTwoFunctionsTest.cpp\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_Extended_ASCII_test",
				"InValidSysmbolTest.cpp",
				"Initial",
				"😂",
				"{\"file\":{\"file_name\":\"/tmp/TestData/InValidSysmbolTest.cpp\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_Extended_ASCII2_test",
				"InValidSysmbolTest2.cpp",
				"Initial",
				"void 使() {\n }" ,
				"{\"file\":{\"file_name\":\"/tmp/TestData/InValidSysmbolTest2.cpp\"}}\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_ParsingContext_test",
				"InValidParsingContext.cpp",
				"123123123",
				"void YOLO() {\n }" ,
				"[ERR2345235235235OR] Invalid context\n"
			)
		);

		this.testCases.add(
			new ParserFacadeTestCase(
				"Invalid_NotImplemented_ParsingContext_test",
				"InValidNotImplementedParsingContext.cpp",
				"Hover",
				"void YOLO() {\n }" ,
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

			String fileContent = this.cppParserFacade.readFile(
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

			this.cppParserFacade.parse(new File("/tmp/TestData/" + this.testCases.get(i).getFileName()), this.testCases.get(i).getParsingContext());
			assertEquals(this.testCases.get(i).getExpectedParsedResult(), this.sysOut.asString(), "File content is not equal for " + this.testCases.get(i).getName());
		}

	}

	@AfterAll
	public void deleteTestDataDirectory(){
		if(this.testDataDirectory.exists()){
	        File[] files = this.testDataDirectory.listFiles();
	        if(files != null){
	            for(int i=0; i<files.length; i++) {
	            	if (files[i].getPath().contains(".cpp")) {
	               		files[i].delete();
	            	}
	            }
	        }
	    }
	    this.testDataDirectory.delete();
	}
}