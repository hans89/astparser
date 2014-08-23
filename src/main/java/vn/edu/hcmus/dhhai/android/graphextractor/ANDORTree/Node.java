package vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree;

import java.util.*;

public abstract class Node<T> {
	protected T internalData;

	protected Node<T> optimalSolutionSubgraph;

	public Node(T data) {
		internalData = data;
	}		

	@Override
	public String toString() {
		return internalData.toString();
	}

	public Node<T> buildOptimalSolution() {
		// caching
		if (optimalSolutionSubgraph == null)	
			optimalSolutionSubgraph = buildOptiomalSolutionSubgraph();

		return optimalSolutionSubgraph;
	}

	// delegate to subclass
	protected abstract Node<T> buildOptiomalSolutionSubgraph();

	private List<SwapOption<T>> optimalSolutionSwapList;

	public List<SwapOption<T>> getOptimalSolutionSwapList() {

		// base case: swap list for optimal solution
		// caching
		if (optimalSolutionSwapList == null) {
			optimalSolutionSwapList = new ArrayList<SwapOption<T>>();
			// take the next possible swap option
			// for every OR node in the optimal solution
			
			// we make a BFS over the optimal solution
			Queue<Node<T>> queue = new ArrayDeque<Node<T>>();

			queue.offer(this);

			Node<T> currentNode;

			while ((currentNode = queue.poll()) != null) {
				// if this is an OR node
				if (currentNode instanceof ORNode<?>) {
					ORNode<T> parentNode = (ORNode<T>) currentNode;

					// add the first swap option to swap list of the optimal solution
					SwapOption<T> firstSwapOption = parentNode.getFirstChildSwapOption();
					if (firstSwapOption != null)
						optimalSolutionSwapList.add(firstSwapOption);

					// put the first child in queue for further exploration
					Node<T> firstChild = parentNode.getChildAt(0);
					if (firstChild != null) 
						queue.offer(firstChild);

				} else if (currentNode instanceof ANDNode<?>) {
					ANDNode<T> andNode = (ANDNode<T>)currentNode;

					// put all children in queue for further exploration
					queue.addAll(andNode.getChildren());
				}
			}
		}

		return optimalSolutionSwapList;
	}


	private Set<SwapOption<T>> allSubSwapOptions;
	public Set<SwapOption<T>> getAllSubSwapOptions() {

		// base case: swap list for optimal solution
		// caching
		if (allSubSwapOptions == null) {
			allSubSwapOptions = new HashSet<SwapOption<T>>();
			// take the next possible swap option
			// for every OR node in the optimal solution
			
			// we make a BFS over the optimal solution
			Queue<Node<T>> queue = new ArrayDeque<Node<T>>();

			queue.offer(this);

			Node<T> currentNode;

			while ((currentNode = queue.poll()) != null) {
				// if this is an OR node
				if (currentNode instanceof ORNode<?>) {
					ORNode<T> parentNode = (ORNode<T>) currentNode;

					// add the first swap option to swap list of the optimal solution
					SwapOption<T> firstSwapOption = parentNode.getFirstChildSwapOption();
					SwapOption<T> swapOpt = firstSwapOption;
					while (swapOpt != null) {
						allSubSwapOptions.add(swapOpt);
						swapOpt = swapOpt.getNextSwapOption();
					}
				}

				if (currentNode instanceof ImmediateNode<?>) {
					ImmediateNode<T> node = (ImmediateNode<T>)currentNode;

					// put all children in queue for further exploration
					queue.addAll(node.getChildren());
				}
			}
		}

		return allSubSwapOptions;
	}

	public List<Node<T>> computeSolutionTerminalNodes(Set<SwapOption<T>> 
														curSolutionSignature) {
		Set<SwapOption<T>> remainingSwapOptions
					= new HashSet<SwapOption<T>>(curSolutionSignature);

		// as this is a tree, where one node can only visited once
		// we don't need to track the visited nodes

		Queue<Node<T>> queue = new ArrayDeque<Node<T>>();
		List<Node<T>> terminalNodes = new ArrayList<Node<T>>();

		queue.offer(this);
		Node<T> currentNode;

		while ((currentNode = queue.poll()) != null) {
			if (currentNode instanceof TerminalNode<?>) {
				terminalNodes.add(currentNode);
			}
			else if (currentNode instanceof ANDNode<?>) {
				ANDNode<T> andNode = (ANDNode<T>)currentNode;

				// put all children in queue for further exploration
				queue.addAll(andNode.getChildren());
			}
			else if (currentNode instanceof ORNode<?>) {
				ORNode<T> orNode = (ORNode<T>)currentNode;

				SwapOption<T> firstSwapOption = orNode.getFirstChildSwapOption();

				// check if the current swap option is applied
				// if so, remove it from the applied set, and get the next
				SwapOption<T> swapOpt = firstSwapOption;
				SwapOption<T> lastSwapOpt = null;

				while (swapOpt != null && remainingSwapOptions.contains(swapOpt)) {
					remainingSwapOptions.remove(swapOpt);
					lastSwapOpt = swapOpt;
					swapOpt = swapOpt.getNextSwapOption();
				}

				Node<T> nextNode;

				if (lastSwapOpt != null) {
					// the final swap of this ORNode, 
					// take the path of the dest node
					nextNode = lastSwapOpt.getDestinationNode();
				} else {
					// there is no swap at all
					// take the optimal path
					nextNode = orNode.getChildAt(0);
				}

				if (nextNode != null) 
					queue.offer(nextNode);
			}
		}

		return terminalNodes;
	}
}