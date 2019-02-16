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

public class CppLstnr_Initial extends CppExtendedListener {
	private FileModel fileModel;

	CppLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        
        // Get interval between function start and end of function name.
	    Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());
	    
	    // Get the input stream of function definition rule.
	    CharStream input = ctx.start.getInputStream();

	    fileModel.addFunction(new FunctionModel(input.getText(interval)));
    }

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

	@Override
	public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) { 
		if (ctx.nestednamespecifier() != null) {
			fileModel.addUsingNamespace(new UsingNamespaceModel(ctx.nestednamespecifier().getText() + ctx.namespacename().getText(), ctx.getStart().getLine()));	
		
		}else{
			fileModel.addUsingNamespace(new UsingNamespaceModel(ctx.namespacename().getText(), ctx.getStart().getLine()));
		}
	}

    public JSONObject getParsedCode() {  
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

    public FileModel getFileModel(){
    	return this.fileModel;
    }
 }