package astparser.visitor;

import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class VariableReferenceVisitor extends ASTVisitor {
	private HashMap<String, ReferenceInfo> references;
	
	public VariableReferenceVisitor(String[] keys) {			

		references = new HashMap<String, ReferenceInfo>(keys.length);

		for (String key : keys) {
			references.put(key, new ReferenceInfo(key));
		}
	}

	@Override 
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		String key;
		if (binding != null){
			key = binding.getKey();
			if  (references.containsKey(key)) {
				ASTNode parent = node.getParent();
				if (parent != null && !(parent instanceof VariableDeclaration))
				references.get(key).add(node.getParent());
			}
				
		} 
		return false;
	}

	public HashMap<String, ReferenceInfo> getReferencesMap () {
		return references;
	}

	public static class ReferenceInfo {
		private List<ASTNode> nodes;
		private List<ASTNode> lhsAssignments;
		private List<ASTNode> rhsAssignments;
		private List<ASTNode> methodInvocations;
		private List<ASTNode> methodParams;
		private List<ASTNode> fieldAccess;
		private List<ASTNode> accessAsField;
		// cast expression
		// class instance creation
		// return statement

		private String varKey;

		public ReferenceInfo(String key) {
			varKey = key;
			nodes = new ArrayList<ASTNode>();
			lhsAssignments = new ArrayList<ASTNode>();
			rhsAssignments = new ArrayList<ASTNode>();
			methodInvocations = new ArrayList<ASTNode>();
			methodParams = new ArrayList<ASTNode>();
		}

		public String getKey() {
			return varKey;
		}

		public void add(ASTNode node) {
			nodes.add(node);
		}

		public List<ASTNode> getNodes() {
			return nodes;
		}

		public List<ASTNode> getAll() {
			List<ASTNode> allNodes = new ArrayList<ASTNode>(nodes);
			allNodes.addAll(lhsAssignments);
			allNodes.addAll(rhsAssignments);
			allNodes.addAll(methodInvocations);
			allNodes.addAll(methodParams);
			allNodes.addAll(lhsAssignments);
			return allNodes;
		}

		public boolean isEmpty() {
			return nodes.isEmpty();
		}
	}
}