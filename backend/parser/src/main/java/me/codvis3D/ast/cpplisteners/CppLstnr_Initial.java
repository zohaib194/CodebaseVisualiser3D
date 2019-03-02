package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14Lexer;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Stack;

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

	    int index = fileModel.addModelInCurrentScope(functionModel, (Stack<ModelIdentifier>)this.scopeStack.clone());
	    this.enterScope(new ModelIdentifier("functions", index));
    }

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterFunctiondefinition.
     *
     * @param      ctx   The parsing context
     */
    @Override 
    public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
    	this.exitScope();
    }

    /**
	 * Listener for parsing a namespace declaration. Adding namespace to filemodel.
     *
     * @param      ctx   The parsing context
     */
    @Override
	public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) { 
		NamespaceModel namespace = new NamespaceModel(ctx.Identifier().getText());

	    int index = fileModel.addModelInCurrentScope(namespace, (Stack<ModelIdentifier>)this.scopeStack.clone());
	    this.enterScope(new ModelIdentifier("namespaces", index));
	}

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterOriginalnamespacedefinition.
     *
     * @param      ctx   The parsing context
     */
	@Override
	public void exitOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
		this.exitScope();
	}

	/**
	 * Listener for parsing a using namespace declaration. Adding namespace to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) {
		UsingNamespaceModel usingNamespaceModel;
		if (ctx.nestednamespecifier() != null) {
			usingNamespaceModel = new UsingNamespaceModel(ctx.nestednamespecifier().getText() + ctx.namespacename().getText(), ctx.getStart().getLine());	
		
		}else{
			usingNamespaceModel = new UsingNamespaceModel(ctx.namespacename().getText(), ctx.getStart().getLine());
		}

		fileModel.addModelInCurrentScope(usingNamespaceModel, (Stack<ModelIdentifier>)this.scopeStack.clone());
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