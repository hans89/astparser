package astparser;

import astparser.UIModel.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

/**
 *	This visitor will: identify all methods, along with their definitions and
 *		invocations. Found definitions and invocations are stored in UIAction
 *		map, with keys as the method bindings.
 *	The visitor also identify types of actions, using the corresponding meta
 *		UIActionClass, through the UIActionBuilder.
 *
 *	The visitor, with the help of the UIActionBuilder, set up all required
 *	information of the UIAction and the method declaration of UIActionInternal.
 *	It will not set up the information of UIActionInternal#executingPaths and
 *	the UIActionLinkedEvent#setters.
 *
 *	This class is Eclipse JDT and event-driven OO programming paradigm dependent.
 */
public class MethodVisitor extends ASTVisitor {
	private HashMap<IMethodBinding, UIAction> actions;
	private UIActionBuilder builder;

	public MethodVisitor(UIActionBuilder b) {
		actions = new HashMap<IMethodBinding, UIAction>();
		builder = b;
	}

	public HashMap<IMethodBinding, UIAction> getAllActions() {
		return actions;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding mBinding;
		if ((mBinding = node.resolveBinding()) != null) {
			UIAction act;
			if (!actions.containsKey(mBinding)) {
				// if never seen this before, build one
				act = builder.buildAction(mBinding, node);

				actions.put(mBinding, act);
			} else {
				// if seen, then set up the method declaration
				act = actions.get(mBinding);
				if (act instanceof UIActionInternal) {
					((UIActionInternal)act).declaration = node;
				}
			}
		}
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding mBinding;

		if ((mBinding = node.resolveMethodBinding()) != null) {
			UIAction act;

			if (!actions.containsKey(mBinding)) {
				act = builder.buildAction(mBinding);
				
				actions.put(mBinding, act);
			} else {
				act = actions.get(mBinding);	
			}

			// set up the invocation list
			if (act.invokedList == null)
				act.invokedList = new ArrayList<UIActionInvocation>();

			UIActionInvocation actionInvoke = new UIActionInvocation();
			actionInvoke.astSourceNode = node;
			actionInvoke.invokedMethod = mBinding;

			ASTNode parent = actionInvoke.astSourceNode;

			while ((parent = parent.getParent()) != null
			 	&& !(parent instanceof MethodDeclaration))
				;

			if (parent != null)
				actionInvoke.invokingMethod = ((MethodDeclaration)parent).resolveBinding();

			act.invokedList.add(actionInvoke);
		}
		return true;
	}
}