package astparser.UIModel;

import java.util.*;

public class LTS<S, A> {

	Set<S> states;
	Set<A> actions;
	Set<Transition<S,A>> transitions;
	Set<A> tauActions;
	Set<S> initialStates;
	Set<S> terminalStates;


	public static class Transition<S, A> {
		public S fromState;
		public A labelledAction;
		public S toState;
	}
}

