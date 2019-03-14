package me.codvis.ast;

import me.codvis.ast.AccessSpecifierModel;
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
 * Class for exstending listeners and parsing code requiered for initial 3D view
 * for code abstraction.
 */
public class CppLstnr_Initial extends CppExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath for file being parsed.
	 *
	 * @param filePath The file path
	 */
	CppLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
		this.enterScope(this.fileModel);
	}

	/**
	 * Listener for parsing a class declaration. Appends class model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
		System.out.println("Entered class: " + ctx.classhead().classheadname().classname().Identifier().getText());
		ClassModel classModel = new ClassModel();
		this.scopeStack.peek().addDataInModel(classModel);
		this.enterScope(classModel);
		System.out.println("Adding private access specifier");
		AccessSpecifierModel accessSpecifierModel = new AccessSpecifierModel("private");
		this.scopeStack.peek().addDataInModel(accessSpecifierModel);
		this.enterScope(accessSpecifierModel);
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterClassspecifier.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			this.exitScope();
			System.out.println("Exited access specifier scope");
		} else {
			System.err.println("Couldn't find access specifier in class!");
		}
		this.exitScope();
		System.out.println("Exited class: " + ctx.classhead().classheadname().classname().Identifier().getText());
	}

	/**
	 * Listener for parsing a class declaration. Appends class model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterAccessspecifier(CPP14Parser.AccessspecifierContext ctx) {
		String name = ctx.getText();
		System.out.println("Entered accessspecifer: " + name);
		// Currently in AccessSpecifierModel.
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			System.out.println("Inside access specifier scope");
			// Found a different specifier than the current one.
			if (((AccessSpecifierModel) this.scopeStack.peek()).getName() != name) {
				System.out.println("Found different access specifier scope! Exiting current");
				// Exit it!
				this.exitScope();
				// Within a class model.
				if (this.scopeStack.peek() instanceof ClassModel) {
					System.out.println("Class scope as current!");
					ClassModel classModel = (ClassModel) this.scopeStack.peek();

					for (AccessSpecifierModel asm : classModel.getAccessSpecifiers()) {
						System.out.println("Class: " + classModel.getName() + " | " + asm.getName());
					}

					// Get exsiting access specifier model with name
					AccessSpecifierModel asm = classModel.getAccessSpecifier(name);

					if (asm == null) { // New specifier, add it and set as current scope.
						System.out.println("NEW ACCESS SPECIFIER with name: " + name);
						asm = new AccessSpecifierModel(name);
						this.scopeStack.peek().addDataInModel(asm);
					}

					System.out.println("Entering access specifier with name: " + asm.getName());
					this.enterScope(asm);
				}
			}
		}
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterClassspecifier.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitAccessspecifier(CPP14Parser.AccessspecifierContext ctx) {
		//
	}

	/**
	 * Listener for parsing a class head name. Adds name to class scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterClassname(CPP14Parser.ClassnameContext ctx) {
		System.out.println("Entered Classheadname: " + ctx.getText());
		String className = "";

		if (ctx.Identifier() != null) {
			className = ctx.Identifier().getText();
		} else if (ctx.simpletemplateid() != null) {
			className = ctx.simpletemplateid().getText();
		} else {
			System.err.println("Couldn't find name of class!");
		}

		if (this.scopeStack.peek() instanceof AccessSpecifierModel && className != "") {
			AccessSpecifierModel asm = (AccessSpecifierModel) this.exitScope();

			if (this.scopeStack.peek() instanceof ClassModel) {
				this.scopeStack.peek().addDataInModel(className);
			}
			this.enterScope(asm);
		}
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterClassname.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitClassname(CPP14Parser.ClassnameContext ctx) {
		System.out.println("Exited Classheadname: " + ctx.getText());
	}

	/**
	 * Listener for parsing a method/function declaration. Adding function name to
	 * filemodel.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
		// Get interval between function start and end of function name.
		Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());

		// Get the input stream of function definition rule.
		CharStream input = ctx.start.getInputStream();

		FunctionModel functionModel = new FunctionModel(input.getText(interval));
		this.enterScope(functionModel);
		System.out.println(this.scopeStack.size() + " : " + this.scopeStack.peek().getClass());
		System.out.println(ctx.getText());
		/*
		 * // Get interval between function start and end of function name. Interval
		 * interval = new Interval(ctx.start.getStartIndex(),
		 * ctx.declarator().stop.getStopIndex());
		 * 
		 * // Get the input stream of function definition rule. CharStream input =
		 * ctx.start.getInputStream();
		 * 
		 * JSONObject functionMetaData = new JSONObject(); functionMetaData =
		 * fetchMetaData(ctx);
		 * 
		 * // Set this function model with name, declaratorid, scope(if exists),
		 * lineStart and lineEnd. FunctionModel functionModel = new
		 * FunctionModel(input.getText(interval),
		 * functionMetaData.getString("declarator")); if(functionMetaData.has("scope")){
		 * functionModel.setScope(functionMetaData.getString("scope")); }
		 * 
		 * if(functionMetaData.has("parameters")){ Iterator i =
		 * functionMetaData.getJSONArray("parameters").iterator();
		 * 
		 * while (i.hasNext()) { JSONObject variable = (JSONObject) i.next();
		 * functionModel.addParameter( new VariableModel( (String)variable.get("name"),
		 * (String)variable.get("type") ) ); } }
		 * functionModel.setLineStart(ctx.functionbody().start.getLine());
		 * functionModel.setLineEnd(ctx.functionbody().stop.getLine());
		 * 
		 * this.scopeStack.peek().addDataInModel(functionModel);
		 * this.enterScope(functionModel);
		 */
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterFunctiondefinition.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			this.scopeStack.peek().addDataInModel((FunctionModel) model);
		} else {
			this.enterScope(model);
			System.out.println("Could not find function model in scope stack on exitFunctiondefinition.");
			System.exit(1);
		}
	}

	/**
	 * Listener for parsing variables from scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterStatement(CPP14Parser.StatementContext ctx) {
		CPP14Parser.DeclarationstatementContext declStatement = ctx.declarationstatement();
		CPP14Parser.BlockdeclarationContext blockdeclaration = null;
		CPP14Parser.InitdeclaratorlistContext initDeclaratorlist = null;
		String variableName = "";
		String variableType = "";
		if (declStatement != null) {

			if (declStatement.blockdeclaration() != null) {
				blockdeclaration = declStatement.blockdeclaration();

				if (blockdeclaration.simpledeclaration() != null) {

					if (blockdeclaration.simpledeclaration().declspecifierseq() != null) {
						variableType = blockdeclaration.simpledeclaration().declspecifierseq().getText();

						if (blockdeclaration.simpledeclaration().initdeclaratorlist() != null) {
							initDeclaratorlist = blockdeclaration.simpledeclaration().initdeclaratorlist();

							while (initDeclaratorlist != null) {
								if (initDeclaratorlist.initdeclarator().declarator() == null) {
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
	 * @param ctx The parsing context
	 */
	@Override
	public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
		NamespaceModel namespace = new NamespaceModel(ctx.Identifier().getText());

		this.scopeStack.peek().addDataInModel(namespace);
		this.enterScope(namespace);
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterOriginalnamespacedefinition.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
		this.exitScope();
	}

	/**
	 * Listener for parsing a using namespace declaration. Adding namespace to
	 * filemodel.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) {
		UsingNamespaceModel usingNamespaceModel;
		if (ctx.nestednamespecifier() != null) {
			usingNamespaceModel = new UsingNamespaceModel(
					ctx.nestednamespecifier().getText() + ctx.namespacename().getText(), ctx.getStart().getLine());
		} else {
			usingNamespaceModel = new UsingNamespaceModel(ctx.namespacename().getText(), ctx.getStart().getLine());
		}

		this.scopeStack.peek().addDataInModel(usingNamespaceModel);
	}

	/**
	 * Listener for parsing function calls.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) {
		this.scopeStack.peek().addDataInModel(ctx.getText());
	}

	/*
	 * /** Fetches a meta data of the function.
	 *
	 * @param ctx The context
	 *
	 * @return return JSON object containing list of parameters, declrator ID, and
	 * scope function is from.
	 * 
	 * private JSONObject fetchMetaData(CPP14Parser.FunctiondefinitionContext ctx){
	 * JSONObject functionMetaData = new JSONObject();
	 * CPP14Parser.PtrdeclaratorContext ptrdeclarator = null;
	 * CPP14Parser.NoptrdeclaratorContext noptrdeclarator = null;
	 * 
	 * if (ctx.declarator().ptrdeclarator() != null){ ptrdeclarator =
	 * ctx.declarator().ptrdeclarator(); } else { noptrdeclarator =
	 * ctx.declarator().noptrdeclarator(); }
	 * 
	 * while(true){ if(ptrdeclarator != null){ if (ptrdeclarator.ptrdeclarator() !=
	 * null){ ptrdeclarator = ptrdeclarator.ptrdeclarator();
	 * 
	 * 
	 * }else if(ptrdeclarator.noptrdeclarator() != null){ noptrdeclarator =
	 * ptrdeclarator.noptrdeclarator();
	 * 
	 * while(true){ if (noptrdeclarator.declaratorid() != null) {
	 * functionMetaData.put("declarator",
	 * noptrdeclarator.declaratorid().idexpression().getText());
	 * if(noptrdeclarator.declaratorid().idexpression().qualifiedid() != null){
	 * functionMetaData.put("scope",
	 * noptrdeclarator.declaratorid().idexpression().qualifiedid().
	 * nestednamespecifier().getText()); functionMetaData.put("declarator",
	 * noptrdeclarator.declaratorid().idexpression().qualifiedid().unqualifiedid().
	 * getText()); }
	 * 
	 * return functionMetaData; }else if (noptrdeclarator.noptrdeclarator() != null)
	 * {
	 * 
	 * functionMetaData.put("parameters", fetchParameters(noptrdeclarator));
	 * noptrdeclarator = noptrdeclarator.noptrdeclarator(); continue; }else if
	 * (noptrdeclarator.ptrdeclarator() != null) { ptrdeclarator =
	 * noptrdeclarator.ptrdeclarator(); break; }else{
	 * System.out.println("Could not understand simple functions"); System.exit(1);
	 * } } }else{ System.out.println("Could not understand simple functions");
	 * System.exit(1); } } else { if(noptrdeclarator.declaratorid() != null){
	 * functionMetaData.put("declarator", noptrdeclarator.declaratorid().getText());
	 * return functionMetaData; }else if (noptrdeclarator.noptrdeclarator() != null)
	 * { noptrdeclarator = noptrdeclarator.noptrdeclarator(); }else if
	 * (noptrdeclarator.ptrdeclarator() != null) { ptrdeclarator =
	 * noptrdeclarator.ptrdeclarator(); }else{
	 * System.out.println("Could not understand simple functions"); System.exit(1);
	 * } } } }
	 */
	@Override
	public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		this.enterScope(new VariableModel());
		System.out.println("Enter Parameter decl: " + this.scopeStack.size());
		System.out.println("Enter Parameter decl: " + ctx.getText());
	}

	@Override
	public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			FunctionModel func = (FunctionModel) model;
			func.addParameter(vm);
			this.enterScope(func);
			System.out.println("Exit Parameter decl: " + this.scopeStack.size());
		} else {
			this.enterScope(model);
			System.out.println(ctx.getText());
			System.out.println("Could not understand parent model for parameter declaration.");
			// System.exit(1);
		}
		System.out.println("\n");
	}

	@Override
	public void enterDeclarator(CPP14Parser.DeclaratorContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
			System.out.println("Decl: " + this.scopeStack.size());

		}
		if (ctx.noptrdeclarator() != null) {
			System.out.println("Ptrdeclarator: " + ctx.getText());
		}
	}

	@Override
	public void enterDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setType(ctx.getText());
			this.enterScope(vm);
			System.out.println("seq: " + this.scopeStack.size());
		}
	}

	@Override
	public void enterAbstractdeclarator(CPP14Parser.AbstractdeclaratorContext ctx) {
		System.out.println("abstractdeclarator: " + ctx.getText());

		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
			System.out.println(this.scopeStack.size());
		}
	}

	/**
	 * Gets the parsed code as JSONObject.
	 *
	 * @return The parsed code.
	 */
	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		return parsedCode.put("file", this.fileModel.getParsedCode());
	}

	/**
	 * Gets the file model.
	 *
	 * @return The file model.
	 */
	public FileModel getFileModel() {
		return this.fileModel;
	}
}