package astparser.UIModel;

import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class UIObject {
	public ITypeBinding typeBinding;
	public UIObjectClass metaClassInfo;

	/**
	 * map from an init method to list of UI actions that are called
	 *	by the method (with all possible paths from the action
	 *	to the method)
	 */
	public HashMap<IMethodBinding, List<Set<ActionInvocation>>>
		initPaths;

	/**
	 * map from a top-event method to list of UI actions that are called
	 *	by the method (with all possible paths from the action
	 *	to the method)
	 */
	public HashMap<IMethodBinding, List<Set<ActionInvocation>>>
		topEventPaths;

	/**
	 * list of linked-events that are linked by the initPaths
	 */
	public HashSet<UILinkedEventObject> initEvents;

	

	public static class UILinkedEventObject {
		public ITypeBinding typeBinding;

		/**
		 * map from a linked-event method to list of UI actions that are called
		 *	by the method (with all possible paths from the action
		 *	to the method)
		 */
		public HashMap<IMethodBinding, List<Set<ActionInvocation>>>
			eventPaths;		
		public Set<ActionInvocation> setters;
	}
}

