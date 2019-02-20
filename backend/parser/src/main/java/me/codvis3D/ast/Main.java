package me.codvis.ast;

import java.io.File;
import java.io.IOException;

public class Main {
    public static String flag="";
    public static String target="";
    public static String context="";
    public static String file="";
    
    public static void main(String[] argv) throws IOException {

        parseArgs(argv);

        if(target == "" || file == "" || context == ""){
            System.err.println("Use: java Main --help for more information\n");
            System.err.println("[ERROR] Usage: java Main [-t | --Target] targetName [-f | --File] fileName [-c | --Context] context");
            System.exit(0);
        }

        target = target.toUpperCase();
        // Checks which language to be parse.
        switch(target){
            case "CPP":
                CppParserFacade cppParserFacade = new CppParserFacade();
                cppParserFacade.parse(new File(file), context);
                break;
            
            case "JAVA":
                JavaParserFacade javaParserFacade = new JavaParserFacade();
                javaParserFacade.parse(new File(file), context);
                break;
            default:
                System.err.println("[ERROR] Target is not supported.");
                System.exit(0);
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
                    target = argv[++i];
                    break;

                case "--File":
                case "-f":
                    file = argv[++i];
                    break;

                case "--Context":
                case "-c":
                    context = argv[++i];
                    break;

                case "--help":
                    String help = "Usage: java Main [option...] \n\n" 
                        + " -t, --Target \t Language target in which source file is written in.\n"
                        + " -f, --File \t Relative path to source file.\n"
                        + " -c, --Context \t Context to be achieved from the source file.\n";

                    System.out.println(help);
                    System.exit(0);
                    break;

                default:
                    System.err.println("[ERROR] Usage: java Main [-t | --Target] targetName [-f | --File] fileName [-c | --Context] context");
                    System.exit(0);
            }
        }
    }
}
        



