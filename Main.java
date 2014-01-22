package astparser;


import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.compiler.IProblem;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map.Entry;


public class Main {
	public static void main(String[] args) {

		HashMap<String,CompilationUnit> units = testWithJars();

		//ASTVisitor visitor = new Visitor();
		ASTVisitor visitor = new Visitor.MethodClassVisitor("Lorg/eclipse/jdt/core/dom/MethodInvocation;");

		
		for (Entry<String, CompilationUnit> e : units.entrySet()) {
			String k = e.getKey();
    		CompilationUnit u = e.getValue();
			System.out.println("--------------------");
			System.out.println(k);
			for (IProblem p : u.getProblems()) {
				System.out.println(p);	
			}
			System.out.println("--------------------");
			u.accept(visitor);
			System.out.println("--------------------");
		}
		
	}

	private static HashMap<String,CompilationUnit> testWithJars() {
		
		return Parser.parse(
				FileUtils.getJavaFiles("/Users/hans/Desktop/ast/astparser", 
					new String[] {".java", ".JAVA"}),
				new String[] { /* binary class paths */
					"/Users/hans/Desktop/ast/lib/org.eclipse.core.contenttype_3.4.200.v20130326-1255.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.core.jobs_3.5.300.v20130429-1813.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.core.resources_3.8.101.v20130717-0806.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.core.runtime_3.9.0.v20130326-1255.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.equinox.preferences_3.5.100.v20130422-1538.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.jdt.core_3.9.1.v20130905-0837.jar",
					"/Users/hans/Desktop/ast/lib/org.eclipse.osgi_3.9.1.v20130814-1242.jar"
				},
				new String[] { /* source dependent paths*/
					"/Users/hans/Desktop/ast/astparser/"
				});
	}

	private static HashMap<String,CompilationUnit> testWithoutJars() {
		
		return Parser.parse(
				new String[] {	/* source files */
					"/Users/hans/Desktop/ast/astparser/Main.java",
					"/Users/hans/Desktop/ast/astparser/Parser.java"
				},
				new String[] { /* binary class paths */
				},
				new String[] { /* source dependent paths*/
					"/Users/hans/Desktop/ast/astparser/"
				});
	}

	private static void testHelloBuilder() {
		String source = HelloBuilder.build();
		
		System.out.println(source);
		
		CompilationUnit astForSource = ASTParserTest.testString(source);		

		System.out.println(astForSource);
	}
}
