import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void testMainVars() {
    	String[] args = ["-t", "Cpp", "-f", "examples/helloworld.cpp"];
    	Main mainTester = new Main("-t", );


    	assertEquals(args[3], mainTester.file, "[TEST] File name did not match.");
    	assertEquals(args[1], mainTester.target, "[TEST] Target did not match.");
    }
}