package vn.edu.hcmus.dhhai.android.graphextractor;

import java.util.*;
import org.eclipse.jdt.core.dom.*;
import vn.edu.hcmus.dhhai.android.graphextractor.UIModel.*;

/**
 *	This class aggregates a group of extractor in the fashion of 
 *	the Chain of Responsibility patterns. The chaining, however,
 *	is managed by this class, thus the visiting order of the 
 *	processors is decided by this class.
 *	TODO: handle arguments binding
 */

public class MethodExtractorGroup {

	public interface Extractor {

		/**
		 * Extract from an Expression the class (type binding) that implements or
		 *	is a subclass of the input interface/class
		 *
		 * 	@param input	the expression to be extract
		 *	@param superType	the superclass/interface qualified name
		 *	@return		the class type binding, null if not found
		 */
		ITypeBinding extract(Expression input, String superType);
		boolean canHandle(Expression input);
	}

	private List<Extractor> extractors;
	private HashMap<IMethodBinding, UIAction> allActions;
	private HashMap<IVariableBinding, UIEventObject> allEventVariables;

	public MethodExtractorGroup(HashMap<IMethodBinding, UIAction> allActs,
					HashMap<IVariableBinding, UIEventObject> allEventVars) {

		allActions = allActs;
		allEventVariables = allEventVars;

		extractors = new ArrayList<Extractor>();

		extractors.add(new SingleExtractor());
		extractors.add(new ComplexExtractor(this));
		
	}

	public UIAction findUIAction(IMethodBinding method) {
		return allActions.get(method);
	}

	public UIEventObject findUIEventObject(IVariableBinding var) {
		return allEventVariables.get(var);
	} 
	
	public ITypeBinding handle(Expression e, String superClass) {
		for (Extractor extractor : extractors) {
			if (extractor.canHandle(e)) {
				return extractor.extract(e, superClass);
			}
				
		}
		return null;
	}

	public static class SingleExtractor implements Extractor {

		public ITypeBinding extract(Expression e, String superClass) {
			
			ITypeBinding typeBinding = e.resolveTypeBinding();

			//System.out.println("SingleExtract: " + e);

			if (e instanceof NullLiteral)
				return typeBinding;

			if (ASTNodeUtils.getSuperTypeQualifiedNames(typeBinding)
							.contains(superClass))
				return typeBinding;
			
			return null;
		}

		public boolean canHandle(Expression e) {
			return e != null && e.resolveTypeBinding() != null &&
					(e instanceof ClassInstanceCreation ||
					e instanceof ThisExpression ||
					e instanceof NullLiteral ||
					e.resolveTypeBinding().isClass())
					;
		}
	}

	public static class ReturnStatementASTVisitor extends ASTVisitor {
		private Expression returnExpression;

		@Override
		public boolean visit(ReturnStatement node) {
			returnExpression = node.getExpression();
			return false;
		}

		public Expression getExpression() {
			return returnExpression;
		}
	}

	public static class ComplexExtractor implements Extractor {
		private MethodExtractorGroup group;

		public ComplexExtractor(MethodExtractorGroup g) {
			group = g;
			
		}

