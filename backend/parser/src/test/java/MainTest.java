package me.codvis.ast;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainVars() {
    	String[] args = {"-t", "CPP", "-f", "examples/helloworld.cpp"};

    	// Catch io exception if the file name is incorrect.
    	try {
    		Main.main(args);
        } catch (IOException io) {
        	assertEquals(args[3], io.getMessage());
        }

        // Checking if variables in Main has correct data.
    	assertEquals(args[3], Main.file, "[TEST] File name did not match.");
    	assertEquals(args[1], Main.target, "[TEST] Target did not match.");
    }
}