package astparser;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class ASTNodeUtils {
	public static MethodDeclaration getContainingMethod(ASTNode node) {
		while (node != null 
				&& !(node instanceof MethodDeclaration)){
			node = node.getParent();
		}

		return (MethodDeclaration)node;
	}


	public static ASTNode getContainingType(ASTNode node) {
		while (node != null 
				&& !(node instanceof AbstractTypeDeclaration)
				&& !(node instanceof AnonymousClassDeclaration)){
			node = node.getParent();
		}

		return node;
	}

	public static String getMethodKey(MethodDeclaration method) {
		IMethodBinding methodBinding = null;
		if (method != null && (methodBinding = method.resolveBinding()) != null) {
			return methodBinding.getKey();
		}
		return null;
	}

	public static String getTypeKey(AbstractTypeDeclaration type) {
		ITypeBinding typeBinding = null;
		if (type != null && (typeBinding = type.resolveBinding()) != null) {
			return typeBinding.getKey();
		}
		return null;
	}

	public static String getTypeKey(AnonymousClassDeclaration type) {
		ITypeBinding typeBinding = null;
		if (type != null && (typeBinding = type.resolveBinding()) != null) {
			return typeBinding.getKey();
		}
		return null;	
	}


	public static Set<String> getSuperTypeQualifiedNames(ITypeBinding startClass) {
		Set<String> qualNames = null;
		if (startClass != null) {
			qualNames = new HashSet<String>();
			
			Queue<ITypeBinding> tbQueue = new LinkedList<ITypeBinding>();

			tbQueue.offer(startClass);

			ITypeBinding current;

			// a BFS to visit all super types
			// while the queue is not empty
			while ((current = tbQueue.poll()) != null) {
				String currentTypeName = current.getQualifiedName();
				if (!qualNames.contains(currentTypeName)) {
					// if we haven't seen this class yet

					qualNames.add(currentTypeName);

					// we find its direct super types
					tbQueue.addAll(Arrays.asList(current.getInterfaces()));

					ITypeBinding superClass = current.getSuperclass();
					if (superClass != null)
						tbQueue.offer(superClass);
				}
				// or else we have seen this type before,
				// so its super types have been listed,
				// we don't need to do anything
			}
		}

		return qualNames;
	}

	public static List<String> getSuperClassQualifiedNames(ITypeBinding startClass) {
		List<String> qualNames = null;
		if (startClass != null) {
			qualNames = new ArrayList<String>();

			ITypeBinding current = startClass;

			// a BFS to visit all super types
			// while the queue is not empty
			while (current != null) {
				qualNames.add(current.getQualifiedName());
					
				current = current.getSuperclass();
			}
		}

		return qualNames;
	}


	public static List<ASTNode> getAllTypeNodes(ASTNode[] units) {
		final List<ASTNode> nodes = new ArrayList<ASTNode>();

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				nodes.add(node);
				return true;
			}

			@Override 
			public boolean visit(TypeDeclaration node) {
				nodes.add(node);
				return true;
			}
		};

		for (ASTNode u : units) {
			u.accept(visitor);
		}

		return nodes;
	}

	public static String getMethodName(String classMethodName) {
		return classMethodName.substring(classMethodName.indexOf('#') + 1);
	}

	public static String getClassName(String classMethodName) {
		return classMethodName.substring(0, classMethodName.indexOf('#'));
	}

	public static boolean matchMethodByName(IMethodBinding tBinding, String classMethodName) {
		String mName = ASTNodeUtils.getMethodName(classMethodName);
		String cName = ASTNodeUtils.getClassName(classMethodName);

		// match name first
		if (!mName.equals(tBinding.getName()))
			return false;
		// then match super types;
		ITypeBinding declaringClass = tBinding.getDeclaringClass();

		Set<String> superTypeNames
			= ASTNodeUtils.getSuperTypeQualifiedNames(declaringClass);

		if (superTypeNames == null)
			return false;

		
		return superTypeNames.contains(cName);
	}


	public static HashMap<String,List<MethodDeclaration>>
		matchAndroidUIMethodImpls(
			List<String> classMethodName,
			ASTNode[] units) {

		final HashMap<String, List<String>> 
			mapMethodName = new HashMap<String, List<String>>();

		for (String cMName : classMethodName) {
			String mName = ASTNodeUtils.getMethodName(cMName);
			String cName = ASTNodeUtils.getClassName(cMName);

			if (!mapMethodName.containsKey(mName)) {
				mapMethodName.put(mName, 
							new ArrayList<String>());
			}

			mapMethodName.get(mName).add(cName);
		}

		final HashMap<String, List<MethodDeclaration>>
			methodNameBindings = new HashMap<String, List<MethodDeclaration>>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				String methodName = node.getName().toString();
				IMethodBinding methodBinding = node.resolveBinding();				

				if (methodBinding != null 
					&& mapMethodName.containsKey(methodName)) {

					ITypeBinding declaringClass = methodBinding.getDeclaringClass();

					Set<String> superTypeNames
						= ASTNodeUtils.getSuperTypeQualifiedNames(declaringClass);

					if (superTypeNames == null)
						return true;

					for (String cName : mapMethodName.get(methodName)) {
						if (superTypeNames.contains(cName)) {
							
							if (!methodNameBindings.containsKey(methodName)) {
								methodNameBindings.put(methodName, 
											new ArrayList<MethodDeclaration>());
							}

							methodNameBindings.get(methodName).add(node);
							break;
						}
					}
				}

				return true;
			}	
		};


		for (ASTNode u : units) {
			u.accept(visitor);
		}

		return methodNameBindings;

	}


	
}
