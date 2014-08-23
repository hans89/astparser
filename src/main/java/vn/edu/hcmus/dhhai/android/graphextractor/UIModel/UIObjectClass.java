package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;

/**
 *	Objects of this class reprensent meta info about UI classes of a certain
 *	UI framework.
 *	This class only depends on the event-driven OO programming paradigm approach
 *
 *	There should be at least window types and widget types. 
 */
public class UIObjectClass {

	/**
	 * Type of the UI object
	 */
	public UIType uiType;
	public enum UIType {
		Window,
		Dialog,
		Widget
	}

	/**
	 *	The fullly qualified name of the class, this is used to detect certain
	 *	extensions of the class
	 */
	public String classKey;
	
	/**
	 *	List of possibly interesting methods of the class, some of which are
	 *	final, some of which are overridable
	 */
	public List<UIActionClass> actionsInfo;
	

	public UIObjectClass() {
		actionsInfo = new ArrayList<UIActionClass>();
	}
}