package vn.edu.hcmus.dhhai.android.graphextrator.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

/**
 * This class represent variables that are of event handler type
 * e.g. the variable of type View.OnClickListener
 */
public class UIEventObject {
	public Set<String> superTypeKeys;
	/* node that declares this variable */
	public VariableDeclaration declaration;

	/* nodes that refer to this variable */
	public List<Expression> references;

	/* the variable binding of this variable: 
		this is the unique key of the variable */
	public IVariableBinding variableBinding;

	/* the type binding of this variable */
	public ITypeBinding typeBinding;
}