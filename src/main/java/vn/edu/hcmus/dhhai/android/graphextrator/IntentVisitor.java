package vn.edu.hcmus.dhhai.android.graphextrator;

import vn.edu.hcmus.dhhai.android.graphextrator.UIModel.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

class IntentVisitor extends ASTVisitor {
	public static final String IntentClassname = "android.content.Intent";
	public static final String ClassTypename = "java.lang.Class";
	public static class IntentInfo {
		// if the intent is made a variable
		public IVariableBinding varBinding;

		// if the intent is an explicit intent, then we can have a targetted
		// activity class
		public VariableDeclarationFragment declarationNode;

		public List<Expression> references;

		private ITypeBinding targetActivity;

		public ITypeBinding extractActivity() {
			if (declarationNode != null && targetActivity == null) {
				Expression classInitNode = declarationNode.getInitializer();
				if (classInitNode != null &&
					 classInitNode instanceof ClassInstanceCreation)
				targetActivity = IntentInfo.extract((ClassInstanceCreation)classInitNode);
			}

			return targetActivity;
		}

		public static ITypeBinding extract(ClassInstanceCreation node) {
			ITypeBinding nodeTypeBinding = node.resolveTypeBinding();
			if (nodeTypeBinding != null &&
				ASTNodeUtils.subClassOf(nodeTypeBinding, IntentVisitor.IntentClassname)
					!= null) {

				List<Expression> exps = node.arguments();
				for (Expression exp : exps) {
					ITypeBinding typeBinding = exp.resolveTypeBinding();

					if (typeBinding != null &&
						typeBinding.isParameterizedType() && 
						typeBinding.getErasure().getQualifiedName()
								.toString().equals(IntentVisitor.ClassTypename)) {

						//System.out.println(exp + " " + typeBinding.getErasure().getQualifiedName());		

						for (ITypeBinding typeArg : typeBinding.getTypeArguments()){
							return typeArg;
						}
					}
				}
			}
			return null;
		}
	}

	private HashMap<IVariableBinding, IntentInfo> intents;

	public IntentVisitor() {
		intents = new HashMap<IVariableBinding, IntentInfo>();
	}

	public HashMap<IVariableBinding, IntentInfo> getAllIntents() {
		return intents;
	}

	public void addReference(IVariableBinding binding, Expression refNode) {
		ITypeBinding nodeTypeBinding = refNode.resolveTypeBinding();
		if (binding != null && nodeTypeBinding != null &&
			ASTNodeUtils.subClassOf(nodeTypeBinding, IntentVisitor.IntentClassname)
						!= null) {
			IntentInfo info;

			if (!intents.containsKey(binding)) {
				info = new IntentInfo();
				info.varBinding = binding;
				intents.put(binding, info);
			} else {
				info = intents.get(binding);
			}

			 if (info.references == null)
			 	info.references = new LinkedList<Expression>();

			 info.references.add(refNode);
		}
	}

	public void addDeclaration(IVariableBinding varBinding, VariableDeclarationFragment node) {
		ITypeBinding typeBinding = varBinding.getType();
		if (varBinding != null && typeBinding != null &&
			ASTNodeUtils.subClassOf(typeBinding, IntentVisitor.IntentClassname)
				!= null){
			IntentInfo info;

			if (!intents.containsKey(varBinding)) {
				info = new IntentInfo();
				info.varBinding = varBinding;
				intents.put(varBinding, info);
			} else {
				info = intents.get(varBinding);
			}

			info.declarationNode = node;
			info.extractActivity();
		}
	}

	// regular variables, fields 
	@Override
	public boolean visit(VariableDeclarationFragment node) {

		this.addDeclaration(node.resolveBinding(), node);
		
		return true;
	}


	// references
	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding varBinding = node.resolveFieldBinding();
		if (varBinding != null) {
			this.addReference(varBinding, node);
		}

		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		IVariableBinding varBinding = node.resolveFieldBinding();
		
		if (varBinding != null) {
			this.addReference(varBinding, node);
		}
			
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding varBinding = node.resolveBinding();
		if (varBinding != null && varBinding instanceof IVariableBinding){
			this.addReference((IVariableBinding)varBinding, node);
		}
			
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding varBinding = node.resolveBinding();
		
		if (varBinding != null && varBinding instanceof IVariableBinding) {
			this.addReference((IVariableBinding)varBinding, node);
		}
			
		return false;
	}
}