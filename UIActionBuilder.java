package astparser.UIModel;

import astparser.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;
/**
 *	This class build an action from its IMethodBinding and meta-data from
 * 	a list of UIActionClass, which provides possibly interesting UI methods
 *
 *	Using the IMethodBinding info, this class will select the appropriate
 *	sub-class of UIAction and set up the following information:
 *	For UIAction:
 *		metaClassInfo, type, methodBinding, containing type
 *	For UIActionExternal:
 *		set up the class
 *	For UIActionInternal:
 *		set up the class and method declaration node
 *	For UIActionLinkedEvent:
 *		set up the class
 */
public class UIActionBuilder {
	private HashMap<String, UIActionClass> actionMetaData;

	public UIActionBuilder(HashMap<String, UIActionClass> map) {
		actionMetaData = map;
	}

	public UIAction buildAction(IMethodBinding methodBinding) {
		return buildAction(methodBinding, null);
	}

	public UIAction buildAction(IMethodBinding methodBinding, MethodDeclaration node) {
		UIAction action;

		ITypeBinding declaringClass = methodBinding.getDeclaringClass();

		// check if current method is interesting as declared in android.xml
		Set<String> superTypeNames = ASTNodeUtils.getSuperTypeQualifiedNames(declaringClass);
		String methodName = methodBinding.getName();

		UIActionClass interestingActionClass = null;

		// DEBUG
		// System.out.println("-------------");

		for (String superTypeName : superTypeNames) {
			String methodClassName = superTypeName + "#" + methodName;



			if (actionMetaData.containsKey(methodClassName)) {
				// DEBUG
				// System.out.println("Found " + methodClassName + " for " + declaringClass);
				interestingActionClass = actionMetaData.get(methodClassName);
				break;
			}
		}

		// has a UIActionClass		
		if (interestingActionClass != null) {
			/*  EXTERNAL_UI */
			if (declaringClass.isFromSource() == false
				&& interestingActionClass.category == 
						UIActionClass.UIActionCategory.OUTSOURCE) {

				action = new UIActionExternal();
				action.type = UIAction.ActionType.EXTERNAL_UI;

			} else if (declaringClass.isFromSource()
					&& interestingActionClass.category ==
						UIActionClass.UIActionCategory.INSOURCE) {
				/*  INTERNAL_UI */
				if (interestingActionClass.type == 
						UIActionClass.UIActionType.LINKED_EVENT){
					action = new UIActionLinkedEvent();
				}
				else 
					action = new UIActionInternal();

				((UIActionInternal)action).declaration = node;	
				action.type = UIAction.ActionType.INTERNAL_UI;
				
			} else {
				action = new UIAction();
			}

		} else {
			/* EXTERNAL_NON_UI */
			if (declaringClass.isFromSource() == false) {
				action = new UIActionExternal();
				action.type = UIAction.ActionType.EXTERNAL_NON_UI;
			}
			else {
			/* INTERNAL, not known yet */
				action = new UIActionInternal();

				((UIActionInternal)action).declaration = node;	

				action.type = UIAction.ActionType.INTERNAL_APP_DEFINED;
			}
		}

		action.containingType = declaringClass;
		action.metaClassInfo = interestingActionClass;
		action.methodBinding = methodBinding;

		return action;
	}
}

