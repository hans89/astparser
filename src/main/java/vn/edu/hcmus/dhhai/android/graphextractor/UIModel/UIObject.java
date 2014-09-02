package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree.*;

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

	public List<Set<UIAction>> topEventSets;

	private List<Set<UIAction>> initialStates;

	/**
	 * While each internal action has an executing tree, rooted at the action
	 * itself, UIObject has an executing tree rooted at an ANDNode, whose 
	 * children are the trees of the initial internal actions.
	 */
	private ANDNode<UIActionStatement> uiOBJExecutingTreeRoot;

	/**
	 * Each effective set is a set of invocations, which is a solution of the
	 * UIObject executing tree.
	 */
	private Set<Set<UIActionStatement>> initialEffectiveSets;

	/**
	 * Each state deltas corresponds to an effective set.
	 */
	private List<StateDelta> possibleInitialStateDeltas;

	public Collection<Set<UIAction>> getAllPossibleInitialActionSets() {
		// caching
		if (initialStates != null)
			return initialStates;

		initialStates = new ArrayList<Set<UIAction>>();

		List<UIAction> initialEvents = null;

		if (topEventActions != null) {
			initialEvents = new ArrayList<UIAction>();
			initialEvents.addAll(topEventActions.values());
		}


		// get linked-event set up by inits
		if (initActions != null) {
			// caching
			if (uiOBJExecutingTreeRoot == null) {
				// set up the executing tree of this UIObject

				List<Node<UIActionStatement>> childTrees = 
					new ArrayList<Node<UIActionStatement>>(initActions.size());

				for (UIAction act : initActions.values()) {

					UIActionInternal intAct = (UIActionInternal)act;

					Node<UIActionStatement> childTree = intAct.buildExecutingTree();
					if (childTree != null)
						childTrees.add(childTree);
				}

				uiOBJExecutingTreeRoot 
					= new ANDNode<UIActionStatement>(null, childTrees);
							// empty internal data
			}

			// caching
			if (initialEffectiveSets == null) {
				// set up possible effects of initial actions, in form of
				// action invocations

				Set<Set<SwapOption<UIActionStatement>>> solutionSignatures 
					= ASG.runASG(uiOBJExecutingTreeRoot);

				initialEffectiveSets 
					= new HashSet<Set<UIActionStatement>>();

				for (Set<SwapOption<UIActionStatement>> solSig
													: solutionSignatures) {

					List<TerminalNode<UIActionStatement>> terminalNodes
						= uiOBJExecutingTreeRoot.computeSolutionTerminalNodes(solSig);

					Set<UIActionStatement> nodeSet 
						= new HashSet<UIActionStatement>(terminalNodes.size());

					for (TerminalNode<UIActionStatement> node : terminalNodes) {
						UIActionStatement st = node.getInternalData();
						if (st != null)
							nodeSet.add(st);
					}
					if (nodeSet.size() > 0)
						initialEffectiveSets.add(nodeSet);
				}
			}

			// caching
			if (possibleInitialStateDeltas == null) {
				// calculate possible state deltas from the possible initial
				// effective sets

				possibleInitialStateDeltas 
					= StateDelta.getPossibleStateDelta(initialEffectiveSets);
			}
			

			// we get all possible sets of initital states 
			// basing on event-binding actions found in initial actions

			Set<Set<UIAction>> possibleStates = new HashSet<Set<UIAction>>();

			for (StateDelta delta : possibleInitialStateDeltas) {
				Set<UIAction> newState = new HashSet<UIAction>();

				// we have to mix them with the top events
				if (initialEvents != null)
					newState.addAll(initialEvents);
				
				// we assume that initial actions do not make modal transition
				if (delta.addedActions != null)
						newState.addAll(delta.addedActions);

				if (delta.removedActions != null)
					newState.removeAll(delta.removedActions);

				// we might also want to check for uniqueness, i.e.
				// holding that each state is unique - we use a Set
				if (!newState.isEmpty())
					possibleStates.add(newState);
			}
			// add the set to the return object
			initialStates.addAll(possibleStates);
		} 


		// no init actions, there are only top events
		// and therefore should be only 1 initial state
		if (initialStates.isEmpty()) {
			if (initialEvents != null) {
				initialStates.add(new HashSet<UIAction>(initialEvents));
			}
			else {
				// if there is no action
				// make it as an empty ui object
				initialStates.add(new HashSet<UIAction>(0));
			}	
		}
			
		return initialStates;
	}

	public String getName() {
		return typeBinding.getName();
	}

	@Override
	public String toString() {
		return typeBinding.getName();
	}
}

