package vn.edu.hcmus.dhhai.android.graphextrator.UIModel;

import java.util.*;
import vn.edu.hcmus.dhhai.android.graphextrator.*;
import org.eclipse.jdt.core.dom.*;

public class UIActionInvocationEnableWidget extends UIActionInvocation {
	public Set<UIAction> enabledEvents;
	public Set<UIAction> disabledEvents;
}