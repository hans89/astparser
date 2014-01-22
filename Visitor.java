package astparser;

import org.eclipse.jdt.core.dom.*;

public class Visitor extends ASTVisitor {

	@Override
	public boolean visit(MethodInvocation node) {
		System.out.print(node.getExpression());
		System.out.println("." + node.getName());
		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			ITypeBinding type = binding.getDeclaringClass();
			if (type != null) {
				System.out.println("-- from class " + type.getQualifiedName());
			}
			
		} else {
			System.out.println("-- null");
		}
		return true;
	}


	public static class MethodClassVisitor extends ASTVisitor {
		private String key;
		public MethodClassVisitor(String classKey) {
			key = classKey;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			IMethodBinding binding = node.resolveMethodBinding();
			if (binding != null) {
				ITypeBinding type = binding.getDeclaringClass();
				//System.out.println(type.getKey() + " " + key);
				if (type != null && type.getKey().equals(key)) {
					System.out.println("invoke method " + node.getName() 
						+ " of class " + type.getQualifiedName());
				}
			}
			return true;
		}
	}
}