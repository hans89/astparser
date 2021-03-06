package vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree;

public class TerminalNode<T> extends Node<T> {
	public TerminalNode(T data) {
		super(data);
	}

	protected Node<T> buildOptiomalSolutionSubgraph() {
		return new TerminalNode<T>(this.internalData);
	}
}