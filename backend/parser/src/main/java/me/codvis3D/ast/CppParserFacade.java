package me.codvis.ast;

import me.codvis.ast.parser.CPP14Lexer;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14BaseListener;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class CppParserFacade {
    
    /**
     * Function reads a file.
     *
     * @param      file         The file
     * @param      encoding     The encoding
     *
     * @return     string version of file.
     *
     * @throws     IOException  Input/output exception
     */
    private static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }
    
    /**
     * Function parse a cpp file and adds a listener on parser.
     *
     * @param      file         The file
     *
     * @throws     IOException  Input/output exception
     */
    public void parse(File file, String context) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        CPP14Lexer lexer = new CPP14Lexer(new ANTLRInputStream(code));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CPP14Parser parser = new CPP14Parser(tokens);

        ParseTree tree = parser.translationunit();
        
        CPP14BaseListener listener = null;
        switch(context){
            case "Initial":
                listener = new CppLstnr_initial();
                break;
            case "Hover":
                break;

            default:
                System.err.println("[ERROR] Invalid context\n");
                System.exit(0);
        }
        
        ParseTreeWalker walker =  new ParseTreeWalker();
        
        if(listener == null){
            System.exit(0);
        }
        walker.walk(listener, tree);
    }
}