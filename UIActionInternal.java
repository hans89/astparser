package astparser.UIModel;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class UIActionInternal extends UIAction {
	
	/**
	 *	The AST node that defines the method. It is actually the source code
	 *	of the method. EXTERNAL methods don't have this property.
	 *
	 *	This property is Eclipse JDT dependent.
	 */
	public MethodDeclaration declaration;

	/**
	 *	Paths of calling
	 *	Each path is a series of calls created by the method, starting from bottom
	 *	with the last external method to be called to the method.
	 * 	For example, if the method calls a method A, A then calls B, B then calls
	 *	external method setOnClickListener, then the path will be
	 *	setOnClickListener -> B -> A
	 *	The path is explored using DFS, starting from the external method, visiting
	 *	through the calling method (using IMethodBinding fields declared in 
	 *	UIActionInvocation) until it reaches an implicitly called method, which
	 *	is of type INTERNAL_UI
	 */
	public List<LinkedHashSet<UIActionInvocation>> executingPaths;
}