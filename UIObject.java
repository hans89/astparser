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

	// public Set<UIAction> getAllInitialEvents() {
	// 	Set<UIAction> initialEvents = new HashSet<UIAction>();

	// 	if (topEventActions != null)
	// 		initialEvents.addAll(topEventActions.values());

	// 	// get linked-event set up by inits
	// 	if (initActions != null)
	// 		for (UIAction act : initActions.values()) {
	// 			Set<UIAction> enabledEvents =
	// 					((UIActionInternal)act).getEnabledEvents();

	// 			initialEvents.addAll(enabledEvents);				
	// 		}

	// 	return initialEvents;
	// }

	public List<Set<UIAction>> topEventSets;

	private List<Set<UIAction>> initialStates;

	private Set<UIAction> initialEvents;

	public Collection<Set<UIAction>> getAllPossibleInitialActionSets() {

		if (initialStates != null)
			return initialStates;

		initialStates = new ArrayList<Set<UIAction>>();

		if (topEventActions != null) {
			initialEvents = new HashSet<UIAction>();
			initialEvents.addAll(topEventActions.values());
		}


		// get linked-event set up by inits
		if (initActions != null) {
			// if there are init actions, we should mix their effects
			// each init action may have multiple effect sets, 
			// so we need to mix all of them together
			// this is a set selection problem

			// TODO
			List<List<UIActionInternal.StateDelta>> stateDeltaAlphabets
				= new ArrayList<List<UIActionInternal.StateDelta>>();

			for (UIAction act : initActions.values()) {

				UIActionInternal intAct = (UIActionInternal)act;

				List<UIActionInternal.StateDelta> possibleStateDeltas
					= intAct.getPossibleStateDelta();
				if (possibleStateDeltas != null && !possibleStateDeltas.isEmpty())
					stateDeltaAlphabets.add(possibleStateDeltas);	
			}
			SetSelector<UIActionInternal.StateDelta> stateDeltaSelector 
				= new SetSelector<UIActionInternal.StateDelta>(stateDeltaAlphabets);

			List<List<UIActionInternal.StateDelta>>
				possibleInitialStateDeltas = stateDeltaSelector.getSelectionSet();

			// we got all possible sets of initital states 
			// basing on event-binding actions found in initial actions

			Set<Set<UIAction>> possibleStates = new HashSet<Set<UIAction>>();

			for (List<UIActionInternal.StateDelta> select :
					possibleInitialStateDeltas) {
				Set<UIAction> newState = new HashSet<UIAction>();

				// we have to mix them with the top events
				if (initialEvents != null)
					newState.addAll(initialEvents);
				
				
				// we assume that initial actions do not make modal transition
				for (UIActionInternal.StateDelta delta : select) {
					if (delta.addedActions != null)
						newState.addAll(delta.addedActions);

					if (delta.removedActions != null)
						newState.removeAll(delta.removedActions);
				}

				// we might also want to check for uniqueness, i.e.
				// holding that each state is unique - we use a Set
				if (!newState.isEmpty())
					possibleStates.add(newState);
			}
			// add the set to the return object
			initialStates.addAll(possibleStates);
		} else { 
			// no init actions, there are only top events
			// and therefore should be only 1 initial state
			if (initialEvents != null)
				initialStates.add(initialEvents);
		}

		// if there is no action
		// make it as an empty ui object
		if (initialStates.isEmpty()) {
			Set<UIAction> emptySet = new HashSet<UIAction>();
			emptySet.add(new UIAction());
			initialStates.add(emptySet);
		}
			
		return initialStates;
	}

	public String getName() {
		String name = typeBinding.getName();
		if (name.equals("")) {

		}

		return name;
	}
}

