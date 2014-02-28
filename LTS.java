package astparser.UIModel;

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
}

