package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;


/**
 * Extension for LINKED_EVENT type action
 * Objects of this type also include information about which BIND_EVENT
 * actions can be used to link the event
 * This is the dual class of UIActionEventBinderClass
 */
public class UIActionLinkedEventClass extends UIActionClass {
	/**
	 * the key of the BIND_EVENT method, following the format defined in
	 * UIActionClass#getKey	
	 */
	public String setterKey;

	public UIActionEventBinderClass setterAction;
}