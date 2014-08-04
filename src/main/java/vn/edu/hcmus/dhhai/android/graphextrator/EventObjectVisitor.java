package vn.edu.hcmus.dhhai.android.graphextrator;

import vn.edu.hcmus.dhhai.android.graphextrator.UIModel.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

/**
 *	This visitor will: identify all event objects as defined by the 
 * 	UIActionLinkedEventClass
 */
public class EventObjectVisitor extends ASTVisitor {
	
	private HashMap<IVariableBinding, UIEventObject> eventObjects;
	private Set<String> eventClassKeys;

	public EventObjectVisitor(Set<String> eClassKeys) {
		eventClassKeys = eClassKeys;
		eventObjects = new HashMap<IVariableBinding, UIEventObject>();
		
	}

	public HashMap<IVariableBinding, UIEventObject> getAllEventObjects() {
		return eventObjects;
	}

	private Set<String> findSuperTypes(ITypeBinding typeBinding) {
		if (typeBinding == null)
			return null;
		
		return ASTNodeUtils.matchSuperType(typeBinding, eventClassKeys);
	}

	private void visitVarDecl(VariableDeclaration node) {
		IVariableBinding varBinding = node.resolveBinding();
		if (varBinding == null)
			return;

		ITypeBinding typeBinding = varBinding.getType();

		Set<String> superTypeKeys = findSuperTypes(typeBinding);

		if (superTypeKeys == null || superTypeKeys.isEmpty())
			return;

		UIEventObject eventObj;

		if (!eventObjects.containsKey(varBinding)) {
			eventObj = new UIEventObject();
			eventObj.typeBinding = typeBinding;
			eventObj.variableBinding = varBinding;
			eventObj.superTypeKeys = superTypeKeys;
			
			eventObjects.put(varBinding, eventObj);
		} else {
			eventObj = eventObjects.get(varBinding);	
		}	

		eventObj.declaration = node;
	}

	private void visitVarRef(Expression node, IVariableBinding varBinding) {

		// we should filter references as left-hand sides in an assignment only 
		// (return will be handled by ReturnStatementVisitor)
		ASTNode parent = node.getParent();
		if (parent == null || !(parent instanceof Assignment)
			 || ((Assignment)parent).getLeftHandSide() != node)
			return;

		ITypeBinding typeBinding = varBinding.getType();

		Set<String> superTypeKeys = findSuperTypes(typeBinding);

		if (superTypeKeys == null || superTypeKeys.isEmpty())
			return;

		UIEventObject eventObj;

		if (!eventObjects.containsKey(varBinding)) {
			eventObj = new UIEventObject();
			eventObj.typeBinding = typeBinding;
			eventObj.variableBinding = varBinding;
			eventObj.superTypeKeys = superTypeKeys;

			eventObjects.put(varBinding, eventObj);
		} else {
			eventObj = eventObjects.get(varBinding);	
		}				

		if (eventObj.references == null)
			eventObj.references = new LinkedList<Expression>();

		eventObj.references.add(node);
	}

	// regular variables, fields 
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		visitVarDecl(node);
		return false;
	}

	// formal parameter lists and catch clauses
	@Override
	public boolean visit(SingleVariableDeclaration node) {
		visitVarDecl(node);
		return false;
	}

	// references
	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding varBinding = node.resolveFieldBinding();
		if (varBinding != null) {
			visitVarRef(node, varBinding);
		}

		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		IVariableBinding varBinding = node.resolveFieldBinding();
		
		if (varBinding != null) {
			visitVarRef(node, varBinding);
		}
			
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding varBinding = node.resolveBinding();
		if (varBinding != null && varBinding instanceof IVariableBinding){
			visitVarRef(node, (IVariableBinding)varBinding);
		}
			
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding varBinding = node.resolveBinding();
		
		if (varBinding != null && varBinding instanceof IVariableBinding) {
			visitVarRef(node, (IVariableBinding)varBinding);
		}
			
		return false;
	}

	// @Override 
	// public void preVisit(ASTNode node) {
	// 	if (node.toString().equals("myListener4"))
	// 		System.out.println("Previsit " + node + " in " + node.getParent());
	// }
}