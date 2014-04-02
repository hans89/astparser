package astparser.UIModel;

import java.util.*;
import org.eclipse.jdt.core.dom.*;

/**
 *	Objects of this class represent UI windows or widgets which are defined in
 *	the application source code
 *
 *	These UI windows or widgets are actually extended from interesting UI 
 *	windows/widgets defined by the UI framework. The meta class of these
 *	are stored in the UIObjectClass.
 */
public class UIObject {
	/**
	 *	The key of this class. This is Eclipse JDT dependent.
	 */
	public ITypeBinding typeBinding;

	/**
	 *	Meta class info of this window or widget.
	 *	The meta class info defines way to detect if this window or widget is
	 *	a subclass of interesting UI classes. It also defines the list of
	 *	possibly interesting methods and events.
	 */
	public UIObjectClass metaClassInfo;

	/**
	 *	Map from an init method key the extra information
	 *	These are initializers that will be call implicitly by the framework
	 *	The most important information to be extracted is the list of 
	 *	linked-events enabled by these actions, using the "executingPaths"
	 *	property of UIActionInternal class
	 */
	public HashMap<IMethodBinding, UIAction> initActions;

	/**
	 * 	Map from a top-event method key to the actual info
	 *	These are top-level events that are automatically enabled whenever
	 *	this window/widget is enabled
	 *	The most important information to be extracted is the list of events
	 *	enabled/disabled by each event, or the action to start, end, show, or 
	 *	hide another window, menu, or dialog
	 */
	public HashMap<IMethodBinding, UIAction> topEventActions;

	public Set<UIAction> getAllInitialEvents() {
		Set<UIAction> initialEvents = new HashSet<UIAction>();

		if (topEventActions != null)
			initialEvents.addAll(topEventActions.values());

		// get linked-event set up by inits
		if (initActions != null)
			for (UIAction act : initActions.values()) {
				Set<UIAction> enabledEvents =
						((UIActionInternal)act).getEnabledEvents();

				initialEvents.addAll(enabledEvents);				
			}

		return initialEvents;
	}
}

