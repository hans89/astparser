package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;

/**
 *	Objects of this class define the actual invocation statements of the
 * 	methods defined by UIAction, in a UI invocation path
 */
public class UIActionInvocation extends UIActionStatement {
	/**
	 *	The key of the method being invoked
	 */
	public IMethodBinding invokedMethod;

	/**
	 *	The statement as in source code
	 */
	public MethodInvocation astSourceNode;

	/**
	 *	The key of the method that contains the statement
	 */
	public IMethodBinding invokingMethod;

	@Override 
	public String toString() {
		return String.valueOf(astSourceNode);
	}

}
