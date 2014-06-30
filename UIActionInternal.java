package astparser.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;
import java.util.Map.Entry;

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
	 * 	About merging of paths, we have some initial ideas (unproved):
	 *		- merge all paths that only differ by the external invocation
	 *		- path without branching nodes should be merged with those that have
	 *			branching in order to complete the whole effects
	 */
	public List<LinkedHashSet<UIActionInvocation>> executingPaths;

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
	public Collection<Set<UIActionInvocation>> getPossibleEffects() {

		if (executingPaths == null)
			return null;

		if (possibleEffectsMap == null) {

			// we remove the external actions, and then check which sets are equal
			
			possibleEffectsMap 
			= new HashMap<LinkedHashSet<UIActionInvocation>, Set<UIActionInvocation>>();
			
			for (LinkedHashSet<UIActionInvocation> path : executingPaths) {
				LinkedHashSet<UIActionInvocation> newPath 
					= new LinkedHashSet<UIActionInvocation>(path);

				UIActionInvocation externalInvoke = newPath.iterator().next();
				newPath.remove(externalInvoke);
				
				if (!possibleEffectsMap.containsKey(newPath)) {
					possibleEffectsMap.put(newPath, new HashSet<UIActionInvocation>());
				}

				possibleEffectsMap.get(newPath).add(externalInvoke);
			}
		}
		
		// DEBUG
		// for (Entry<LinkedHashSet<UIActionInvocation>, Set<UIActionInvocation>>
		// 		e : possibleEffectsMap.entrySet()) {
		// 	System.out.print("-Set: ");

		// 	for (UIActionInvocation act : e.getValue()) {
		// 		System.out.print(act.astSourceNode.getName() + " | ");
		// 	}

		// 	for (UIActionInvocation act : e.getKey()) {
		// 		System.out.print(" <- " + act.astSourceNode.getName());
		// 	}
			
		// 	System.out.print(" <- " + this.methodBinding.getKey());	

		// 	System.out.println();
		// }
		// END DEBUG

		return possibleEffectsMap.values();
	}

	public static class StateDelta {
		// if this is a delta that adds/removes actions then
		// at least one of addedActions and removedActions is not empty
		public Set<UIAction> addedActions;
		public Set<UIAction> removedActions;

		// if this is a delta that has start/end modal effect then
		// startModelEffect should not be empty
		public Set<UIActionInvocationStartModal> startModalEffects;
	}
	
	private List<StateDelta> stateDeltas;

	public List<StateDelta> getPossibleStateDelta() {

		if (stateDeltas != null)
			return stateDeltas;
		
		stateDeltas = new ArrayList<StateDelta>();

		Collection<Set<UIActionInvocation>> effectSets = this.getPossibleEffects();


		if (effectSets != null) {
			// with each effective set
			for (Set<UIActionInvocation> effectSet : effectSets) {
				// first check if there is a start modal
				Set<UIAction> addedActions = new HashSet<UIAction>();
				Set<UIAction> removedActions = new HashSet<UIAction>();
				Set<UIActionInvocationStartModal> startModalEffects
					= new HashSet<UIActionInvocationStartModal>();		

				for (UIActionInvocation act : effectSet) {
					// is a modal action
					if (act instanceof UIActionInvocationStartModal) {
						startModalEffects.add((UIActionInvocationStartModal)act);
					}
					// or an event-enabling action
					else if (act instanceof UIActionInvocationBindEvent) {
					// either by binding an event
						UIActionInvocationBindEvent ebActInv = 
							(UIActionInvocationBindEvent)act;

						if (ebActInv.bindedEvents != null) {
							addedActions.addAll(ebActInv.bindedEvents);
						}
					} else if (act instanceof UIActionInvocationEnableWidget) {
					// or by enabling a widget with some already binded events
						UIActionInvocationEnableWidget ewActInv = 
							(UIActionInvocationEnableWidget)act;

						if (ewActInv.enabledEvents != null)
							addedActions.addAll(ewActInv.enabledEvents);

						if (ewActInv.disabledEvents != null)
							removedActions.addAll(ewActInv.disabledEvents);
					}
				}

				if (startModalEffects.size() > 0 
						|| addedActions.size() > 0
						|| removedActions.size() > 0) {
					StateDelta delta = new StateDelta();

					if (startModalEffects.size() > 0)
						delta.startModalEffects = startModalEffects;

					if (addedActions.size() > 0)
						delta.addedActions = addedActions;

					if (removedActions.size() > 0)
						delta.removedActions = removedActions;

					stateDeltas.add(delta);
				}
			}
		}

		return stateDeltas;
	}

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