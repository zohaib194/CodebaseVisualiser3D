package me.codvis.ast;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import org.json.JSONObject;

/**
 * Class for exstending listeners and parsing code requiered for initial 3D view for code abstraction.
 */
public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;

	/**
	 * Constructs the object, setting the filepath to file being parsed.
	 *
	 * @param      filePath  The file path
	 */
	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

	/**
	 * Listener for parsing a method/function declaration. Adding function name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
    @Override 
    public void enterMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {  //see gramBaseListener for allowed functions

	    // Set this function model with name, lineStart and lineEnd.
      	FunctionModel functionModel = new FunctionModel(ctx.methodHeader().methodDeclarator().getText());
      	functionModel.setLineStart(ctx.methodBody().start.getLine());
      	functionModel.setLineEnd(ctx.methodBody().stop.getLine());
	    
	    int index = fileModel.addModelInCurrentScope(functionModel, (Stack<ModelIdentifier>)this.scopeStack.clone());
	    this.enterScope(new ModelIdentifier("functions", index));
    }

    /**
     * Listener for parsing a class declaration.
     *
     * @param      ctx   The parsing context
     */
    @Override public void enterClassDeclaration(Java9Parser.ClassDeclarationContext ctx) {
        String className = "";
        ClassModel classModel;

        // Parse class name.
        for(Java9Parser.ClassModifierContext classModifier : ctx.normalClassDeclaration().classModifier()) {
            className = className + " " + classModifier.getText();
        }
        
        // Create class model and give the class name.
        classModel = new ClassModel(className + " " + ctx.normalClassDeclaration().identifier().getText());

       //System.out.println("Class : " + classModifiers + " class " + ctx.normalClassDeclaration().identifier().getText());
        
        for (Java9Parser.ClassBodyDeclarationContext declaration  : ctx.normalClassDeclaration().classBody().classBodyDeclaration()) {
            //System.out.println("Declaration : " + declaration.classMemberDeclaration().getText());

            if(declaration.classMemberDeclaration() != null && declaration.classMemberDeclaration().fieldDeclaration() != null){
                // Parse field modifier (static/final/volatile/..)
                List<Java9Parser.FieldModifierContext> modifiersContext = declaration.classMemberDeclaration().fieldDeclaration().fieldModifier();
                String modifierNames = "";
                for (Java9Parser.FieldModifierContext tempModifier : modifiersContext ) {
                    if( !tempModifier.getText().contains("public") && 
                        !tempModifier.getText().contains("private") &&
                        !tempModifier.getText().contains("protected")
                    ) {
                        
                        modifierNames += tempModifier.getText() + " ";
                    }
                        
                }

                if(modifiersContext.size() > 0){
                    for (Java9Parser.FieldModifierContext modifier : modifiersContext) {

                        switch (modifier.getText()) {
                            
                            case "public": 
                                classModel.addPublicData(modifierNames + declaration.classMemberDeclaration().fieldDeclaration().unannType().getText()
                                + " " + declaration.classMemberDeclaration().fieldDeclaration().variableDeclaratorList().getText());
                            break;
                            
                            case "private": 
                                classModel.addPrivateData(modifierNames + declaration.classMemberDeclaration().fieldDeclaration().unannType().getText()
                                + " " + declaration.classMemberDeclaration().fieldDeclaration().variableDeclaratorList().getText());
                            break;
                            
                            case "protected":
                                classModel.addProtectedData(modifierNames + declaration.classMemberDeclaration().fieldDeclaration().unannType().getText()
                                + " " + declaration.classMemberDeclaration().fieldDeclaration().variableDeclaratorList().getText());
                            break;

                        }
                    }
                } else {
                    classModel.addPrivateData(declaration.classMemberDeclaration().fieldDeclaration().unannType().getText()
                     + " " + declaration.classMemberDeclaration().fieldDeclaration().variableDeclaratorList().getText());
                }

            } else if (declaration.classMemberDeclaration().methodDeclaration() != null) {

                // Parse method modifier. (public/private/protected/abstract/static/...)
                List<Java9Parser.MethodModifierContext> modifiersContext = declaration.classMemberDeclaration().methodDeclaration().methodModifier();
                String modifierNames = "";
                for (Java9Parser.MethodModifierContext tempModifier : modifiersContext ) {
                    if( !tempModifier.getText().contains("public") && 
                        !tempModifier.getText().contains("private") &&
                        !tempModifier.getText().contains("protected")
                    ) {
                        
                        modifierNames += tempModifier.getText() + " ";
                    }
                        
                }

                String returnType = declaration.classMemberDeclaration().methodDeclaration().methodHeader().result().getText();
                String functionName = declaration.classMemberDeclaration().methodDeclaration().methodHeader().methodDeclarator().getText();

                if(modifiersContext.size() > 0){
                    for (Java9Parser.MethodModifierContext modifier : modifiersContext) {

                        switch (modifier.getText()) {
                            
                            case "public": 
                                classModel.addPublicData(modifierNames + returnType + " " + functionName);
                            break;
                            
                            case "private": 
                                classModel.addPrivateData(modifierNames + returnType + " " + functionName);
                            break;
                            
                            case "protected":
                                classModel.addProtectedData(modifierNames + returnType + " " + functionName);
                            break;

                        }
                    }
                } else {
                    classModel.addPrivateData(returnType + " " + functionName);
                }
            }

        }

        fileModel.addClass(classModel);
    }

    /**
     * Listener for exiting the current scope, expecting that scope to be one entered by enterMethodDeclaration.
     *
     * @param      ctx   The parsing context
     */
    @Override 
    public void exitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) { 
    	this.exitScope();
    }

    /**
     * Listener for parsing a package/namespace declaration. Adding package name to filemodel.
     *
     * @param      ctx   The parsing context
     */

	@Override
	public void enterPackageDeclaration(Java9Parser.PackageDeclarationContext ctx){
		NamespaceModel namespace = new NamespaceModel(ctx.packageName().getText());
		
		int index = fileModel.addModelInCurrentScope(namespace, (Stack<ModelIdentifier>)this.scopeStack.clone());
	    this.enterScope(new ModelIdentifier("namespaces", index));
	}

	/**
	 * Listener for parsing a package/namespace import. Adding package name to filemodel.
	 *
	 * @param      ctx   The parsing context
	 */
	@Override
	public void enterImportDeclaration(Java9Parser.ImportDeclarationContext ctx){
		Java9Parser.SingleStaticImportDeclarationContext importSingle = ctx.singleStaticImportDeclaration();
		Java9Parser.StaticImportOnDemandDeclarationContext importOnDemand = ctx.staticImportOnDemandDeclaration();
		Java9Parser.SingleTypeImportDeclarationContext importSingleType = ctx.singleTypeImportDeclaration();
		Java9Parser.TypeImportOnDemandDeclarationContext importTypeOnDemand = ctx.typeImportOnDemandDeclaration();

		UsingNamespaceModel usingNamespaceModel;

		if ( importSingle != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingle.typeName().getText(), importSingle.typeName().getStart().getLine());
		} else if (importOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importOnDemand.typeName().getText(), importOnDemand.typeName().getStart().getLine());				

		} else if (importSingleType != null) {
			usingNamespaceModel = new UsingNamespaceModel(importSingleType.typeName().getText(), importSingleType.typeName().getStart().getLine());		

		} else if (importTypeOnDemand != null) {
			usingNamespaceModel = new UsingNamespaceModel(importTypeOnDemand.packageOrTypeName().getText(), importTypeOnDemand.packageOrTypeName().getStart().getLine());

		}else{
			System.out.println("Unhandeled using dirctive");
			return;
		}

	    fileModel.addModelInCurrentScope(usingNamespaceModel, (Stack<ModelIdentifier>)this.scopeStack.clone());
	}	

	/**
	 * Gets the parsed code as JSONObject.
	 * Adding the file content as part of the package being declared at start of file.
	 *
	 * @return     The parsed code.
	 */
    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }
 }