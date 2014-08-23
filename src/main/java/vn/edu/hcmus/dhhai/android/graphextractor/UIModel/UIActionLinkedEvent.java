package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

/**
 *	Extensions for Linked Event. 
 *	This action is added with event class info (often as a listener interface)
 *	and its corresponding setters.
 *	The setters are detected by looking for objects of eventContainingType which
 * 	are used in the setter methods listed in the UIActionLinkedEventClass meta
 *	info object.
 */
public class UIActionLinkedEvent extends UIActionInternal {

	public Set<UIActionInvocation> setters;

}