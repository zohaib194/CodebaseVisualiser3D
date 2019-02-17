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

    @Override 
    public void enterClassspecifier(CPP14Parser.ClassspecifierContext ctx) {   
    	ClassModel classModel;

    	// If it is not a annonymous class.
    	if(ctx.classhead().classheadname() != null){
    		classModel = new ClassModel(ctx.classhead().classheadname().getText());
    		//fileModel.
    	} else {
    		classModel = new ClassModel("null");
    	}

    	CPP14Parser.AccessspecifierContext currentAccessSpecifier = null;
    	CPP14Parser.MemberspecificationContext currentParseContent = ctx.memberspecification();
    	while(currentParseContent != null){

    		// If member specification is a access specifier (public/protected/private) exists.
    		if(currentParseContent.accessspecifier() != null){

    			currentAccessSpecifier = currentParseContent.accessspecifier();

    		} else {		// Or else it is member declration.

				// Get the interval where the function name in class exists.
   				Interval nameInterval = new Interval(currentParseContent.memberdeclaration().start.getStartIndex(), currentParseContent.memberdeclaration().stop.getStopIndex());
    			
    			// Get the input stream of member declaration context..
    			CharStream input = currentParseContent.memberdeclaration().start.getInputStream();

                CPP14Parser.MemberdeclarationContext currentDeclaration = currentParseContent.memberdeclaration();
    			
    			if (currentAccessSpecifier != null) {

		   			switch(currentAccessSpecifier.getText()){
		   				case "public":
                            if(currentDeclaration.functiondefinition() != null){
                              classModel.addPublicData(currentDeclaration.functiondefinition().declarator().getText());  
                            } else {
		   					  classModel.addPublicData(input.getText(nameInterval));  
                            }
		   					break;
		   				case "private":
		   					if(currentDeclaration.functiondefinition() != null){
                              classModel.addPublicData(currentDeclaration.functiondefinition().declarator().getText());  
                            } else {
                              classModel.addPublicData(input.getText(nameInterval));  
                            }
                            break;
                        case "protected":
                            if(currentDeclaration.functiondefinition() != null){
                              classModel.addPublicData(currentDeclaration.functiondefinition().declarator().getText());  
                            } else {
                              classModel.addPublicData(input.getText(nameInterval));  
                            }
		   					break;
		   				
		   			}
    		
    			} else { // Or private member declaration exist without private access specifier.
		   	
		   			classModel.addPrivateData(input.getText(nameInterval));
  			
	  			}
	    	}

    		// Move to next member specification.
	 		currentParseContent = currentParseContent.memberspecification(); 
    	
    	}

    	fileModel.addClass(classModel);
    }

    public JSONObject getParsedCode() {  
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

    public FileModel getFileModel(){
    	return this.fileModel;
    }
 }