package astparser.ANDORTree;

import java.util.*;

public class ORNode<T> extends ImmediateNode<T> {

	public ORNode(T data, List<Node<T>> children) {
		super(data, children);
	}

	protected Node<T> buildOptiomalSolutionSubgraph() {
		List<Node<T>> optimalNodeChild;
		
		if (this.children.size() > 0) {
			Node<T> firstChild = this.children.get(0);
			optimalNodeChild = new ArrayList<Node<T>>(1);

			// recursive call to first child optimal solution
			optimalNodeChild.add(firstChild.buildOptimalSolution());
		} else {
			optimalNodeChild = new ArrayList<Node<T>>(0); 
		}

		return new ORNode(this.internalData, optimalNodeChild);
	}

	private SwapOption<T> firstChildSwapOption;
	private boolean firstChildSwapOptionCached;

	public SwapOption<T> getFirstChildSwapOption() {
		// caching
		if (firstChildSwapOptionCached == false) {
			if (this.children.size() >= 2) {
				firstChildSwapOption = new SwapOption(this, 0);
			}
				
			firstChildSwapOptionCached = true;
		}

		return firstChildSwapOption;
	}
}