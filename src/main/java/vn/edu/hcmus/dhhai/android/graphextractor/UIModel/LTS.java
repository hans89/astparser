package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;

public class LTS<S, A> {

	public Set<S> states;
	public Set<A> actions;
	public Set<Transition<S,A>> transitions;
	public Set<A> tauActions;
	public Set<S> initialStates;
	public Set<S> terminalStates;

	public LTS() {
		states = new HashSet<S>();
		actions = new HashSet<A>();
		transitions = new HashSet<Transition<S,A>>();
		tauActions = new HashSet<A>();
		initialStates = new HashSet<S>();
		terminalStates = new HashSet<S>();
	}

	public static class Transition<S, A> {
		public S fromState;
		public A labelledAction;
		public S toState;
	}

	public void addTransition(S fromState, A action, S toState) {
		Transition<S, A> newTransition
				= new Transition<S, A>();

		newTransition.fromState = fromState;
		newTransition.labelledAction = action;
		newTransition.toState = toState;
		this.transitions.add(newTransition);
	}

	private Map<S, Map<S, List<A>>> adjacencyMap;
	public Map<S, Map<S, List<A>>> makeAdjacencyMap() {
		if (adjacencyMap != null)
			return adjacencyMap;

		adjacencyMap = new HashMap<S, Map<S, List<A>>>();

		for (Transition<S,A> trans : transitions) {
			Map<S, List<A>> internalMap;

			if (!adjacencyMap.containsKey(trans.fromState)) {
				internalMap = new HashMap<S, List<A>>();
				adjacencyMap.put(trans.fromState, internalMap);
			} else {
				internalMap = adjacencyMap.get(trans.fromState);
			}

			List<A> transitionList;

			if (!internalMap.containsKey(trans.toState)) {
				transitionList = new ArrayList<A>();
				internalMap.put(trans.toState, transitionList);
			} else {
				transitionList = internalMap.get(trans.toState);
			}

			transitionList.add(trans.labelledAction);
		}

		return adjacencyMap;
	}
}

