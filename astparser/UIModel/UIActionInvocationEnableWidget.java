package astparser.UIModel;

import java.util.*;
import astparser.*;
import org.eclipse.jdt.core.dom.*;

public class UIActionInvocationEnableWidget extends UIActionInvocation {
	public Set<UIAction> enabledEvents;
	public Set<UIAction> disabledEvents;
}