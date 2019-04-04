package me.codvis.ast;

import me.codvis.ast.AccessSpecifierModel;
import me.codvis.ast.DeclaratorListModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.parser.CPP14BaseListener;
import me.codvis.ast.parser.CPP14Parser;
import me.codvis.ast.parser.CPP14Lexer;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import java.util.*;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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

		ClassModel classModel = new ClassModel();
		this.scopeStack.peek().addDataInModel(classModel);
		this.enterScope(classModel);
		// Add default access specifier, private for class, public for union and struct.
		AccessSpecifierModel accessSpecifierModel = null;
		if (ctx.classhead().classkey().Class() != null) {
			accessSpecifierModel = new AccessSpecifierModel("private");
		} else if (ctx.classhead().classkey().Union() != null || ctx.classhead().classkey().Struct() != null) {
			accessSpecifierModel = new AccessSpecifierModel("public");
		} else { // Wrong class key found.
			System.err.println("Not correct Class specifier");
		}
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
		// Is there an access specfier, exit it.
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			this.exitScope();
		} else { // No access specfier.
			System.err.println("Couldn't find access specifier in class");
		}
		this.exitScope();
	}

	/**
	 * Listener for parsing a class declaration. Appends class model.
	 *
	 * @param ctx The parsing context
	 */

	@Override
	public void enterAccessspecifier(CPP14Parser.AccessspecifierContext ctx) {
		String name = ctx.getText();
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			// Found a different specifier than the current one.
			if (((AccessSpecifierModel) this.scopeStack.peek()).getName() != name) {
				this.exitScope();
				if (this.scopeStack.peek() instanceof ClassModel) {
					ClassModel classModel = (ClassModel) this.scopeStack.peek();
					// Get exsiting access specifier model with name
					AccessSpecifierModel asm = classModel.getAccessSpecifier(name);
					// New specifier, add it and set as current scope.
					if (asm == null) {
						asm = new AccessSpecifierModel(name);
						this.scopeStack.peek().addDataInModel(asm);
					}
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
		String className = "";
		if (ctx.Identifier() != null) {
			className = ctx.Identifier().getText();
		} else if (ctx.simpletemplateid() != null) {
			className = ctx.simpletemplateid().getText();
		} else {
			System.err.println("Couldn't find name of class!");
		}

		// Inside AccessSpecifierModel, update ClassModel underneath if existant.
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
		//
	}

	/**
	 * Listener for parsing a class member declaration.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {

		if (ctx.declspecifierseq() != null && ctx.memberdeclaratorlist() != null) {

			if (ctx.memberdeclaratorlist().memberdeclaratorlist() == null) {
				if (ctx.getText().contains("(") && ctx.getText().contains(")")) {
					FunctionModel functionModel = new FunctionModel("");
					functionModel.setLineStart(ctx.getStart().getLine());
					functionModel.setLineEnd(ctx.getStop().getLine());
					this.enterScope(functionModel);
				} else {
					this.enterScope(new VariableModel());
				}
			} else {
				this.enterScope(new DeclaratorListModel());
			}
		} else {
			System.err.println("Uncaught member declaration at: " + ctx.getStart().getLine());
		}
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterMemberdeclaration.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
		if (ctx.declspecifierseq() != null && ctx.memberdeclaratorlist() != null) {
			Model model = this.exitScope();
			if (model instanceof DeclaratorListModel) {
				DeclaratorListModel declaratorList = (DeclaratorListModel) model;
				for (Iterator<String> i = declaratorList.getVariables().iterator(); i.hasNext();) {
					String variableName = i.next();
					VariableModel vm = new VariableModel(variableName, declaratorList.getType());
					vm.trimType();
					this.scopeStack.peek().addDataInModel(vm);
				}

				for (Iterator<String> i = declaratorList.getFunctions().iterator(); i.hasNext();) {
					String functionName = i.next();
					FunctionModel func = new FunctionModel(declaratorList.getType() + " " + functionName);
					func.setLineStart(ctx.getStart().getLine());
					func.setLineEnd(ctx.getStop().getLine());
					this.scopeStack.peek().addDataInModel(func);
				}
			} else {
				this.scopeStack.peek().addDataInModel(model);
			}
		}
	}

	@Override
	public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
		// Get interval between function start and end of function name.
		Interval interval = new Interval(ctx.start.getStartIndex(), ctx.declarator().stop.getStopIndex());

		// Get the input stream of function definition rule.
		CharStream input = ctx.start.getInputStream();

		FunctionModel functionModel = new FunctionModel(input.getText(interval));
		functionModel.setLineStart(ctx.functionbody().start.getLine());
		functionModel.setLineEnd(ctx.functionbody().stop.getLine());
		this.enterScope(functionModel);

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
			System.err.println("Could not understand parent model for function definition.");
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
		this.enterScope(new CallModel());
	}

	/**
	 * Listener for exiting the current scope.
	 *
	 * TODO: Function call in combination with "=" is not captured. e.g. HelloWorld*
	 * hw = new HelloWorld();
	 *
	 * @param ctx The context
	 */
	@Override
	public void exitExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) {
		CallModel call = (CallModel) this.exitScope();

		String context = ctx.getText();
		int countParantheses = 0;
		int startParantheses = 0;
		int endParantheses = 0;

		for (int i = context.length() - 1; i >= 0; i--) {
			char character = context.charAt(i);

			if (character == ')') {
				countParantheses++;
				if (endParantheses == 0) {
					endParantheses = i;
				}

			} else if (character == '(') {
				countParantheses--;
				if (countParantheses == 0) {
					startParantheses = i;

					break;
				}
			}
		}

		if (startParantheses != endParantheses) {

			if (!context.subSequence(startParantheses, endParantheses + 2).toString().contains("=")
					&& !ctx.getText().contains("=")) {

				CharSequence parameterList = context.subSequence(startParantheses, endParantheses + 2);

				context = context.replace(parameterList, "");

				String[] splitContext = context.split("(::)|(->)|(\\.)");

				for (int i = 0; i < splitContext.length - 1; i++) {
					call.addScopeIdentifier(splitContext[i]);
				}
				if (parameterList != null) {
					call.setIdentifier(splitContext[splitContext.length - 1] + parameterList);
				}

				if (this.scopeStack.peek() instanceof FunctionBodyModel) {
					this.scopeStack.peek().addDataInModel(call);
				} else {
					System.err.println("Could not understand parent model for expression statement.");
				}
			}
		}
	}

	/**
	 * Listener for adding variable declaration into the scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {

		if (!ctx.getText().contains("class") && !ctx.getText().contains("union") && !ctx.getText().contains("struct")) {
			this.enterScope(new DeclaratorListModel());
		}
	}

	/**
	 * Listener for exiting the current scope, and add data in model parent scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
		if (!ctx.getText().contains("class") && !ctx.getText().contains("union") && !ctx.getText().contains("struct")) {

			DeclaratorListModel declaratorList = (DeclaratorListModel) this.exitScope();
			for (Iterator<String> i = declaratorList.getVariables().iterator(); i.hasNext();) {
				String variableName = i.next();
				VariableModel vm = new VariableModel(variableName, declaratorList.getType());
				vm.trimType();
				this.scopeStack.peek().addDataInModel(vm);
			}

			for (Iterator<String> i = declaratorList.getFunctions().iterator(); i.hasNext();) {
				String functionName = i.next();
				FunctionModel func = new FunctionModel(declaratorList.getType() + " " + functionName);
				func.setLineStart(ctx.getStart().getLine());
				func.setLineEnd(ctx.getStop().getLine());
				this.scopeStack.peek().addDataInModel(func);
			}
		}
	}

	/**
	 * Determines if ctx is a variable.
	 *
	 * @param ctx The parsing context
	 *
	 * @return True if variable, False otherwise.
	 */
	protected boolean isVariable(CPP14Parser.DeclaratorContext ctx) {

		if (ctx.ptrdeclarator() != null) {
			if (ctx.ptrdeclarator().noptrdeclarator() != null) {
				if (ctx.ptrdeclarator().noptrdeclarator().declaratorid() != null) {
					return true;
				}
			} else if (ctx.ptrdeclarator().ptrdeclarator() != null) {
				if (ctx.ptrdeclarator().ptrdeclarator().noptrdeclarator() != null) {
					if (ctx.ptrdeclarator().ptrdeclarator().noptrdeclarator().declaratorid() != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Listener for parsing function body.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
		this.enterScope(new FunctionBodyModel());
	}

	/**
	 * Listener for exiting the current scope and adding data into function model
	 * scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
		FunctionBodyModel functionBody = (FunctionBodyModel) this.exitScope();
		this.scopeStack.peek().addDataInModel(functionBody);
	}

	/**
	 * Listener for parsing declaratorid for function model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterDeclaratorid(CPP14Parser.DeclaratoridContext ctx) {
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			FunctionModel func = (FunctionModel) model;
			func.setDeclaratorId(ctx.getText());
			this.enterScope(func);
		} else {
			this.enterScope(model);
			System.err.println("Could not understand parent model for declarator id. ");
		}
	}

	/**
	 * Listener for parsing scope of a function.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterQualifiedid(CPP14Parser.QualifiedidContext ctx) {
		if (ctx.nestednamespecifier() != null) {
			Model model = this.exitScope();
			if (model instanceof FunctionModel) {
				FunctionModel func = (FunctionModel) model;
				func.setScope(ctx.nestednamespecifier().getText());
				this.enterScope(func);
			} else {
				this.enterScope(model);
				System.err.println("Could not understand parent model for scope id.");

			}
		}
	}

	/**
	 * Listener for parsing parameters.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		this.enterScope(new VariableModel());
	}

	/**
	 * Listener for existing the current scope and add data in function model scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			FunctionModel func = (FunctionModel) model;
			vm.trimType();
			func.addParameter(vm);
			this.enterScope(func);
		} else {
			this.enterScope(model);
			System.err.println("Could not understand parent model for parameter declaration.");
		}
	}

	/**
	 * Listener for parsing declarators.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterDeclarator(CPP14Parser.DeclaratorContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.addName(ctx.getText());
			this.enterScope(vlm);
		} else if (this.scopeStack.peek() instanceof DeclaratorListModel) {
			DeclaratorListModel declaratorList = (DeclaratorListModel) this.scopeStack.pop();
			if (isVariable(ctx)) {
				declaratorList.addVariable(ctx.getText());
			} else {
				declaratorList.addFunction(ctx.getText());
			}
			this.enterScope(declaratorList);
		} else if (this.scopeStack.peek() instanceof FunctionModel) {
			FunctionModel functionModel = (FunctionModel) this.exitScope();
			functionModel.setName(ctx.getText());
			this.enterScope(functionModel);
		} else {
			System.err.println("Could not understand parent model for declarator.");
		}
	}

	@Override
	public void enterDeclspecifier(CPP14Parser.DeclspecifierContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.applyModifierOnType(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.applyModifierOnType(ctx.getText());
			this.enterScope(vlm);
		} else if (this.scopeStack.peek() instanceof DeclaratorListModel) {
			this.scopeStack.peek().addDataInModel(ctx.getText());
		} else {
			System.err.println("Could not understand parent model for variable modifier. " + ctx.getStart().getLine());
		}
	}

	/**
	 * Listener for parsing abstract declarator.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterAbstractdeclarator(CPP14Parser.AbstractdeclaratorContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
		} else {
			System.err.println("Could not understand parent model for abstract declarator.");
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
