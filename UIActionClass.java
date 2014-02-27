package astparser.UIModel;

import java.util.*;

public class UIActionClass {

	public String methodName;
	public String classKey;
	public UIActionCategory category;
	public UIActionType type;

	public enum UIActionCategory {
		INSOURCE,
		OUTSOURCE
	}

	public enum UIActionType {
		INIT,
		TOP_EVENT,
		LINKED_EVENT,
		BIND_EVENT,
		START_MODAL,
		END_MODAL,
		OPEN_MENU,
		ENABLE_WIDGET
	}

	public String getKey() {
		return classKey + "#" + methodName;
	}

	public static class UIActionLinkedEventClass extends UIActionClass {
		public String setterKey;
		public UIActionEventBinderClass setterAction;
	}

	public static class UIActionEventBinderClass extends UIActionClass {
		public List<UIActionLinkedEventClass> linkedEventList;
	}
}