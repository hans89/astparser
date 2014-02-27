package astparser.visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class ReferenceVisitor extends ASTVisitor {
	protected HashMap<String, Set<ASTNode>> matchedReferences;

	public ReferenceVisitor (String[] keys) {

		matchedReferences = new HashMap<String, Set<ASTNode>> (keys.length);

		for (String key : keys) {
			matchedReferences.put(key, new HashSet<ASTNode>());
		}

	}

	public Set<ASTNode> getAllReferences() {
		int count = 0;
		for (Set<ASTNode> v : matchedReferences.values()) {
			count += v.size();
		}

		Set<ASTNode> allNodes = new HashSet<ASTNode>(count);

		for (Set<ASTNode> v : matchedReferences.values()) {
			allNodes.addAll(v);
		}	

		return allNodes;
	}

	public Set<ASTNode> getReferencesByKey(String key) {
		return matchedReferences.get(key);
	}
}