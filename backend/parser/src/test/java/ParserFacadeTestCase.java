package me.codvis.ast;

public class ParserFacadeTestCase {
	private String name;
	private String fileName;
	private String content;
	private String parsingContext;
	private String expectedParsedResult;

	ParserFacadeTestCase(String name, String fileName, String parsingContext, String content, String expectedResult){
		this.name = name;
		this.fileName = fileName;
		this.parsingContext = parsingContext;
		this.content = content;
		this.expectedParsedResult = expectedResult;
	}

	public String getName(){
		return this.name;
	}

	public String getFileName(){
		return this.fileName;
	}

	public String getParsingContext(){
		return this.parsingContext;
	}

	public String getContent(){
		return this.content;
	}

	public String getExpectedParsedResult(){
		return this.expectedParsedResult;
	}
}