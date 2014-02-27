package astparser.UIModel;

import java.util.*;

public class UIObjectClass {

	public enum UIType {
		Window,
		Dialog,
		Widget
	}

	public String classKey;
	public UIType uiType;
	public List<UIActionClass> actionsInfo;
	

	public UIObjectClass() {
		actionsInfo = new ArrayList<UIActionClass>();
	
	}
}