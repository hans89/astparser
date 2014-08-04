package vn.edu.hcmus.dhhai.android.graphextrator.UIModel;

import java.util.*;
import vn.edu.hcmus.dhhai.android.graphextrator.*;
import org.eclipse.jdt.core.dom.*;

public class UIActionInvocationStartModal extends UIActionInvocation {
	public UIObject targetObject;

	public UIObject endTargetObject;

	public boolean endCurrentObject = false;
}