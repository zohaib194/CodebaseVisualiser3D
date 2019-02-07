package me.codvis.ast;

import java.io.File;
import java.io.IOException;

public class Main {
    public static String flag;
    public static String target;
    public static String file;
    public static boolean err = false;
    
    public static void main(String[] argv) throws IOException {

        parseArgs(argv);


        // Checks which language to be parse.
        switch(target){
            case "cpp":
            case "CPP":
            case "Cpp":
                CppParserFacade cppParserFacade = new CppParserFacade();
                cppParserFacade.parse(new File(file));
                break;
            
            case "java":
            case "Java":
                JavaParserFacade javaParserFacade = new JavaParserFacade();
                javaParserFacade.parse(new File(file));
                break;
            default:
                System.err.println("[ERROR] Target is not supported.");
        }
    }

    /**
     * Function parse arguments list
     *
     * @param      argv  list of arguments
     */
    public static void parseArgs(String[] argv){
        for (int i = 0; i < argv.length; i++) {
            
            flag = argv[i];

            switch(flag){
                case "--Target":
                case "-t":
                    target = argv[i + 1];
                    i++;
                    break;

                case "--File":
                case "-f":
                    file = argv[i + 1];
                    i++;
                    break;

                default:
                    System.err.println("[ERROR] Usage: java Main [-t | --Target] targetName [ -f | --File] fileName.");
                    err = true;
                    break;
            }

            // In case there is a error in syntax.
            if(err){
                err = false;
                return;
            }
        }
    }
}
        



