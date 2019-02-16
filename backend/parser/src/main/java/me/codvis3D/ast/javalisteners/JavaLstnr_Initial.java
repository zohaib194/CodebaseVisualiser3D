package me.codvis.ast;

import java.util.List;
import java.util.ArrayList;

import me.codvis.ast.parser.Java9BaseListener;
import me.codvis.ast.parser.Java9Parser;

import org.json.JSONObject;

public class JavaLstnr_Initial extends JavaExtendedListener {
	private FileModel fileModel;

	JavaLstnr_Initial(String filePath) {
		this.fileModel = new FileModel(filePath);
	}

    @Override 
    public void enterMethodDeclarator(Java9Parser.MethodDeclaratorContext ctx) {  //see gramBaseListener for allowed functions
      	
	    fileModel.addFunction(new FunctionModel(ctx.getText()));
    }

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

    public JSONObject getParsedCode() {
		JSONObject parsedCode = new JSONObject();

    	return parsedCode.put("file", this.fileModel.getParsedCode());
    }

 }