		public ITypeBinding extract(Expression e, String superClass) {
			ITypeBinding typeBinding = e.resolveTypeBinding();

			if (!ASTNodeUtils.getSuperTypeQualifiedNames(typeBinding)
								.contains(superClass))
				return null;

			if (e instanceof CastExpression) {
				CastExpression castExp = (CastExpression)e;

				ITypeBinding castType = typeBinding;
				ITypeBinding subExpType = group.handle(castExp.getExpression(), superClass);

				if (castType.isInterface()) {
					return subExpType;
				}

				// TODO
				// the case in which both castType and subExpType are classes
				// if the matching method is implemented in subExpType -> subExpType
				//	else -> castType
				// We need the method information
				// else if (castType.isClass() && castType.isFromSource()) {

				// } 
				// else
					return null;
			}

			if (e instanceof MethodInvocation) {
				// must find method declaration
				UIAction action = group.findUIAction(
								((MethodInvocation)e).resolveMethodBinding());

				if (action != null && action instanceof UIActionInternal) {
					UIActionInternal internalAct = (UIActionInternal) action;

					ReturnStatementASTVisitor returnVisitor = new ReturnStatementASTVisitor();

					if (internalAct.declaration == null) {
						// System.out.println("NULL DECLARATION");
						// System.out.println(internalAct.methodBinding.getKey());
						
						// if (internalAct.invokedList != null)
						// 	for (UIActionInvocation actInv : internalAct.invokedList) {
						// 		System.out.println(actInv.astSourceNode);
						// 	}
						return null;
					}
					else
						internalAct.declaration.accept(returnVisitor);

					return group.handle(returnVisitor.getExpression(), superClass);
				}
			}

			if (e instanceof Name || e instanceof FieldAccess 
				|| e instanceof SuperFieldAccess) {

				IVariableBinding variableBinding = null;

				if (e instanceof Name) {
					IBinding varBinding = ((Name)e).resolveBinding();
					if (!(varBinding instanceof IVariableBinding)) 
						return null;

					variableBinding = (IVariableBinding) varBinding;
				}

				if (e instanceof FieldAccess) {
					variableBinding = ((FieldAccess)e).resolveFieldBinding();
				}

				if (e instanceof SuperFieldAccess) {
					variableBinding = ((SuperFieldAccess)e).resolveFieldBinding();
				}

				if (variableBinding == null)
					return null;

				UIEventObject uiEO = group.findUIEventObject(variableBinding);

				if (uiEO == null)
					return null;

				Set<ITypeBinding> foundTypes = new HashSet<ITypeBinding>();
				// handle declaration
				if (uiEO.declaration != null) {
					Expression initializer = null;

					if (uiEO.declaration instanceof VariableDeclarationFragment) {
						initializer = 
						((VariableDeclarationFragment)uiEO.declaration).getInitializer();
					}
					else if (uiEO.declaration instanceof SingleVariableDeclaration){
						initializer = 
						((SingleVariableDeclaration)uiEO.declaration).getInitializer();
					}

					if (initializer != null) {
						ITypeBinding initType = group.handle(initializer, superClass);
					
						if (initType != null)
							foundTypes.add(initType);	
					}
				}

				// handle references
				if (uiEO.references != null) { 
					for (Expression ex : uiEO.references) {
						ASTNode parent = ex.getParent();
						if (parent != null && parent instanceof Assignment &&
								((Assignment)parent).getLeftHandSide() == ex) {

							ITypeBinding rhsType 
								= group.handle(((Assignment)parent).getRightHandSide(), superClass);

							if (rhsType != null)
								foundTypes.add(rhsType);
						}
					}
				}
				// Name.resolveBinding() 
				// -> VariableDeclaration, Name + Assignment/ReturnStatement
				// FieldAccess.resolveFieldBinding() 
				// -> FieldAccess, FieldDeclaration + Assignment
				// SuperFieldAccess.resolveFieldBinding() 
				// -> SuperFieldAccess, FieldDeclaration + Assignment/ReturnStatement

				// must check if: field, or variable

				if (foundTypes.isEmpty())
					return null;

				if (foundTypes.size() == 1)
					return foundTypes.iterator().next();

				if (foundTypes.size() > 1) {
					for (ITypeBinding foundType : foundTypes) {
						if (!foundType.isNullType())
							return foundType;
					}
					return foundTypes.iterator().next();
				}
			}
			
			return null;
		}

		public boolean canHandle(Expression e) {
			if (e == null)
				return false;

			ITypeBinding type = e.resolveTypeBinding();
			return (type != null && type.isInterface()) ||
					e instanceof CastExpression;
		}
	}
}