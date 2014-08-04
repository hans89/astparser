
package vn.edu.hcmus.dhhai.android.graphextrator;

/*
	Here we use ASTParser#createASTs to parse source code files outside Eclipse

	public void createASTs(String[] sourceFilePaths,
                       String[] encodings,
                       String[] bindingKeys,
                       FileASTRequestor requestor,
                       IProgressMonitor monitor)
	
	Parameters:
		sourceFilePaths - the compilation units to create ASTs for
		encodings - the given encoding for the source units
		bindingKeys - the binding keys to create bindings for
		requestor - the AST requestor that collects abstract syntax trees
			 and bindings
		monitor - the progress monitor used to report progress and
			request cancellation, or null if none

	BEFORE calling createASTs, since we are parsing outside the Eclipse 
	environment, without Eclipse project information (as IJavaProject in the
	JavaCore model), we have to set up the environment. This includes setting
	the class paths, source paths, using the ASTParser#setEnvironment

	public void setEnvironment(String[] classpathEntries,
                           String[] sourcepathEntries,
                           String[] encodings,
                           boolean includeRunningVMBootclasspath)

    Parameters:
		classpathEntries - the given classpath entries to be used to
			resolve bindings
		sourcepathEntries - the given sourcepath entries to be used to
			resolve bindings
		encodings - the encodings of the corresponding sourcepath entries
			or null if the platform encoding can be used.
		includeRunningVMBootclasspath - true if the bootclasspath of
			the running VM must be prepended to the given classpath and
			false if the bootclasspath of the running VM should be ignored.
 */

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.JavaCore;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser is a quick wrap of Eclipse JDT's ASTParser. It parses a collection
 * of source code files, provided together with any dependent libraries.
 * It uses the batch parsing operation ASTParser#createASTs:
 *
 *	public void createASTs(String[] sourceFilePaths,
 *                     String[] encodings,
 *                     String[] bindingKeys,
 *                     FileASTRequestor requestor,
 *                     IProgressMonitor monitor)
 *
 * Environment configurations must be set while running outside Eclipse
 * Java project model.
 * Bindings are fetched by defaults.
 * Optionally, we can also choose to set ASTParser#setCompilerOptions.
 */
public class Parser {

	public static HashMap<String,CompilationUnit> parse(String[] sourceFilePaths,
											String[] classpathEntries,
											String[] sourcepathEntries) {

		ASTParser astParser = ASTParser.newParser(AST.JLS4);
		
		final HashMap<String,CompilationUnit> compilationUnits
					 = new HashMap<String,CompilationUnit>();	

		// set up libraries (.jar, .class or .java)
		astParser.setEnvironment(classpathEntries,
								 sourcepathEntries,
								 null, /*  use default encoding */
								 true /* use VM class path */
								);

		astParser.setResolveBindings(true);

		// with Bingding Recovery on, the compiler can detect 
		// binding among the set of compilation units
		astParser.setBindingsRecovery(true);

		// set default options, especially for Java 1.5
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		astParser.setCompilerOptions(options);
		

		FileASTRequestor requestor = new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit ast) {
				compilationUnits.put(sourceFilePath, ast);
			}

			@Override
			public void acceptBinding(String bindingKey, IBinding binding) {
				// do nothing
				// System.out.println("Accept Binding:... " + bindingKey);
				// System.out.println(binding);
			}
		};

		astParser.createASTs(sourceFilePaths,
							 null, 			/*  use default encoding */
							 new String[]{}, /* no binding key */
							 requestor,		
							 null			/* no IProgressMonitor */
							);

		return compilationUnits;
	}
}




