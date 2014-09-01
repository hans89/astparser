package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

import vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree.*;

public class UIActionInternal extends UIAction {
	
	/**
	 *	The AST node that defines the method. It is actually the source code
	 *	of the method. EXTERNAL methods don't have this property.
	 *
	 *	This property is Eclipse JDT dependent.
	 */
	public MethodDeclaration declaration;

	/**
	 *	Paths of calling
	 *	Each path is a series of calls created by the method, starting from bottom
	 *	with the last external method to be called to the method.
	 * 	For example, if the method calls a method A, A then calls B, B then calls
	 *	external method setOnClickListener, then the path will be
	 *	setOnClickListener -> B -> A
	 *	The path is explored using DFS, starting from the external method, visiting
	 *	through the calling method (using IMethodBinding fields declared in 
	 *	UIActionInvocation) until it reaches an implicitly called method, which
	 *	is of type INTERNAL_UI
	 *
	 *	We can extend the paths to include branching among the invocations: add
	 *	the branching blocks as nodes between the invokee and the invoker. Then
	 *	the complete path will include all the needed information about the effects
	 *	of an INTERNAL_UI. By merging the paths (grouping the actions as AND), 
	 *	we can conclude all the possible effects (the ungroupable paths are OR
	 *	effects).
	 *
	 */
	public List<LinkedHashSet<UIActionStatement>> executingPaths;

	
	/**
	 * An execting tree of UIActionInternal is an ANDORTree of UIActionStatement
	 * where ORNodes are UIActionBranchingStatement, ANDNodes and terminal nodes
	 * are UIActionInvocation.
	 * 
	 * The tree is rooted at the UIActionInternal. Since this method might not
	 * be executed, we build a fake UIActionInvocation with no invocation information.
	 * Or rather interesting we can make the root node with empty data (null)
	 *
	 */

	protected ANDNode<UIActionStatement> executingTreeRoot;

