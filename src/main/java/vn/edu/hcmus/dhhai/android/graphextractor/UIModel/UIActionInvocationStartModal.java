package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;
import vn.edu.hcmus.dhhai.android.graphextractor.*;
import org.eclipse.jdt.core.dom.*;

public class UIActionInvocationStartModal extends UIActionInvocation {
	public UIObject targetObject;

	public UIObject endTargetObject;

	public boolean endCurrentObject = false;
}