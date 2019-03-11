package me.codvis.ast;

import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14Lexer;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.*;

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
		this.enterScope(this.fileModel);
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

	    JSONObject functionMetaData = new JSONObject();
	    functionMetaData = fetchMetaData(ctx);

	    // Set this function model with name, declaratorid, scope(if exists), lineStart and lineEnd.
	    FunctionModel functionModel = new FunctionModel(input.getText(interval), functionMetaData.getString("declarator"));
	    if(functionMetaData.has("scope")){
	    	functionModel.setScope(functionMetaData.getString("scope"));
	    }

	    if(functionMetaData.has("parameters")){
	    	Iterator i = functionMetaData.getJSONArray("parameters").iterator();

	        while (i.hasNext()) {
	            JSONObject variable = (JSONObject) i.next();
	    		functionModel.addParameter(
	    			new VariableModel(
	    				(String)variable.get("name"), 
	    				(String)variable.get("type")
	    			)
	    		);
	        }
	    }
	    functionModel.setLineStart(ctx.functionbody().start.getLine());
	    functionModel.setLineEnd(ctx.functionbody().stop.getLine());

	    this.scopeStack.peek().addDataInModel(functionModel);
	    this.enterScope(functionModel);
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
     * Listener for parsing variables from scope.
     *
     * @param      ctx   The parsing context
     */
    @Override 
    public void enterStatement(CPP14Parser.StatementContext ctx) {
    	CPP14Parser.DeclarationstatementContext declStatement = ctx.declarationstatement();
    	CPP14Parser.BlockdeclarationContext blockdeclaration = null;
    	CPP14Parser.InitdeclaratorlistContext initDeclaratorlist = null;
    	String variableName = "";
    	String variableType = "";
    	if(declStatement != null){
	    	
	    	if(declStatement.blockdeclaration() != null){
	    		blockdeclaration = declStatement.blockdeclaration();
	    		
	    		if(blockdeclaration.simpledeclaration() != null){
	    			
	    			if(blockdeclaration.simpledeclaration().declspecifierseq() != null){
	    				variableType = blockdeclaration.simpledeclaration().declspecifierseq().getText();
		    			
		    			if(blockdeclaration.simpledeclaration().initdeclaratorlist() != null){
		    				initDeclaratorlist = blockdeclaration.simpledeclaration().initdeclaratorlist();
		    				
		    				while(initDeclaratorlist != null){
		    					if(initDeclaratorlist.initdeclarator().declarator() == null){
		    						break;
		    					} 
		    					
	    						variableName = initDeclaratorlist.initdeclarator().declarator().getText();
	    						this.scopeStack.peek().addDataInModel(new VariableModel(variableName, variableType));
		    					initDeclaratorlist = initDeclaratorlist.initdeclaratorlist();
			    			} 
		    			}
	    			}
	    		}
    		}	
    	} 
    }

    /**
	 * Listener for parsing a namespace declaration. Adding namespace to filemodel.
     *
     * @param      ctx   The parsing context
     */
    @Override
	public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) { 
		NamespaceModel namespace = new NamespaceModel(ctx.Identifier().getText());

	    this.scopeStack.peek().addDataInModel(namespace);
	    this.enterScope(namespace);
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

		this.scopeStack.peek().addDataInModel(usingNamespaceModel);
	}

	/**
	 * Listener for parsing function calls.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override 
	public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) { 
	    this.scopeStack.peek().addDataInModel(ctx.getText());
	}

	/**
	 * Fetches a meta data of the function.
	 *
	 * @param      ctx   The context
	 *
	 * @return     return JSON object containing list of parameters, declrator ID, and scope function is from.
	 */
	private JSONObject fetchMetaData(CPP14Parser.FunctiondefinitionContext ctx){
		JSONObject functionMetaData = new JSONObject();
		CPP14Parser.PtrdeclaratorContext ptrdeclarator = null;
	  	CPP14Parser.NoptrdeclaratorContext noptrdeclarator = null;

	  	if (ctx.declarator().ptrdeclarator() != null){
	  		ptrdeclarator = ctx.declarator().ptrdeclarator();
	  	} else {
	  		noptrdeclarator = ctx.declarator().noptrdeclarator();
	  	}

	    while(true){
		    if(ptrdeclarator != null){
			    if (ptrdeclarator.ptrdeclarator() != null){
			    	ptrdeclarator = ptrdeclarator.ptrdeclarator();


			    }else if(ptrdeclarator.noptrdeclarator() != null){
			    	noptrdeclarator = ptrdeclarator.noptrdeclarator();
			    	
			    	while(true){
			    		if (noptrdeclarator.declaratorid() != null) {
			    			functionMetaData.put("declarator", noptrdeclarator.declaratorid().idexpression().getText());
			    			if(noptrdeclarator.declaratorid().idexpression().qualifiedid() != null){
			    				functionMetaData.put("scope", noptrdeclarator.declaratorid().idexpression().qualifiedid().nestednamespecifier().getText());
			    				functionMetaData.put("declarator", noptrdeclarator.declaratorid().idexpression().qualifiedid().unqualifiedid().getText());
			    			}

							return functionMetaData;
			    		}else if (noptrdeclarator.noptrdeclarator() != null) {

							functionMetaData.put("parameters", fetchParameters(noptrdeclarator));
			    			noptrdeclarator = noptrdeclarator.noptrdeclarator();
			    			continue;
			    		}else if (noptrdeclarator.ptrdeclarator() != null) {
			    			ptrdeclarator = noptrdeclarator.ptrdeclarator();
			    			break;
			    		}else{
			    			System.out.println("Could not understand simple functions");
			    			System.exit(1);
			    		}
			    	}
			    }else{
			    	System.out.println("Could not understand simple functions");
			    	System.exit(1);
			    }
			} else {
				if(noptrdeclarator.declaratorid() != null){
			    	functionMetaData.put("declarator", noptrdeclarator.declaratorid().getText());
					return functionMetaData;
			    }else if (noptrdeclarator.noptrdeclarator() != null) {
	    			noptrdeclarator = noptrdeclarator.noptrdeclarator();
	    		}else if (noptrdeclarator.ptrdeclarator() != null) {
	    			ptrdeclarator = noptrdeclarator.ptrdeclarator();
	    		}else{
	    			System.out.println("Could not understand simple functions");
	    			System.exit(1);
	    		}
			}
		}
	}

	/**
	 * Fetches parameters.
	 *
	 * @param      noptrdeclarator  The noptrdeclarator
	 *
	 * @return     List of parameters found.
	 */
	private List<VariableModel> fetchParameters(CPP14Parser.NoptrdeclaratorContext noptrdeclarator){
		if(noptrdeclarator.parametersandqualifiers() == null){
			return new ArrayList<>();
		}

		List<VariableModel> paramVariables = new ArrayList<>();
			
		if(noptrdeclarator.parametersandqualifiers() != null){
			if(noptrdeclarator.parametersandqualifiers().parameterdeclarationclause() != null){
				CPP14Parser.ParameterdeclarationclauseContext parameters = noptrdeclarator.parametersandqualifiers().parameterdeclarationclause();
				if(parameters.parameterdeclarationlist() != null){
					CPP14Parser.ParameterdeclarationlistContext paramList = parameters.parameterdeclarationlist();
					CPP14Parser.ParameterdeclarationContext paraDecl = null;
					
					while(true){
						if(paramList == null){
							break;
						}

						if(paramList.parameterdeclaration() != null){
							paraDecl = paramList.parameterdeclaration();
							if(paraDecl.declarator() != null){
								paramVariables.add(
									new VariableModel(
										paraDecl.declarator().getText(),
										paraDecl.declspecifierseq().getText()
									)
								);

							} else if (paraDecl.abstractdeclarator() != null) {
								paramVariables.add(
									new VariableModel(
										paraDecl.abstractdeclarator().getText(),
										paraDecl.declspecifierseq().getText()
									)
								);
							}
							
							paramList = paramList.parameterdeclarationlist();

						} else if(paramList.parameterdeclarationlist() != null){
							paramList = paramList.parameterdeclarationlist();
							continue;

						}
					} 
				}
			}
		}
		
		return paramVariables;    		
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