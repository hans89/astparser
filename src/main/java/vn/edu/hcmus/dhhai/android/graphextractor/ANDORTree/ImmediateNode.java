package vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree;

import java.util.*;

public abstract class ImmediateNode<T> extends Node<T> {
	protected List<Node<T>> children;

	public ImmediateNode(T data, List<Node<T>> children) {
		super(data);
		this.children = children;
	}

	public Node<T> getChildAt(int index) {
		if (index >= 0 && index < this.children.size()) {
			return this.children.get(index);
		}
		return null;
	}

	public List<Node<T>> getChildren() {
		return this.children;
	}

	public void addChild(Node<T> child) {
		if (this.children == null)
			this.children = new ArrayList<Node<T>>();

		this.children.add(child);
	}

}