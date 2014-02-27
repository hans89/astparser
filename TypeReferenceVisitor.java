package astparser.visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class TypeReferenceVisitor extends ReferenceVisitor {
	
	public static final String JAVA_ROOTOBJECT_KEY = "Ljava/lang/Object;";
	
	public TypeReferenceVisitor (String[] keys) {
		super(keys);
	}

	@Override 
	public boolean visit(Assignment node) {
		Expression lhs = node.getLeftHandSide();
		Expression rhs = node.getRightHandSide();

		//main interest: lhs, rhs
		String foundKey = null;
		foundKey = matchTypeKey(lhs);
		if (foundKey != null) 
			foundKey = matchTypeKey(rhs);
		if (foundKey != null) {			
			matchedReferences.get(foundKey).add(node);
		}	
		 
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// main interest: calling object, or arguments	
		Expression callExp = node.getExpression();
		String foundKey = null;
		if (callExp != null) {
			foundKey = matchTypeKey(callExp);
			if (foundKey != null) {			
				matchedReferences.get(foundKey).add(node);
			}
		}

		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			ITypeBinding type = binding.getDeclaringClass();
			if (type != null) {
				String typeKey =  type.getKey();
				if (matchedReferences.containsKey(typeKey)) {
					matchedReferences.get(typeKey).add(node);	
				}
			}
		}

		List<Expression> args = node.arguments();
		foundKey = null;
		for (Expression exp : args) {
			foundKey = matchTypeKey(exp);
			if (foundKey != null) {
				matchedReferences.get(foundKey).add(node);
			}
		}
		// found nothing, may go lower
		return true;
	}

	@Override
	// visit fields and local variables decl
	public boolean visit(VariableDeclarationFragment node) {
		IVariableBinding vbind = node.resolveBinding();
		if (vbind != null) {
			ITypeBinding tbind = vbind.getType();
			String foundKey = null;
			if ((foundKey = matchSuperClass(tbind)) != null) {
				if (vbind.isField())
					matchedReferences.get(foundKey).add(node);
				else
					matchedReferences.get(foundKey).add(node);
			}
		}

		return true;
	}

	@Override
	// visit formal arguments (including exception formal args)
	public boolean visit(SingleVariableDeclaration node) {
		IVariableBinding vbind = node.resolveBinding();
		if (vbind != null) {
			ITypeBinding tbind = vbind.getType();
			String foundKey = null;
			if ((foundKey = matchSuperClass(tbind)) != null) {
				matchedReferences.get(foundKey).add(node);
			}
		}
		return true;
	}

	/**
	 * Check if the input expression has an interesting type
	 *
	 * @param ex  the expression to be check
	 * @return    type key of the expression if it has an interesting type,
	 * 				otherwise, null.
	 */

	// private void print(String type, String key, ASTNode node) {
	// 	System.out.println(type + " : " + key + " --- " + node);
	// }
	private String matchTypeKey(Expression exp) {
		ITypeBinding typeBind = exp.resolveTypeBinding();
		
		return matchSuperClass(typeBind);
	}

	private String matchSuperClass(ITypeBinding tbind) {
		while (tbind != null && !(tbind.getKey().equals(JAVA_ROOTOBJECT_KEY))) {
			String key = tbind.getKey();
			if (matchedReferences.containsKey(key))
				return key;
			tbind = tbind.getSuperclass();
		}
		
		return null;
	}
}

// public class TypeReferenceVisitor extends ASTVisitor {
	
// 	public static final String JAVA_ROOTOBJECT_KEY = "Ljava/lang/Object;";

// 	public static final int ASSIGNMENT_LEFT_HANDSIDE = 1;
// 	public static final int ASSIGNMENT_RIGHT_HANDSIDE = 2;
// 	public static final int METHOD_INVOKE_OBJECT = 3;
// 	public static final int METHOD_INVOKE_CLASS = 4;
// 	public static final int METHOD_INVOKE_ARGUMENTS = 5;
// 	public static final int VARIABLE_DECLARATION_FIELD = 6;
// 	public static final int VARIABLE_DECLARATION_LOCAL = 7;
// 	public static final int VARIABLE_DECLARATION_FORMAL = 8;


// 	private Set<String> matchKeys;
// 	private NodeTracker tracker;

