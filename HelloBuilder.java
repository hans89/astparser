package astparser;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;


public class HelloBuilder {
	public static String build() {
		AST ast = AST.newAST(AST.JLS4);
		
		// a new java file
		CompilationUnit unit = ast.newCompilationUnit();
		
		// package statement
		// package
		PackageDeclaration pkgdecl = ast.newPackageDeclaration();
		
		// package com.example;
		pkgdecl.setName(ast.newName("com.example"));
		unit.setPackage(pkgdecl);
		
		// import statement
		// import
		ImportDeclaration impDecl = ast.newImportDeclaration();
		// import java.util;
		impDecl.setName(ast.newQualifiedName(ast.newName("java"),
				ast.newSimpleName("util")));
		// import java.util.*;
		impDecl.setOnDemand(true);

		unit.imports().add(impDecl);
		
		// class declaration
		TypeDeclaration type = ast.newTypeDeclaration();
		// class
		type.setInterface(false);
		// public class
		type.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		// public class HelloWorld
		type.setName(ast.newSimpleName("HelloWorld"));
		
		
		// method declaration
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		List modifiers = methodDeclaration.modifiers();
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName("main"));
		// public static Main
		
		// public static void main
		methodDeclaration.setReturnType2(ast
				.newPrimitiveType(PrimitiveType.VOID));
		
		SingleVariableDeclaration variableDeclaration = ast
				.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast
				.newSimpleName("String"))));
		// String[]
		variableDeclaration.setName(ast.newSimpleName("args"));
		// String[] args
		// public static void main(String[] args)
		methodDeclaration.parameters().add(variableDeclaration);
		
		// body block
		org.eclipse.jdt.core.dom.Block block = ast.newBlock();
		
		// System.out.println
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		QualifiedName name = ast.newQualifiedName(ast.newSimpleName("System"),
				ast.newSimpleName("out"));
		methodInvocation.setExpression(name);
		methodInvocation.setName(ast.newSimpleName("println"));
		
		// "Hello" + " world"
		InfixExpression infixExpression = ast.newInfixExpression();
		infixExpression.setOperator(InfixExpression.Operator.PLUS);
		StringLiteral literal = ast.newStringLiteral();
		literal.setLiteralValue("Hello");
		infixExpression.setLeftOperand(literal);
		literal = ast.newStringLiteral();
		literal.setLiteralValue(" world");
		infixExpression.setRightOperand(literal);
		
		// System.out.println("Hello" + " world")
		methodInvocation.arguments().add(infixExpression);
		
		// System.out.println("Hello" + " world");
		ExpressionStatement expressionStatement = ast
				.newExpressionStatement(methodInvocation);
		
		// { System.out.println("Hello" + " world"); }
		block.statements().add(expressionStatement);
		
		// add body to method "main"
		methodDeclaration.setBody(block);
		
		// add method "main" to "HelloWorld" class
		type.bodyDeclarations().add(methodDeclaration);
		
		
		// add HelloWorld class to com.example
		unit.types().add(type);
		
		return unit.toString();
	}
}
