package astparser.UIModel;

import java.util.*;

/**
 * Extension for BIND_EVENT type action
 * Objects of this type also include information about which LINKED_EVENT
 * it can bind
 * This is the dual class of UIActionLinkedEventClass
 */
public class UIActionEventBinderClass extends UIActionClass {
	public List<UIActionLinkedEventClass> linkedEventList;
}