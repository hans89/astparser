package vn.edu.hcmus.dhhai.android.graphextractor;

/* junit packages */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/* ANDOR tree */
import vn.edu.hcmus.dhhai.android.graphextractor.ANDORTree.*;

import java.util.*;


/**
 * ANDORTree TestSuite for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
@RunWith(JUnit4.class)
public class ANDORTreeTestSuite extends AbstractTestSuite {
	
	private Node<String> buildANDORTree1() {
		Node<String> v3 = new TerminalNode<String>("v3"),
					v6 = new TerminalNode<String>("v6"),
					v9 = new TerminalNode<String>("v9"),
					v10 = new TerminalNode<String>("v10"),
					v11 = new TerminalNode<String>("v11"),
					v12 = new TerminalNode<String>("v12"),
					v13 = new TerminalNode<String>("v13"),
					v14 = new TerminalNode<String>("v14"),
					v15 = new TerminalNode<String>("v15");

		// v5
		List<Node<String>> v5Children
			= new ArrayList<Node<String>>(2);
		v5Children.add(v9);
		v5Children.add(v10);
		Node<String> v5 = new ORNode<String>("v5", v5Children);

		// v2
		List<Node<String>> v2Children
			= new ArrayList<Node<String>>(2);
		v2Children.add(v5);
		v2Children.add(v6);
		Node<String> v2 = new ANDNode<String>("v2", v2Children);

		// v7
		List<Node<String>> v7Children
			= new ArrayList<Node<String>>(2);
		v7Children.add(v11);
		v7Children.add(v12);
		Node<String> v7 = new ORNode<String>("v7", v7Children);

		// v8
		List<Node<String>> v8Children
			= new ArrayList<Node<String>>(3);
		v8Children.add(v13);
		v8Children.add(v14);
		v8Children.add(v15);
		Node<String> v8 = new ORNode<String>("v8", v8Children);

		// v4
		List<Node<String>> v4Children
			= new ArrayList<Node<String>>(2);
		v4Children.add(v7);
		v4Children.add(v8);
		Node<String> v4 = new ANDNode<String>("v4", v4Children);

		// v1
		List<Node<String>> v1Children
			= new ArrayList<Node<String>>(3);
		v1Children.add(v2);
		v1Children.add(v3);
		v1Children.add(v4);
		Node<String> v1 = new ORNode<String>("v1", v1Children);

		// List<Node<String>> allNodes = new ArrayList<Node<String>>(15);

		// allNodes.add(v1);
		// allNodes.add(v2);
		// allNodes.add(v3);
		// allNodes.add(v4);
		// allNodes.add(v5);
		// allNodes.add(v6);
		// allNodes.add(v7);
		// allNodes.add(v8);
		// allNodes.add(v9);
		// allNodes.add(v10);
		// allNodes.add(v11);
		// allNodes.add(v12);
		// allNodes.add(v13);
		// allNodes.add(v14);
		// allNodes.add(v15);

		return v1;
	}


	private Node<String> buildANDORTree2() {
		Node<String> v3 = new TerminalNode<String>("v3"),
					v6 = new TerminalNode<String>("v6"),
					v7 = new TerminalNode<String>("v7"),
					v9 = new TerminalNode<String>("v9"),
					v10 = new TerminalNode<String>("v10"),
					v12 = new TerminalNode<String>("v12"),
					v13 = new TerminalNode<String>("v13"),
					v14 = new TerminalNode<String>("v14"),
					v15 = new TerminalNode<String>("v15");

		// v5
		List<Node<String>> v5Children
			= new ArrayList<Node<String>>(2);
		v5Children.add(v12);
		v5Children.add(v13);
		Node<String> v5 = new ANDNode<String>("v5", v5Children);

		// v2
		List<Node<String>> v2Children
			= new ArrayList<Node<String>>();
		v2Children.add(v5);
		v2Children.add(v6);
		v2Children.add(v7);
		Node<String> v2 = new ORNode<String>("v2", v2Children);

		// v11
		List<Node<String>> v11Children
			= new ArrayList<Node<String>>(2);
		v11Children.add(v14);
		v11Children.add(v15);
		Node<String> v11 = new ORNode<String>("v11", v11Children);

		// v8
		List<Node<String>> v8Children
			= new ArrayList<Node<String>>(3);
		v8Children.add(v10);
		v8Children.add(v11);
		Node<String> v8 = new ANDNode<String>("v8", v8Children);

		// v4
		List<Node<String>> v4Children
			= new ArrayList<Node<String>>(2);
		v4Children.add(v8);
		v4Children.add(v9);
		Node<String> v4 = new ORNode<String>("v4", v4Children);

		// v1
		List<Node<String>> v1Children
			= new ArrayList<Node<String>>(3);
		v1Children.add(v2);
		v1Children.add(v3);
		v1Children.add(v4);
		Node<String> v1 = new ANDNode<String>("v1", v1Children);

		return v1;
	}

	private Node<String> buildANDORTree3() {
		Node<String> v7 = new TerminalNode<String>("v7"),
					v8 = new TerminalNode<String>("v8"),
					v10 = new TerminalNode<String>("v10"),
					v11 = new TerminalNode<String>("v11"),
					v13 = new TerminalNode<String>("v13"),
					v15 = new TerminalNode<String>("v15"),
					v16 = new TerminalNode<String>("v16"),
					v17 = new TerminalNode<String>("v17"),
					v18 = new TerminalNode<String>("v18"),
					v19 = new TerminalNode<String>("v19"),
					v20 = new TerminalNode<String>("v20"),
					v21 = new TerminalNode<String>("v21");

		// v14
		List<Node<String>> v14Children
			= new ArrayList<Node<String>>(2);
		v14Children.add(v20);
		v14Children.add(v21);
		Node<String> v14 = new ANDNode<String>("v14", v14Children);

		// v6
		List<Node<String>> v6Children
			= new ArrayList<Node<String>>(3);
		v6Children.add(v13);
		v6Children.add(v14);
		v6Children.add(v15);
		Node<String> v6 = new ORNode<String>("v6", v6Children);

		// v2
		List<Node<String>> v2Children
			= new ArrayList<Node<String>>(2);
		v2Children.add(v6);
		v2Children.add(v7);
		Node<String> v2 = new ANDNode<String>("v2", v2Children);


		// v9
		List<Node<String>> v9Children
			= new ArrayList<Node<String>>(2);
		v9Children.add(v16);
		v9Children.add(v17);
		Node<String> v9 = new ORNode<String>("v9", v9Children);

		// v3
		List<Node<String>> v3Children
			= new ArrayList<Node<String>>(3);
		v3Children.add(v8);
		v3Children.add(v9);
		v3Children.add(v10);
		Node<String> v3 = new ANDNode<String>("v3", v3Children);

		// v12
		List<Node<String>> v12Children
			= new ArrayList<Node<String>>(2);
		v12Children.add(v18);
		v12Children.add(v19);
		Node<String> v12 = new ORNode<String>("v12", v12Children);

		// v4
		List<Node<String>> v4Children
			= new ArrayList<Node<String>>(2);
		v4Children.add(v11);
		v4Children.add(v12);
		Node<String> v4 = new ANDNode<String>("v4", v4Children);

		// v1
		List<Node<String>> v1Children
			= new ArrayList<Node<String>>(3);
		v1Children.add(v2);
		v1Children.add(v3);
		v1Children.add(v4);
		Node<String> v1 = new ORNode<String>("v1", v1Children);

		return v1;
	}

	public static <T> void printANDORTreeSolutions (Node<T> tree) {
		Set<Set<SwapOption<T>>> solutionSignatures 
				= ASG.runASG(tree);

		int i = 1;	

		// for (Set<SwapOption<T>> solSig : solutionSignatures) {

		// 	List<Node<T>> terminalNodes
		// 			= tree.computeSolutionTerminalNodes(solSig);

		// 	System.out.print("Solution " + Integer.toString(i++) + ": ");
			
		// 	for (SwapOption<T> swap : solSig)
		// 		System.out.print(swap + " ");
		// 	System.out.print(" -> ");
		// 	for (Node<T> node : terminalNodes)
		// 		System.out.print(node + " ");
		// 	System.out.println();
		// }

		Set<Set<Node<T>>> setSolNodes = new HashSet<Set<Node<T>>>();

		for (Set<SwapOption<T>> solSig : solutionSignatures) {

			List<TerminalNode<T>> terminalNodes
					= tree.computeSolutionTerminalNodes(solSig);

			Set<Node<T>> nodeSet = new HashSet<Node<T>>(terminalNodes);

			setSolNodes.add(nodeSet);
			
		}

		for (Set<Node<T>> solNodes : setSolNodes) {

			System.out.print("DupSolution " + Integer.toString(i++) + ": ");
			
			for (Node<T> node : solNodes)
				System.out.print(node + " ");
			System.out.println();
		}

		System.out.println();
	}

	@Test
	public void testANDORTree1() {
		ANDORTreeTestSuite.printANDORTreeSolutions(this.buildANDORTree1());
	}

	@Test
	public void testANDORTree2() {
		ANDORTreeTestSuite.printANDORTreeSolutions(this.buildANDORTree2());
	}

	@Test
	public void testANDORTree3() {
		ANDORTreeTestSuite.printANDORTreeSolutions(this.buildANDORTree3());
	}
}
