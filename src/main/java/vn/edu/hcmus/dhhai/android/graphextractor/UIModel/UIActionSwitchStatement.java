package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;
/**
 *	Objects of this class define the If statements in a UI invocation path
 */
public class UIActionSwitchStatement extends UIActionBranchingStatement {

	/*
		SwitchStatement: 
			switch(Expression) {
				{SwitchCase | Statement}
			}

		SwitchCase:
			case Expression:
			default:

		A SwitchStatement has as its children an ordered list of statements,
		which include usual statements, switch-case statements, break statements,
		return statements, etc. A switch-statement therefore is a branching point.

		In order to know which branch a statement belongs to, we must consider 
		the placement of the statement in the child list of the switch-statement.

		Each branch is defined as the range starting at the first SwitchCase to 
		the first BreakStatement, ReturnStatement, or unusually a ContinueStatement.

		Two statements are in the same branch if they are in the same range.
		The branch itself can be identified as the first switch-case statement.

		The calculation should be cached.

		Eclipse JDT usage:
		SwitchCase#getExpression: Expression (null if isDefault())
		SwitchCase#isDefault: boolean
		
		SwitchStatement#getExpression
		SwitchStatement#statements: ordered list of {SwitchCase | Statement}
	*/

	public UIActionSwitchStatement(SwitchStatement sStatement, ASTNode expression) {
		pointStatement = sStatement;

		List<Statement> statements = sStatement.statements();

		if (statements.size() > 0) {
			
			Set<ASTNode> parentNodes = new HashSet<ASTNode>();

			ASTNode current = expression;
			while (current != null && current != sStatement) {
				parentNodes.add(current);
				current = current.getParent();
			}

			Statement start, end;
			start = end = statements.get(0);

			for (Statement s : statements) {
				// check which s is the parent of expression,
				// while maintaining the range

				if (end != start && s instanceof SwitchCase)
					start = end = s;

				if (parentNodes.contains(s)) {
					this.branchStatement = start;
					return;
				} else if (s instanceof BreakStatement
						|| s instanceof ReturnStatement 
						|| s instanceof ContinueStatement) {
						end = s;
				} 
			}
		}	
	}

	@Override
	public String toString() {
		return ((SwitchStatement)pointStatement).getExpression().toString()
				+ "/"
				+ ((SwitchCase)branchStatement).getExpression().toString();

	}
}


