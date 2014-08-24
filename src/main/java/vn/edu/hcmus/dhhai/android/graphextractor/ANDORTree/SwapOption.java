package vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree;

public class SwapOption<T> {
	private ORNode<T> parentNode;
	private int sourceNodeId;

	private SwapOption<T> nextSwapOption;
	private boolean nextSwapOptionCached;

	public SwapOption(ORNode<T> parent, int sourceId) {
		parentNode = parent;
		sourceNodeId = sourceId;
	}

	public Node<T> getParentNode() {
		return parentNode;
	}

	public Node<T> getSourceNode() {
		return parentNode.getChildAt(sourceNodeId);
	}

	public Node<T> getDestinationNode() {
		return parentNode.getChildAt(sourceNodeId + 1);
	}

	public SwapOption<T> getNextSwapOption() {
		// caching
		if (nextSwapOptionCached == false) {
			int currentDestId = sourceNodeId + 1;
			Node<T> currentDest = parentNode.getChildAt(currentDestId);
			Node<T> nextDest = parentNode.getChildAt(currentDestId + 1);

			if (currentDest != null && nextDest != null)
				nextSwapOption = new SwapOption<T>(parentNode, currentDestId);
			nextSwapOptionCached = true;
		}

		return nextSwapOption;
	}

	@Override
	public String toString() {
		String outString = "";

		Node<T> node = null;

		if ((node = getSourceNode()) != null)
			outString += node.toString();

		//outString += getParentNode().toString();

		node = null;

		if ((node = getDestinationNode()) != null)
			outString += node.toString();

		return outString;
	}
}