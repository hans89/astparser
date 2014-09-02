package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class StateDelta {

	// if this is a delta that adds/removes actions then
	// at least one of addedActions and removedActions is not empty
	public Set<UIAction> addedActions;
	public Set<UIAction> removedActions;

	// if this is a delta that has start/end modal effect then
	// startModelEffect should not be empty
	public Set<UIActionInvocationStartModal> startModalEffects;

	/* ---------------------------------------------------------------------*/
	/**
	 * Static method to identify deltas from effectSets
	 * Each effect set is a collection of UIActionInvocation, where
	 * each UIActionInvocation can be binding an event, enabling/disabling a widget,
	 * or starting a new window.
	 *
	 * The effect of each UIActionInvocation is identified in:
	 *		Step 4: ASTNodeUtils.bindEventSetters
	 *		Step 7: ASTNodeUtils.bindEnableWidgetWithEvents
	 *		Step 8: ASTNodeUtils.bindStartModals
	 *
	 * This method task is to combine all the effects of each action into 
	 * the effects of the whole action sets. Each action of course can cancel
	 * out one another's effects. Set structures are used to enable this behavior.
	 */ 
	public static List<StateDelta> getPossibleStateDelta(
							Collection<Set<UIActionStatement>> effectSets) {
		
		List<StateDelta> stateDeltas = null;

		if (effectSets != null) {

			stateDeltas = new ArrayList<StateDelta>();
			// with each effective set
			for (Set<UIActionStatement> effectSet : effectSets) {
				// first check if there is a start modal
				Set<UIAction> addedActions = new HashSet<UIAction>();
				Set<UIAction> removedActions = new HashSet<UIAction>();
				Set<UIActionInvocationStartModal> startModalEffects
					= new HashSet<UIActionInvocationStartModal>();		

				for (UIActionStatement act : effectSet) {
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
}


