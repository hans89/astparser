package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;

/**
 *	Objects of this class define the branching statements in a UI invocation path
 */
public abstract class UIActionBranchingStatement extends UIActionStatement {

	protected Statement pointStatement;
	protected Statement branchStatement;
	
	public boolean isSameBranchingPoint(UIActionBranchingStatement other) {
		return (this.pointStatement == other.pointStatement);
	}

	public boolean isSameBranch(UIActionBranchingStatement other) {
		return (this.branchStatement == other.branchStatement);
	}

	public Statement getBranchingPoint() {
		return pointStatement;
	}

	public Statement getBranch() {
		return branchStatement;
	}
}
