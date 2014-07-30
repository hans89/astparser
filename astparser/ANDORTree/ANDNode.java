package astparser.ANDORTree;

import java.util.*;

public class ANDNode<T> extends ImmediateNode<T> {

	public ANDNode(T data, List<Node<T>> children) {
		super(data, children);
	}

	protected Node<T> buildOptiomalSolutionSubgraph() {
		List<Node<T>> optimalNodeChild;
		
		if (this.children.size() > 0) {
			optimalNodeChild = new ArrayList<Node<T>>(this.children.size());

			for (Node<T> child : this.children) {
				// recursive call to child optimal solution
				optimalNodeChild.add(child.buildOptimalSolution());	
			}
		} else {
			optimalNodeChild = new ArrayList<Node<T>>(0); 
		}

		return new ANDNode(this.internalData, optimalNodeChild);
	}
}