	/**
	 * Build the executing tree from the executingPaths
	 */
	public ANDNode<UIActionStatement> buildExecutingTree() {
		if (executingPaths == null)
			return null;
		
		// caching
		if (executingTreeRoot != null) 
			return executingTreeRoot;

		// fake root node
		UIActionInvocation rootData = new UIActionInvocation();
		rootData.invokedMethod = this.methodBinding;
		// or rather
		// rootData = null;

		// empty children
		executingTreeRoot = new ANDNode<UIActionStatement>(rootData, null);

		HashMap<Node<UIActionStatement>, List<LinkedList<UIActionStatement>>>
		upPathsMap = new 
			HashMap<Node<UIActionStatement>, List<LinkedList<UIActionStatement>>>();

		List<LinkedList<UIActionStatement>> rootPaths = 
				new ArrayList<LinkedList<UIActionStatement>>(executingPaths.size());

		for (LinkedHashSet<UIActionStatement> path : executingPaths) {
			rootPaths.add(new LinkedList<UIActionStatement>(path));
		}

		upPathsMap.put(executingTreeRoot, rootPaths);

		// only ANDNode will be added to this queue
		Queue<ANDNode<UIActionStatement>> nodeQueue 
				= new ArrayDeque<ANDNode<UIActionStatement>>();		

		nodeQueue.offer(executingTreeRoot);


		ANDNode<UIActionStatement> current;

		while ((current = nodeQueue.poll()) != null) {
			List<LinkedList<UIActionStatement>> upPaths = upPathsMap.get(current);

			if (upPaths != null) {

				// branching at this level
				HashMap<Statement, ANDNode<UIActionStatement>> branchANDNodes 
					= new HashMap<Statement, ANDNode<UIActionStatement>>();

				HashMap<Statement, ORNode<UIActionStatement>> branchPointORNodes
					= new HashMap<Statement, ORNode<UIActionStatement>>();

				HashMap<UIActionInvocation, ANDNode<UIActionStatement>> 
					invocationANDNodes
					= new HashMap<UIActionInvocation, ANDNode<UIActionStatement>>();

				for (LinkedList<UIActionStatement> path : upPaths) {
					// get most top action, i.e. action for this level
					// remember that the executing paths are in reverse order
					UIActionStatement nextAction;
					// get the last of the path
					if ((nextAction = path.pollLast()) != null) {
						// nextAction could be an UIActionInvocation
						// or a UIActionBranchingStatement
						ANDNode<UIActionStatement> nextNode = null;
						List<LinkedList<UIActionStatement>> nextPaths;

						if (nextAction instanceof UIActionInvocation) {

							UIActionInvocation nextInvoke = (UIActionInvocation)nextAction;
							
							if (!invocationANDNodes.containsKey(nextInvoke)) {

								if (path.size () > 0) {
									nextNode = new ANDNode<UIActionStatement>(nextAction, null);

									// add new child for current node
									current.addChild(nextNode);	
									// push for further exploration
									nodeQueue.offer(nextNode);
									// update for current level matching and sharing this node
									invocationANDNodes.put(nextInvoke, nextNode);	
								} else {
									// terminal node
									current.addChild
									(new TerminalNode<UIActionStatement>(nextAction));	
								}
							} else {
								nextNode = invocationANDNodes.get(nextInvoke);
							}
						} else if (nextAction instanceof UIActionBranchingStatement) {
							UIActionBranchingStatement nextBranching
								= (UIActionBranchingStatement) nextAction;

							Statement branchPoint = nextBranching.getBranchingPoint();
							Statement branch = nextBranching.getBranch();

							if (!branchANDNodes.containsKey(branch)) {
								ORNode<UIActionStatement> branchPointNode;

								if (!branchPointORNodes.containsKey(branchPoint)) {
									branchPointNode 
									= new ORNode<UIActionStatement>(null, null);

									// add new child for current node
									current.addChild(branchPointNode);
									// update for current level matching and sharing this node
									branchPointORNodes.put(branchPoint, branchPointNode);
								} else {
									branchPointNode =
										branchPointORNodes.get(branchPoint);
								}


								nextNode = new ANDNode<UIActionStatement>(nextBranching, null);

								// add this as child of the branchPointNode
								branchPointNode.addChild(nextNode);
								// push for further exploration
								nodeQueue.offer(nextNode);
								// update for current level matching and sharing this node
								branchANDNodes.put(branch, nextNode);
							} else {
								nextNode = branchANDNodes.get(branch);
							}
						}

						if (nextNode != null) {
							if (!upPathsMap.containsKey(nextNode)) {
								// update the path list for this new node
								nextPaths = new ArrayList<LinkedList<UIActionStatement>>();
								upPathsMap.put(nextNode, nextPaths);
							} else {
								nextPaths = upPathsMap.get(nextNode);
							}
							
							if (path.size() > 0)
								nextPaths.add(path);
						}
					}	
				}

				// we are done at this level, may want to free data
				// for saving memory 
				upPathsMap.remove(current);
			}
		}
				
		return executingTreeRoot;	
	}

	private	HashMap<LinkedHashSet<UIActionInvocation>, Set<UIActionInvocation>> 
		possibleEffectsMap; 
	/**
	 *	Calculate the effect of an INTERNAL_UI action
	 *	Version 0.1: no branching information
	 *	TODO: add branching and adjust the merge operation
	 *
	 *	@return OR-set of possible effects. Each possible effect is an AND-set of
	 *			actions
	 */
	// public Collection<Set<UIActionInvocation>> getPossibleEffects() {

	// 	if (executingPaths == null)
	// 		return null;

	// 	if (possibleEffectsMap == null) {

	// 		// we remove the external actions, and then check which sets are equal
			
	// 		possibleEffectsMap 
	// 		= new HashMap<LinkedHashSet<UIActionInvocation>, Set<UIActionInvocation>>();
			