// 	public TypeReferenceVisitor (String[] keys, NodeTracker t) {

// 		matchKeys = new HashSet<String>(keys.length);

// 		for (String key : keys) {
// 			matchKeys.add(key);
// 		}

// 		tracker = t;
// 	}

// 	@Override 
// 	public boolean visit(Assignment node) {
// 		Expression lhs = node.getLeftHandSide();
// 		Expression rhs = node.getRightHandSide();

// 		//main interest: lhs, rhs
// 		String foundKey = null;
// 		foundKey = matchTypeKey(lhs);
// 		if (foundKey != null) {
// 			tracker.accept(node, ASSIGNMENT_LEFT_HANDSIDE);
// 		}
// 		else {
// 			foundKey = matchTypeKey(rhs);
// 			if (foundKey != null) {			
// 				tracker.accept(node, ASSIGNMENT_RIGHT_HANDSIDE);
// 			}	
// 		} 
		
// 		return true;
// 	}

// 	@Override
// 	public boolean visit(MethodInvocation node) {
// 		// main interest: calling object, or arguments	
// 		Expression callExp = node.getExpression();
// 		String foundKey = null;
// 		if (callExp != null) {
// 			foundKey = matchTypeKey(callExp);
// 			if (foundKey != null) {			
// 				tracker.accept(node, METHOD_INVOKE_OBJECT);
// 			}
// 		}

// 		IMethodBinding binding = node.resolveMethodBinding();
// 		if (binding != null) {
// 			ITypeBinding type = binding.getDeclaringClass();
// 			if (type != null) {
// 				String typeKey =  type.getKey();
// 				if (matchKeys.contains(typeKey)) {
// 					tracker.accept(node, METHOD_INVOKE_CLASS);					
// 				}
// 			}
// 		}

// 		List<Expression> args = node.arguments();
// 		foundKey = null;
// 		for (Expression exp : args) {
// 			foundKey = matchTypeKey(exp);
// 			if (foundKey != null) {
// 				tracker.accept(node, METHOD_INVOKE_ARGUMENTS);
// 			}
// 		}
// 		// found nothing, may go lower
// 		return true;
// 	}

// 	@Override
// 	// visit fields and local variables decl
// 	public boolean visit(VariableDeclarationFragment node) {
// 		IVariableBinding vbind = node.resolveBinding();
// 		if (vbind != null) {
// 			ITypeBinding tbind = vbind.getType();
// 			if (matchSuperClass(tbind, matchKeys) != null) {
// 				if (vbind.isField())
// 					tracker.accept(node, VARIABLE_DECLARATION_FIELD);
// 				else
// 					tracker.accept(node, VARIABLE_DECLARATION_LOCAL);
// 			}
// 		}

// 		return true;
// 	}

// 	@Override
// 	// visit formal arguments (including exception formal args)
// 	public boolean visit(SingleVariableDeclaration node) {
// 		IVariableBinding vbind = node.resolveBinding();
// 		if (vbind != null) {
// 			ITypeBinding tbind = vbind.getType();
// 			if (matchSuperClass(tbind, matchKeys) != null) {
// 				tracker.accept(node, VARIABLE_DECLARATION_FORMAL);
// 			}
// 		}
// 		return true;
// 	}

// 	/**
// 	 * Check if the input expression has an interesting type
// 	 *
// 	 * @param ex  the expression to be check
// 	 * @return    type key of the expression if it has an interesting type,
// 	 * 				otherwise, null.
// 	 */

// 	// private void print(String type, String key, ASTNode node) {
// 	// 	System.out.println(type + " : " + key + " --- " + node);
// 	// }
// 	private String matchTypeKey(Expression exp) {
// 		ITypeBinding typeBind = exp.resolveTypeBinding();
		
// 		return matchSuperClass(typeBind, matchKeys);
// 	}

// 	private String matchSuperClass(ITypeBinding tbind, Set<String> typeKeys) {
// 		while (tbind != null && !(tbind.getKey().equals(JAVA_ROOTOBJECT_KEY))) {
// 			String key = tbind.getKey();
// 			if (typeKeys.contains(key))
// 				return key;
// 			tbind = tbind.getSuperclass();
// 		}
		
// 		return null;
// 	}
// }