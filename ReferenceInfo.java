package astparser.visitor;

import org.eclipse.jdt.core.dom.ASTNode;
import java.util.List;
import java.util.ArrayList;

public class ReferenceInfo {
	private List<ASTNode> nodes;

	private String key;

	public ReferenceInfo(String k) {
		key = k;
		nodes = new ArrayList<ASTNode>();
	}

	public String getKey() {
		return key;
	}

	public void add(ASTNode node) {
		nodes.add(node);
	}

	public List<ASTNode> getNodes() {
		return nodes;
	}

	public boolean isEmpty() {
		return nodes.isEmpty();
	}
}