package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14Lexer;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.json.JSONObject;
/**
 * Class for exstending listeners and parsing code requiered for initial 3D view for code abstraction.
 */
public class CppLstnr_Initial extends CppExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath for file being parsed.
	 *
	 * @param      filePath  The file path
	 */
	CppLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

	/**
	 * Listener for parsing a method/function declaration. Adding function name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
    @Override 
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        
        // Get interval between function start and end of function name.
	    Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());
	    
	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();

	    // Set this function model with name, lineStart and lineEnd.
	    FunctionModel functionModel = new FunctionModel(input.getText(interval));
	    functionModel.setLineStart(ctx.functionbody().start.getLine());
	    functionModel.setLineEnd(ctx.functionbody().stop.getLine());

	    fileModel.addFunction(functionModel);
    }

    /**
	 * Listener for parsing a namespace declaration. Adding namespace to filemodel.
     *
     * @param      ctx   The parsing context
     */
    @Override
	public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) { 
		NamespaceModel namespace = new NamespaceModel(ctx.Identifier().getText());
		// Create a new parser and listener
		CppLstnr_Initial listnr = new CppLstnr_Initial(this.fileModel.getFilename());
        CPP14Lexer lexer = new CPP14Lexer(new ANTLRInputStream(ctx.namespacebody().getText()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CPP14Parser parser = new CPP14Parser(tokens);
        ParseTree tree = parser.translationunit();
        ParseTreeWalker walker =  new ParseTreeWalker();

        walker.walk(listnr, tree);

        FileModel fileModel = listnr.getFileModel();

        namespace.setFunctions(fileModel.getFunctions());
        namespace.setNamespaces(fileModel.getNamespaces());
        namespace.setUsingNamespaces(fileModel.getUsingNamespaces());

		this.fileModel.addNamespace(namespace);
		ctx.exitRule(this);
	}

	/**
	 * Listener for parsing a using namespace declaration. Adding namespace to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) { 
		if (ctx.nestednamespecifier() != null) {
			fileModel.addUsingNamespace(new UsingNamespaceModel(ctx.nestednamespecifier().getText() + ctx.namespacename().getText(), ctx.getStart().getLine()));	
		
		}else{
			fileModel.addUsingNamespace(new UsingNamespaceModel(ctx.namespacename().getText(), ctx.getStart().getLine()));
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {  
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

    /**
     * Gets the file model.
     *
     * @return     The file model.
     */
    public FileModel getFileModel(){
    	return this.fileModel;
    }
 }