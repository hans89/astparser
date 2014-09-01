package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;

/**
 *	Objects of this class define the If statements in a UI invocation path
 */
public class UIActionIfStatement extends UIActionBranchingStatement {

	/*
		IfStatement: if (Expression) Statement [else Statement]

		An IfStatement includes 2 sub statement, a Then-statement and
		an Else-statement. A UIActionIfStatement is either a Then-statement
		or an Else-statement, recorded as @field branchStatement

		UIActionIfStatement represents a "branch" in a conditional, while the
		conditional if (expression) is called a branching "point". The braching
		point is recorded as @field pointStatement

		UIActionIfStatement-s that share a common pointStatement are branches
		of a same conditional.

		Eclipse JDT usage:
		IfStatement#getExpression: Expression
		IfStatement#getThenStatement: Statement
		IfStatement#getElseStatement: Statement (nullable)
	*/

	public UIActionIfStatement(IfStatement p, Statement b) {
		pointStatement = p;
		branchStatement = b;
	}

	@Override 
	public String toString() {
		IfStatement ifSt = (IfStatement)pointStatement;
		String ifExp = ifSt.getExpression().toString();

		if (branchStatement == ifSt.getThenStatement()) {
			return ifExp + "/then";
		} else if (branchStatement == ifSt.getElseStatement()) {
			return ifExp + "/else";
		}
		return "";
	}
}
