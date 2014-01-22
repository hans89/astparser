package astparser;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
public class ASTParserTest {
	

	public static void parse(String str) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
		cu.accept(new ASTVisitor() {
 
			Set names = new HashSet();
 
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '" + name + "' at line"
						+ cu.getLineNumber(name.getStartPosition()));

				IVariableBinding varBinding = node.resolveBinding();
				if (varBinding != null) {
					System.out.println(varBinding.getType());
				} else {
					System.out.println("no binding");
				}
				return false; // do not continue 
			}
 
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
					System.out.println("Usage of '" + node + "' at line "
							+ cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
		});
 
	}

	public static CompilationUnit testString(String source) {
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		
		parser.setSource(source.toCharArray());
	
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);
	
		CompilationUnit result = (CompilationUnit) parser.createAST(null);
		
		return result;
	}
	
	public static CompilationUnit parse (ICompilationUnit unit) {
		// use Java Language Spec 4th (java 6-7)
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		
		// tell the parser to parse a ICompilationUnit
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource(unit);
		parser.setResolveBindings(true);
		
		return (CompilationUnit)parser.createAST(null /* IProgressMonitor */);
	}
	
	public static ArrayList<ICompilationUnit> findICompilationUnit(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		IProject project = root.getProject(projectName);
		IJavaProject javaProject = JavaCore.create(project);
		
		IPackageFragmentRoot[] pkgFragRoots;
		ArrayList<ICompilationUnit> units = new ArrayList<ICompilationUnit>();
		
		try {
			pkgFragRoots = javaProject.getAllPackageFragmentRoots();
			
			for (IPackageFragmentRoot pkgFragRoot : pkgFragRoots) {
				// get default package
				IPackageFragment frag = pkgFragRoot.getPackageFragment("");
				if (frag != null) {
					List<ICompilationUnit> newList = Arrays.asList(frag.getCompilationUnits());
					units.addAll(newList);
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return units;
	}
	

}
