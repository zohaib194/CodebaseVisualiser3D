package me.codvis.ast;

import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.json.JSONObject;

import org.antlr.v4.runtime.ParserRuleContext;

import me.codvis.ast.parser.Java9Parser;
import me.codvis.ast.AccessSpecifierModel;
import me.codvis.ast.CallModel;
import me.codvis.ast.ClassModel;
import me.codvis.ast.FileModel;
import me.codvis.ast.FunctionBodyModel;
import me.codvis.ast.FunctionModel;
import me.codvis.ast.Model;
import me.codvis.ast.NamespaceModel;
import me.codvis.ast.UsingNamespaceModel;
import me.codvis.ast.VariableListModel;
import me.codvis.ast.VariableModel;

/**
 * Class for exstending listeners and parsing code requiered for initial 3D view
 * for code abstraction.
 */
public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath to file being parsed.
	 *
	 * @param filePath The file path
	 */
	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
		this.enterScope(this.fileModel);
	}

	@Override
	public void enterNormalClassDeclaration(Java9Parser.NormalClassDeclarationContext ctx) {
		// Handle modifier list.
		this.handleModifier(ctx.classModifier());

		ClassModel classModel = new ClassModel();
		this.scopeStack.peek().addDataInModel(classModel);
		this.enterScope(classModel);

		classModel.setName(ctx.identifier().getText());

		// Extends a class
		if (ctx.superclass() != null) {
			classModel.addParent(ctx.superclass().classType().identifier().getText());
		}

		AccessSpecifierModel accessSpecifierModel = new AccessSpecifierModel("private");
		classModel.addDataInModel(accessSpecifierModel);
		this.enterScope(accessSpecifierModel);
	}

	@Override
	public void exitNormalClassDeclaration(Java9Parser.NormalClassDeclarationContext ctx) {
		// Is there an access specfier, exit it.
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			this.exitScope();
		} else { // No access specfier.
			System.err.println("Couldn't find access specifier in class");
		}
		this.exitScope();
	}

	@Override
	public void enterFieldDeclaration(Java9Parser.FieldDeclarationContext ctx) {
		// Handle modifier list.
		this.handleModifier(ctx.fieldModifier());
		this.enterScope(new VariableModel());
	}

	@Override
	public void exitFieldDeclaration(Java9Parser.FieldDeclarationContext ctx) {
		// Handle modifier list.
		VariableModel variableModel = (VariableModel) this.exitScope();
		this.scopeStack.peek().addDataInModel(variableModel);
	}

	@Override
	public void enterInterfaceDeclaration(Java9Parser.InterfaceDeclarationContext ctx) {
		// Handle modifier list.
		if (ctx.normalInterfaceDeclaration() != null) {
			this.handleModifier(ctx.normalInterfaceDeclaration().interfaceModifier());
		}
	}

	private <T extends ParserRuleContext> void handleModifier(List<T> modifiers) {
		// There's no modifiers, default to private.
		if (modifiers == null) {
			this.handleAccessSpecifer("private");
			return;
		}
		for (Iterator<T> i = modifiers.iterator(); i.hasNext();) {
			T modifier = i.next();
			// Only modifiers from interface, class, method or variable.
			if ((modifier instanceof Java9Parser.InterfaceModifierContext
					&& ((Java9Parser.InterfaceModifierContext) modifier).annotation() == null)
					|| (modifier instanceof Java9Parser.MethodModifierContext
							&& ((Java9Parser.MethodModifierContext) modifier).annotation() == null)
					|| (modifier instanceof Java9Parser.ClassModifierContext
							&& ((Java9Parser.ClassModifierContext) modifier).annotation() == null)
					|| (modifier instanceof Java9Parser.FieldModifierContext
							&& ((Java9Parser.FieldModifierContext) modifier).annotation() == null)) {
				String name = modifier.getText();
				// Filter access specifiers form modifiers.
				if (name.equals("private") || name.equals("public") || name.equals("protected")) {
					// Handle access specifier.
					this.handleAccessSpecifer(name);
					return;
				}
			} else {
				System.err.println("Illegal modifier type given");
			}
		}
	}

	private void handleAccessSpecifer(String name) {
		if (this.scopeStack.peek() instanceof AccessSpecifierModel) {
			// Found a different specifier than the current one.
			if (((AccessSpecifierModel) this.scopeStack.peek()).getName() != name) {
				if (this.scopeStack.peek() instanceof ClassModel) {
					// Get exsiting access specifier model with name.
					ClassModel classModel = (ClassModel) this.scopeStack.peek();
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
	 * Listener for parsing a method/function declaration. Adding function name to
	 * filemodel.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
		// Handle modifier list.
		this.handleModifier(ctx.methodModifier());

		// Get interval between function
		// and start and end of function name.
		Interval interval = new Interval(ctx.methodHeader().start.getStartIndex(),
				ctx.methodHeader().stop.getStopIndex());

		// Get the input stream of function definition rule.
		CharStream input = ctx.start.getInputStream();
		FunctionModel functionModel = new FunctionModel(input.getText(interval),
				ctx.methodHeader().methodDeclarator().identifier().getText());
		functionModel.setLineStart(ctx.methodBody().start.getLine());
		functionModel.setLineEnd(ctx.methodBody().stop.getLine());
		this.enterScope(functionModel);
	}

	/**
	 * Listener for exiting the current scope, expecting that scope to be one
	 * entered by enterMethodDeclaration.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
		Model model = this.exitScope();

		if (model instanceof FunctionModel) {
			this.scopeStack.peek().addDataInModel((FunctionModel) model);
		} else {
			this.enterScope(model);
			System.err.println("Could not understand parent model for method declaration.");
		}
	}

	@Override
	public void enterMethodBody(Java9Parser.MethodBodyContext ctx) {
		this.enterScope(new FunctionBodyModel());
	}

	@Override
	public void exitMethodBody(Java9Parser.MethodBodyContext ctx) {
		FunctionBodyModel functionBody = (FunctionBodyModel) this.exitScope();
		this.scopeStack.peek().addDataInModel(functionBody);
	}

	@Override
	public void enterResult(Java9Parser.ResultContext ctx){
		FunctionModel functionModel = (FunctionModel) this.exitScope();
		functionModel.setReturnType(ctx.getText());
		this.enterScope(functionModel);
	}


	/**
	 * Listener for parsing a package/namespace declaration. Adding package name to
	 * filemodel.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterPackageDeclaration(Java9Parser.PackageDeclarationContext ctx) {
		NamespaceModel namespace = new NamespaceModel(ctx.packageName().getText());

		this.scopeStack.peek().addDataInModel(namespace);
		this.enterScope(namespace);
	}

	/**
	 * Listener for parsing a package/namespace import. Adding package name to
	 * filemodel.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterImportDeclaration(Java9Parser.ImportDeclarationContext ctx) {
		Java9Parser.SingleStaticImportDeclarationContext importSingle = ctx.singleStaticImportDeclaration();
		Java9Parser.StaticImportOnDemandDeclarationContext importOnDemand = ctx.staticImportOnDemandDeclaration();
		Java9Parser.SingleTypeImportDeclarationContext importSingleType = ctx.singleTypeImportDeclaration();
		Java9Parser.TypeImportOnDemandDeclarationContext importTypeOnDemand = ctx.typeImportOnDemandDeclaration();

		UsingNamespaceModel usingNamespaceModel;

		if (importSingle != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingle.typeName().getText(),
					importSingle.typeName().getStart().getLine());

		} else if (importOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importOnDemand.typeName().getText(),
					importOnDemand.typeName().getStart().getLine());

		} else if (importSingleType != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingleType.typeName().getText(),
					importSingleType.typeName().getStart().getLine());

		} else if (importTypeOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importTypeOnDemand.packageOrTypeName().getText(),
					importTypeOnDemand.packageOrTypeName().getStart().getLine());

		} else {
			System.err.println("Unhandeled using dirctive");
			return;
		}

		this.scopeStack.peek().addDataInModel(usingNamespaceModel);
	}

	/**
	 * Listener for parsing function calls.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterMethodInvocation(Java9Parser.MethodInvocationContext ctx) {
		this.enterScope(new CallModel());

	}

	@Override
	public void exitMethodInvocation(Java9Parser.MethodInvocationContext ctx) {
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

		CharSequence parameterList = context.subSequence(startParantheses, endParantheses + 1);

		context = context.replace(parameterList, "");

		String[] splitContext = context.split("\\.");

		for (int i = 0; i < splitContext.length - 1; i++) {
			call.addScope(new ScopeModel(splitContext[i], "class"));
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

	/**
	 * Listener for adding local variables in to the scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterLocalVariableDeclarationStatement(Java9Parser.LocalVariableDeclarationStatementContext ctx) {
		this.enterScope(new VariableListModel());
	}

	/**
	 * Listener for exiting the variable scope and add data in to parent model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitLocalVariableDeclarationStatement(Java9Parser.LocalVariableDeclarationStatementContext ctx) {

		VariableListModel variableList = (VariableListModel) this.exitScope();

		if (this.scopeStack.peek() instanceof FunctionBodyModel || this.scopeStack.peek() instanceof FileModel
				|| this.scopeStack.peek() instanceof NamespaceModel) {

			for (Iterator<String> i = variableList.getNames().iterator(); i.hasNext();) {
				String variableName = i.next();
				VariableModel vm = new VariableModel(variableName, variableList.getType());
				vm.trimType();
				this.scopeStack.peek().addDataInModel(vm);
			}

		} else {
			System.err.println("Could not understand parent model for simple declaration.");
		}
	}

	/**
	 * Listener for adding formal parameters into the scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterFormalParameter(Java9Parser.FormalParameterContext ctx) {
		this.enterScope(new VariableModel());
	}

	/**
	 * Listener for exiting parameter scope and adding data into the parent model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitFormalParameter(Java9Parser.FormalParameterContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			FunctionModel func = (FunctionModel) model;
			vm.trimType();
			func.addParameter(vm);
			this.enterScope(func);
		} else {
			this.enterScope(model);
			System.err.println("Could not understand parent model for formal parameter.");
		}

	}

	/**
	 * Listener for adding last formal parameter into the scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterLastFormalParameter(Java9Parser.LastFormalParameterContext ctx) {
		if (ctx.variableDeclaratorId() != null) {
			this.enterScope(new VariableModel());
		}
	}

	/**
	 * Listener for exiting the last formal parameter scope and adding data into
	 * parent model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitLastFormalParameter(Java9Parser.LastFormalParameterContext ctx) {
		if (ctx.variableDeclaratorId() != null) {
			VariableModel vm = (VariableModel) this.exitScope();
			Model model = this.exitScope();
			if (model instanceof FunctionModel) {
				FunctionModel func = (FunctionModel) model;
				vm.trimType();
				func.addParameter(vm);
				this.enterScope(func);
			} else {
				this.enterScope(model);
				System.err.println("Could not understand parent model for last formal parameter.");
			}
		}
	}

	/**
	 * Listener for adding receiver parameter into the scope.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterReceiverParameter(Java9Parser.ReceiverParameterContext ctx) {
		this.enterScope(new VariableModel());
	}

	/**
	 * Listener for exiting the receiver parameter scope and adding data into the
	 * parent model.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void exitReceiverParameter(Java9Parser.ReceiverParameterContext ctx) {
		VariableModel vm = (VariableModel) this.exitScope();
		Model model = this.exitScope();
		if (model instanceof FunctionModel) {
			FunctionModel func = (FunctionModel) model;
			if (vm.hasName()) {
				vm.trimType();
				func.addParameter(vm);
			} else {
				vm.setName("this");
				vm.trimType();
				func.addParameter(vm);
			}
			this.enterScope(func);
		} else {
			this.enterScope(model);
			System.err.println("Could not understand parent model for receiver parameter.");
		}
	}

	/**
	 * Listener for parsing variable id.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterVariableDeclaratorId(Java9Parser.VariableDeclaratorIdContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.setName(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.addName(ctx.getText());
			this.enterScope(vlm);
		} else {
			System.err.println("Could not understand parent model for identifier.");
		}
	}

	/**
	 * Listener for parsing primitive type.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterUnannType(Java9Parser.UnannTypeContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.applyUnnanTypeOnType(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.applyUnnanTypeOnType(ctx.getText());
			this.enterScope(vlm);
		} else {
			System.err.println("Could not understand parent model for unannType.");
		}
	}

	/**
	 * Listener for parsing variable modifiers.
	 *
	 * @param ctx The parsing context
	 */
	@Override
	public void enterVariableModifier(Java9Parser.VariableModifierContext ctx) {
		if (this.scopeStack.peek() instanceof VariableModel) {
			VariableModel vm = (VariableModel) this.exitScope();
			vm.applyModifierOnType(ctx.getText());
			this.enterScope(vm);
		} else if (this.scopeStack.peek() instanceof VariableListModel) {
			VariableListModel vlm = (VariableListModel) this.exitScope();
			vlm.applyModifierOnType(ctx.getText());
			this.enterScope(vlm);
		} else {
			System.err.println("Could not understand parent model for variable modifier.");
		}
	}

	/**
	 * Gets the parsed code as JSONObject. Adding the file content as part of the
	 * package being declared at start of file.
	 *
	 * @return The parsed code.
	 */
	public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

		return parsedCode.put("file", this.fileModel.getParsedCode());
	}
}