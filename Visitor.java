package astparser.visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;
import java.util.Map.Entry;

public class Visitor extends ASTVisitor {

	@Override
	public boolean visit(MethodInvocation node) {
		System.out.print(node.getExpression());
		System.out.println("." + node.getName());
		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			ITypeBinding type = binding.getDeclaringClass();
			if (type != null) {
				System.out.println("-- from class " + type.getQualifiedName());
			}
			
		} else {
			System.out.println("-- null");
		}
		return true;
	}


	public static class MethodClassVisitor extends ASTVisitor {
		private String key;
		public MethodClassVisitor(String classKey) {
			key = classKey;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			IMethodBinding binding = node.resolveMethodBinding();
			if (binding != null) {
				ITypeBinding type = binding.getDeclaringClass();
				//System.out.println(type.getKey() + " " + key);
				if (type != null && type.getKey().equals(key)) {
					System.out.println("invoke method " + node.getName() 
						+ " of class " + type.getQualifiedName());
				}
			}
			return true;
		}
	}

	// particular care about a Type and its:
	// assignment
	// constructor invoke
	// method invoke 

	// may consider its
	// field access
	// casting
	// class instance creation
	public static class ActionVisitor extends ASTVisitor {
		private String key;
		public ActionVisitor(String classKey) {
			key = classKey;
		}
		
		@Override 
		public boolean visit (Assignment node) {

			Expression lhs = node.getLeftHandSide();
			Expression rhs = node.getRightHandSide();

			//main interest: lhs, rhs
			if (isExpressionTypeMatch(lhs)) {
				System.out.println("Matching type in LHS assignment - " +
					lhs.toString() + " : " + node.toString());	
			}

			if (isExpressionTypeMatch(rhs)) {
				System.out.println("Matching type in RHS assignment - " +
					rhs.toString() + " : " + node.toString());	
			}


			return true;
		}

		@Override 
		public boolean visit(MethodInvocation node) {

			IMethodBinding binding = node.resolveMethodBinding();
			if (binding != null) {
				ITypeBinding type = binding.getDeclaringClass();
				//System.out.println(type.getKey() + " " + key);
				if (type != null && type.getKey().equals(key)) {
					System.out.println("Matching type that has method invoked " + node.toString());
				}
			}

			return true;
		}

		// @Override
		// public boolean visit (ClassInstanceCreation node) {
		// 	if (node.getType().resolveBinding().getKey().equals(key)){
		// 		System.out.println("Matching type that created by " + node.toString());
		// 	}
				
		// 	return true;
		// }

		// @Override 
		// public boolean visit (ArrayCreation node) {
		// 	if (node.getType().getElementType().resolveBinding().getKey().equals(key)) {
		// 		System.out.println("Matching type that array-created by " + node.toString());
		// 	}
		// 	return true;
		// }
		// @Override
		// public boolean visit (CastExpression node) {
		// 	Expression ex = node.getExpression();
		// 	Type casttype = node.getType();

		// 	if (ex.resolveTypeBinding().getKey().equals(key)) {

		// 	}

		// 	if (casttype.resolveBinding().getKey().equals(key)) {
		// 	}


		// 	return true;
		// }

		private boolean isExpressionTypeMatch(Expression ex) {
			return ex.resolveTypeBinding().getKey().equals(key);
		}
	}


	public static class AllExpressionVisitor extends ASTVisitor {
		private String key;
		public AllExpressionVisitor(String classKey) {
			key = classKey;
		}
		@Override
		public void preVisit (ASTNode node){
			if (node instanceof Expression) {
				Expression ex = (Expression)node;
				ITypeBinding binding = ex.resolveTypeBinding();
				if (binding != null && binding.getKey().equals(key)) {
					System.out.println(ex.toString() + " : " + binding.getKey());
				}
			}
		}
	}
}