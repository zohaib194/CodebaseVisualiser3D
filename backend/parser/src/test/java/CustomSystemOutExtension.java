package me.codvis.ast;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class CustomSystemOutExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private PrintStream sysOut;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		this.sysOut = System.out;
        System.setOut(new PrintStream(this.outContent));
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
        System.setOut(this.sysOut);
	}

	public String asString() {
		String out = this.outContent.toString();
		this.outContent.reset();
        return out;
    }

}