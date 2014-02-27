package astparser.visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class RemoverVisitor extends ASTVisitor {
	private Set<ASTNode> markedNodes;
	public RemoverVisitor(Set<ASTNode> nodes) {
		markedNodes = nodes;
	}

	@Override
	public void postVisit(ASTNode node) {
		if (!markedNodes.contains(node) && 
			(node instanceof Statement 
				|| node instanceof MethodDeclaration
				|| node instanceof FieldDeclaration
			)) {
			try {
				node.delete();
			} catch (Exception ex) {
				// cannot be deleted
				//　System.out.println(node);
				// just stay silent
			}
		}
			
	}
}