	// 		// TODO: better refine executingPaths, to include control flow
	// 		// executingPaths must include branching
	// 		// we then merge the effects of actions that are in the same branch
	// 		// if-then
	// 		// if-then-else
	// 		// switch
	// 		// break, continue, return
	// 		for (LinkedHashSet<UIActionInvocation> path : executingPaths) {
	// 			LinkedHashSet<UIActionInvocation> newPath 
	// 				= new LinkedHashSet<UIActionInvocation>(path);

	// 			UIActionInvocation externalInvoke = newPath.iterator().next();
	// 			newPath.remove(externalInvoke);
				
	// 			if (!possibleEffectsMap.containsKey(newPath)) {
	// 				possibleEffectsMap.put(newPath, new HashSet<UIActionInvocation>());
	// 			}

	// 			possibleEffectsMap.get(newPath).add(externalInvoke);
	// 		}
	// 	}
		
	// 	// DEBUG
	// 	// for (Entry<LinkedHashSet<UIActionInvocation>, Set<UIActionInvocation>>
	// 	// 		e : possibleEffectsMap.entrySet()) {
	// 	// 	System.out.print("-Set: ");

	// 	// 	for (UIActionInvocation act : e.getValue()) {
	// 	// 		System.out.print(act.astSourceNode.getName() + " | ");
	// 	// 	}

	// 	// 	for (UIActionInvocation act : e.getKey()) {
	// 	// 		System.out.print(" <- " + act.astSourceNode.getName());
	// 	// 	}
			
	// 	// 	System.out.print(" <- " + this.methodBinding.getKey());	

	// 	// 	System.out.println();
	// 	// }
	// 	// END DEBUG

	// 	return possibleEffectsMap.values();
	// }

	// public static class StateDelta {
	// 	// if this is a delta that adds/removes actions then
	// 	// at least one of addedActions and removedActions is not empty
	// 	public Set<UIAction> addedActions;
	// 	public Set<UIAction> removedActions;

	// 	// if this is a delta that has start/end modal effect then
	// 	// startModelEffect should not be empty
	// 	public Set<UIActionInvocationStartModal> startModalEffects;
	// }
	
	// private List<StateDelta> stateDeltas;

	// // TODO: better refine deltas
	// // by refining possible effects: add branching
	// public List<StateDelta> getPossibleStateDelta() {

	// 	if (stateDeltas != null)
	// 		return stateDeltas;
		
	// 	stateDeltas = new ArrayList<StateDelta>();

	// 	Collection<Set<UIActionInvocation>> effectSets = this.getPossibleEffects();


	// 	if (effectSets != null) {
	// 		// with each effective set
	// 		for (Set<UIActionInvocation> effectSet : effectSets) {
	// 			// first check if there is a start modal
	// 			Set<UIAction> addedActions = new HashSet<UIAction>();
	// 			Set<UIAction> removedActions = new HashSet<UIAction>();
	// 			Set<UIActionInvocationStartModal> startModalEffects
	// 				= new HashSet<UIActionInvocationStartModal>();		

	// 			for (UIActionInvocation act : effectSet) {
	// 				// is a modal action
	// 				if (act instanceof UIActionInvocationStartModal) {
	// 					startModalEffects.add((UIActionInvocationStartModal)act);
	// 				}
	// 				// or an event-enabling action
	// 				else if (act instanceof UIActionInvocationBindEvent) {
	// 				// either by binding an event
	// 					UIActionInvocationBindEvent ebActInv = 
	// 						(UIActionInvocationBindEvent)act;

	// 					if (ebActInv.bindedEvents != null) {
	// 						addedActions.addAll(ebActInv.bindedEvents);
	// 					}
	// 				} else if (act instanceof UIActionInvocationEnableWidget) {
	// 				// or by enabling a widget with some already binded events
	// 					UIActionInvocationEnableWidget ewActInv = 
	// 						(UIActionInvocationEnableWidget)act;

	// 					if (ewActInv.enabledEvents != null)
	// 						addedActions.addAll(ewActInv.enabledEvents);

