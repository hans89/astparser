package vn.edu.hcmus.dhhai.android.graphextractor.UIModel;

import java.util.*;
import vn.edu.hcmus.dhhai.android.graphextractor.*;
import org.eclipse.jdt.core.dom.*;

public class UIActionInvocationEnableWidget extends UIActionInvocation {
	public Set<UIAction> enabledEvents;
	public Set<UIAction> disabledEvents;
}