package astparser.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class MethodReference {
	/* this class defines information of a particular method

	 TYPE:
		EXTERNAL vs INTERNAL: isFromSource
		INTERESTING vs UNINTERESTING: UI actions vs non-UI actions

		EXTERNAL_NON_UI: non UI external method invocations, should be
			ignored. Is not used to create model
		EXTERNAL_UI: external method invocations that relates to UI,
			including setting events, enabling/disabling widgets,
			starting new windows, closing windows, setting other various
			UI states. External UIs are used to check whether INTERNAL 
			actions are actually UI-related.

		There are internal actions that are called by the framework
			(they never gets called in application code), of these:
		
		INTERNAL_INIT: actions that set up windows or widgets. if one is
			not UI-related, it can be ignored.
		INTERNAL_TOP_EVENT: event handlers that are automatically enabled 
			with a window or widget
		INTERNAL_LINKED_EVENT: event handlers that need to be linked by
			an explicit external action.

		Other actions can be seen as Application-defined actions:
		INTERNAL_APP_DEFINED
		Some of these actions may not be called, this may indicate that
			they are unused.
		Some actions may be UI-related, if they (recursively) call UI-related
		actions. Any non UI-related app-defined actions can be ignored.

		Recursive definition of UI-related actions:
			One action is UI-related if:
			- (BASE) it is of EXTERNAL_UI type
			- (BASE) it is an event
			- (INDUCTION) it calls an UI-related action
	*/

	public enum MethodType {
		EXTERNAL_NON_UI,
		EXTERNAL_UI,
		INTERNAL_INIT,
		INTERNAL_TOP_EVENT,
		INTERNAL_LINKED_EVENT,
		INTERNAL_APP_DEFINED
	}
	// each method has methodBinding as its ID
	public IMethodBinding methodBinding;

	// each interesting method has a Android class info 
	// which is used to identify its type
	public UIActionClass metaClassInfo;
	public MethodType type;

	// each internal method has a declaration, which defines action
	// invocations that it calls
	public MethodDeclaration declaration;

	// list of action invocations that calls this method
	public List<ActionInvocation> invokedList;
}
