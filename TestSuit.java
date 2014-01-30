package astparser;

/* astparser packages */
import org.eclipse.jdt.core.dom.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import astparser.visitor.*;
/* junit packages */
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
@RunWith(JUnit4.class)
public class TestSuit {

    @Test
    public void testDeclarationVisitor() {

		HashMap<String,CompilationUnit> units = Parser.parse(
				FileUtils.getFiles("test-android/notepad", 
					new String[] {".java", ".JAVA"}),
				new String[] {"lib/android/android-11.jar"},
				new String[] { /* path that contain the source files*/
					"test-android/notepad"
				});

		String[] androidClassKeys;

		try {
			androidClassKeys = 
			FileUtils.getAllLines("android-ui/class.key");
		} catch (java.io.IOException ex) {
			System.out.println(ex);
			return;
		}
			
		VariableDeclarationVisitor declVisitor = 
			new VariableDeclarationVisitor(androidClassKeys);

		
		for (Entry<String, CompilationUnit> e : units.entrySet()) {
    		CompilationUnit u = e.getValue();
			u.accept(declVisitor);
		}

		Set<VariableDeclarationVisitor.DeclarationInfo> lists
				 = declVisitor.getNonEmpty();
		List<String> declKeys = new ArrayList<String>(); 


		for (VariableDeclarationVisitor.DeclarationInfo vInfo : lists) {
			declKeys.addAll(vInfo.getAllVarKeys());
		}

		VariableReferenceVisitor refVisitor = new VariableReferenceVisitor(
						declKeys.toArray(new String[declKeys.size()]));


		for (Entry<String, CompilationUnit> e : units.entrySet()) {
    		CompilationUnit u = e.getValue();
			u.accept(refVisitor);
		}

		final HashMap<String, VariableReferenceVisitor.ReferenceInfo> refMap
					 = refVisitor.getReferencesMap();
		
		for (VariableDeclarationVisitor.DeclarationInfo vInfo : lists) {
			
			System.out.println("------------------------");
			System.out.println(vInfo.getTypeKey());

			class PrintHelper {
	            public void print(VariableDeclaration declNode, PrintStream stream) {
	            	String key = declNode.resolveBinding().getKey();
	            	VariableReferenceVisitor.ReferenceInfo refInfo;
	            	List<ASTNode> refNodes;
					stream.println(declNode + " " + key);
					if (refMap.containsKey(key)) {
						refInfo = refMap.get(key);
						refNodes = refInfo.getAll();
						for (ASTNode refNode : refNodes) {
							//if (!(refNode instanceof VariableDeclaration))
								stream.println("\t[]--- " + refNode);
							// stream.println("\t[]--- Node type " + 
							// 	refNode.getClass().getName() + " : " + refNode);
						}
					}    
	            }
	        };

	        PrintHelper helper = new PrintHelper();

			System.out.println("-------Fields-----------");
			for (VariableDeclaration node : vInfo.getFields()) {
				helper.print(node, System.out);
			}

			System.out.println("-------Locals-----------");
			for (VariableDeclaration node : vInfo.getLocalVars()) {
				helper.print(node, System.out);
			}

			System.out.println("------Arguments---------");
			for (VariableDeclaration node : vInfo.getFormalArgs()) {
				helper.print(node, System.out);
			}

		}
    }

    @Test
    @Ignore
    public void thisIsIgnored() {
    }

    @Test
    public void testNodeTypesRef() {
    	HashMap<String,CompilationUnit> units = Parser.parse(
				FileUtils.getFiles("test-android/ast", 
					new String[] {".java", ".JAVA"}),
				new String[]{"lib/android/android-11.jar"},
				new String[] { /* path that contain the source files*/
					"test-android/ast"
				});

    	String[] androidClassKeys;

		try {
			androidClassKeys = 
			FileUtils.getAllLines("android-ui/class.key");
		} catch (java.io.IOException ex) {
			System.out.println(ex);
			return;
		}
    }
}