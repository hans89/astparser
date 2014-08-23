package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;

/**
 *	Objects of this class store metadata of Android UI-related methods
 *  including:
 *	- method name and method's containing class
 *	- method category: whether the method is final, or is overridable/definable
 *	- method type: where the method is used for definining initialization of
 *	 	windows/widgets, or to set up events, to disable/enable widgets
 *
 *	This class is framework-independent. It only depends on the event-driven OO
 *	programming paradigm: one that have methods grouped in classes, and have
 *	events binded with event handlers to manage events.
 *
 *	Currently it is used for Android application framework in Java language.
 *
 *	@author Hai Dang (dhhai.uns@gmail.com)
 */
public class UIActionClass {

	/**
	 * method name 
	 */
	public String methodName;

	/**
	 * method's containg class qualified name
	 */
	public String classKey;

	/**
	 * method's category
	 * - OUTSOURCE: the method is final, can only be called
	 * - INSOURCE: the method requires overriding, can be called by the app
	 * 		or only by the framework
	 */
	public UIActionCategory category;
	public enum UIActionCategory {
		INSOURCE,
		OUTSOURCE
	}

	/**
	 * method's type
	 * - INIT: overriding method, is used to define initalization
	 *		of windows/widgets
	 * - TOP_EVENT: overridable method, is used to define top level events,
	 *		those are automatically enabled whenever a window/widget is enabled
	 * - LINKED_EVENT: overridable method of a certain event listener,
	 *		the listener instance must be set up by a BIND_EVENT method
	 *		to be activated
	 * - BIND_EVENT: external, final method provided by the framework, is used
	 *		to bind a linked event (LINKED_EVENT) to a certain window/widget
	 * - START_MODAL: external, final method provided by the framework, is used
	 *		to start another modal window
	 * - END_MODAL: external, final method provided by the framework, is used to
	 *		close a certain modal window
	 * - OPEN_MENU: external, final method provided by the framework, is used to
	 *		extend the current available set of events by showing a menu
	 * - ENABLE_WIDGET: external, final method provided by the framework, is used 
	 *		to enable or disable a widget, along with its top-events and linked-
	 *		events
	 */ 
	public UIActionType type;
	public enum UIActionType {
		INIT,
		TOP_EVENT,
		LINKED_EVENT,
		BIND_EVENT,
		START_MODAL,
		END_MODAL,
		OPEN_MENU,
		ENABLE_WIDGET,
	}

	/**
	 * return a key of the current method, simply using method's name and class
	 * E.g: android.view.View#setOnClickListener
	 */
	public String getKey() {
		return classKey + "#" + methodName;
	}
}