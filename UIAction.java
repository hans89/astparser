package astparser.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public abstract class UIAction {
	public UIActionClass metaClassInfo;


	public static class UIInSourceAction extends UIAction {
		public List<UIAction> callees;

		public UIInSourceAction() {
			callees = new ArrayList<UIAction>();
		}
	}

	public static class UIOutSourceAction extends UIAction {

	}

	public static class UILinkedEvent extends UIInSourceAction {
		public List<UIOutSourceAction> eventSetters;

		public UILinkedEvent() {
			super();
			eventSetters = new ArrayList<UIOutSourceAction>();
		}
	}

	public static class UIBaseAction extends UIInSourceAction {
		public UIObject container;

	}

	public static class UIInitAction extends UIBaseAction {

	}

	public static class UIBaseEvent extends UIBaseAction {

	}
}