	// 					if (ewActInv.disabledEvents != null)
	// 						removedActions.addAll(ewActInv.disabledEvents);
	// 				}
	// 			}

	// 			if (startModalEffects.size() > 0 
	// 					|| addedActions.size() > 0
	// 					|| removedActions.size() > 0) {
	// 				StateDelta delta = new StateDelta();

	// 				if (startModalEffects.size() > 0)
	// 					delta.startModalEffects = startModalEffects;

	// 				if (addedActions.size() > 0)
	// 					delta.addedActions = addedActions;

	// 				if (removedActions.size() > 0)
	// 					delta.removedActions = removedActions;

	// 				stateDeltas.add(delta);
	// 			}
	// 		}
	// 	}

	// 	return stateDeltas;
	// }

	// TODO: remove all the codes below and their dependencies

	// private	Set<UIActionInvocation> allPossibleEffects; 

	// public Set<UIActionInvocation> getAllPossibleEffects() {
	// 	if (executingPaths == null)
	// 		return null;

	// 	if (allPossibleEffects == null) {
	// 		allPossibleEffects = new HashSet<UIActionInvocation>();

	// 		for (LinkedHashSet<UIActionInvocation> path : executingPaths) {
	// 			allPossibleEffects.add(path.iterator().next());
	// 		}	
	// 	}
		

	// 	return allPossibleEffects;
	// }

	// // BIND_EVENT & ENABLE_WIDGET
	// public Set<UIAction> getEnabledEvents() {
	// 	Set<UIActionInvocation> effects = getAllPossibleEffects();

	// 	Set<UIAction> enabledEvents = new HashSet<UIAction>();
		
	// 	if (effects != null) {
			

	// 		for (UIActionInvocation effect : effects) {
				
	// 			if (effect instanceof UIActionInvocationBindEvent) {
	// 			// either by binding an event
	// 				UIActionInvocationBindEvent ebActInv = 
	// 					(UIActionInvocationBindEvent)effect;

	// 				if (ebActInv.bindedEvents != null) {
	// 					enabledEvents.addAll(ebActInv.bindedEvents);
	// 				}
	// 			} else if (effect instanceof UIActionInvocationEnableWidget) {
	// 			// or by enabling a widget with some already binded events
	// 				UIActionInvocationEnableWidget ewActInv = 
	// 					(UIActionInvocationEnableWidget)effect;

	// 				if (ewActInv.enabledEvents != null)
	// 					enabledEvents.addAll(ewActInv.enabledEvents);
	// 			}
	// 		}
	// 	}
	// 	return enabledEvents;
	// }

	// // ENABLE_WIDGET
	// public Set<UIAction> getDisabledEvents() {

	// 	Set<UIActionInvocation> effects = getAllPossibleEffects();

	// 	Set<UIAction> disabledEvents = new HashSet<UIAction>();
		
	// 	if (effects != null) {
	// 		for (UIActionInvocation effect : effects) {
	// 			if (effect instanceof UIActionInvocationEnableWidget) {
	// 				UIActionInvocationEnableWidget ewActInv = 
	// 					(UIActionInvocationEnableWidget)effect;

	// 				if (ewActInv.disabledEvents != null)
	// 					disabledEvents.addAll(ewActInv.disabledEvents);
	// 			}
	// 		}
	// 	}
	// 	return disabledEvents;
	// }

	// public Set<UIActionInvocation> getStartEndModals() {
	// 	Set<UIActionInvocation> effects = getAllPossibleEffects();

	// 	Set<UIActionInvocation> startModals = new HashSet<UIActionInvocation>();
		
	// 	if (effects != null) {
	// 		for (UIActionInvocation effect : effects) {
	// 			if (effect instanceof UIActionInvocationStartModal) {
	// 				UIActionInvocationStartModal smActInv = 
	// 					(UIActionInvocationStartModal)effect;

	// 				startModals.add(smActInv);
	// 			}
	// 		}
	// 	}
	// 	return startModals;

	// }
}