package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

/**
 *	Objects of this class store data about each and every actual method that are 
 *	defined or used in the application. These include external methods defined in
 *	binary/bytecode libraries, and the application methods defined in the
 *	application source code.
 *
 *	If the method is an UI-related method (that are defined by an UIActionClass)
 *	then it is accompanied by the a UIActionClass object that describes its 
 *	possibly interesting properties.
 *
 *	If the method is a non-UI raw method, either being an external non-UI method,
 *	or an app-defined non-UI method, it won't have a UIActionClass object. Although
 *	this kind of method may be non-UI related at first, it may be related with 
 *	UI methods through a possible chain of method invocations, therefore it is
 *	also put in the category of UIAction
 *
 *	The type of each actual method is then the combination of the UIActionClass
 *	object type (UIActionClass.UIActionType, UIActionClass.UICategory) and the 
 *	specific type (UIAction.ActionType)
 *	
 *	This class is Eclipse JDT dependent.
 *
 *	@author Hai Dang (dhhai.uns@gmail.com)
 */

public class UIAction {
	
	/**
	 * 	Meta class info defines interesting UI-related properties of the method
	 * 	(possibly) non-UI methods don't have this property (is null).
	 */
	public UIActionClass metaClassInfo;

	/**
	 *	Action type provide a quick property of whether this method is external
	 *	or interal, is definitely UI-related or possibly non-UI related.
	 * 	This combines with metaClassInfo type (UIActionClass.UIActionType, 
	 *	UIActionClass.UICategory) to provide the complete classification of
	 *	action types.
	 *
	 *	EXTERNAL_UI are OUTSOURCE, INTERNAL_UI are INSOURCE, both are further
	 *	classified by UIActionClass.UIActionType:
	 *		- EXTERNAL_UI: BIND_EVENT, START_MODAL, END_MODAL, OPEN_MENU, ENABLE_WIDGET
	 *		- INTERNAL_UI: 	INIT, TOP_EVENT, LINKED_EVENT
	 *	
	 *	EXTERNAL_NON_UI are non-UI external invocations, and are usually ignored.
	 *	INTERNAL_APP_DEFINED are possibly non-UI related, and can be refined to
	 *		INTERNAL_APP_UI and INTERNAL_APP_NON_UI, where INTERAL_APP_UI are 
	 *		defined recursively as methods that can call recursively any UI-related
	 *		methods (EXTERNAL_UI/INTERNAL_UI). INTERNAL_APP_NON_UI are the rest.
	 */
	public ActionType type;
	public enum ActionType {
		EXTERNAL_NON_UI,
		EXTERNAL_UI,
		INTERNAL_UI,
		INTERNAL_APP_DEFINED
	}

	/**
	 *	The containing type of the method definition
	 */
	public ITypeBinding containingType;
	
	/**
	 *	The definite info of a method, often used as its key
	 *	IMethodBinding is a Eclipse JDT class, reprensting a unique object
	 *	for each method. The definition and all invocations of a single method
	 *	are linked to this binding, therefore it can be used to identified if
	 *	the invocation is of a concerned method.
	 *
	 *	All methods known by the compiler must have this property.
	 *
	 *	This property is Eclipse JDT dependent.
	 */
	public IMethodBinding methodBinding;
	
	/**
	 *	The list of actual invocation of this method
	 *	In the Android UI model, all INTERNAL_UI methods
	 * 	(INIT, TOP_EVENT, LINKED_EVENT) should have this list empty or null
	 */
	public List<UIActionInvocation> invokedList;

	public String getName() {
		return containingType.getName() + "#" + methodBinding.getName();
	}

	public final static UIAction NullAction = new UIAction();
}

