package astparser;

/* astparser packages */
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.compiler.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import org.eclipse.jface.text.*;
import org.eclipse.text.edits.*;

import astparser.UIModel.*;


/* junit packages */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Tests for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
@RunWith(JUnit4.class)
public class TestSuit {

	public static final String LONG_DASH = "------------------------------------";

	public static final String SHORT_DASH = "---------";

	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName()
	      	 + " .............");
	   }
	};

	
	public void tryProject(String projectFullPath, String[] libs, String graphOutput) {

		// 1. get meta info: Android structures
		AndroidUIClassReader structureReader = new AndroidUIClassReader();

		structureReader.parseXML(
					new String[] {
						"android-ui/android.xml"
					});

		// 1a. interesting Android UI Object Structures
		final HashMap<String, UIObjectClass>
			androidUIStructures = structureReader.getUIStructures();
		// 1b. interesting Android UI Actions
		final HashMap<String, UIActionClass>
			androidUIActions = structureReader.getUIActions();		


		// 2. parse project
		HashMap<String,CompilationUnit> units = parseProject(projectFullPath, libs);


		// 3. find all actions, set up their types and invocations
		MethodVisitor methodVisitor 
					= new MethodVisitor(new UIActionBuilder(androidUIActions));

		for (CompilationUnit u : units.values()) {
			u.accept(methodVisitor);
		}

		HashMap<IMethodBinding, UIAction> 
				allActions = methodVisitor.getAllActions();


		// 4. link event setters with corresponding events
		ASTNodeUtils.bindEventSetters(allActions);	


		for (UIAction action : allActions.values()) {
			// find action that binds events
			// redundant check
			if (action.type == UIAction.ActionType.EXTERNAL_UI
				&& action.metaClassInfo != null
				&& action.metaClassInfo.type == 
										UIActionClass.UIActionType.BIND_EVENT) {

				for (UIActionInvocation act : action.invokedList) {
					if (act instanceof UIActionInvocationBindEvent) {
						UIActionInvocationBindEvent bindEAct 
							= (UIActionInvocationBindEvent)act;

						if (bindEAct.bindedEvents == null) {
							System.out.println("null binded " + act.astSourceNode);
						}
					}
				}
			}
		}
									

		// 5. find all ui external actions, and trace their way up to the
		// INTERNAL_UI methods. This completes all the UIAction info.
		ASTNodeUtils.traceExternalUIPaths(allActions);

		// 6. find all UIObjects, attach their UIObjectClass and their init
		//	and top-event actions
		HashMap<ITypeBinding, UIObject> allUIObjects = 
			ASTNodeUtils.findAllUIObjects(androidUIStructures, allActions);


		// for (UIAction act : allActions.values()) {
		// 	if (act instanceof UIActionInternal) {
		// 		UIActionInternal actInt = (UIActionInternal)act;
		// 		if (actInt.executingPaths != null) {
		// 			System.out.println(actInt.methodBinding.getKey());
		// 			for (Set<UIActionInvocation> path : actInt.executingPaths) {
		// 				System.out.print("\t ");
		// 				for (UIActionInvocation actInv : path) {
		// 					System.out.print(actInv.astSourceNode.getExpression()
		// 						+ "." + actInv.astSourceNode.getName() + " -> ");
		// 				}

		// 				System.out.println(".");
		// 			}	
		// 		}
		// 	}
		// }

		// System.out.println(TestSuit.LONG_DASH);

		// for (UIObject obj : allUIObjects.values()) {
		// 	System.out.println(obj.typeBinding.getQualifiedName());
		// 	if (obj.initActions != null)
		// 	for (UIAction act : obj.initActions.values()) {
		// 		if (act instanceof UIActionInternal) {
		// 			UIActionInternal actInt = (UIActionInternal)act;
		// 			if (actInt.executingPaths != null) {
		// 				System.out.println(actInt.methodBinding.getKey());
		// 				for (Set<UIActionInvocation> path : actInt.executingPaths) {
		// 					System.out.print("\t ");
		// 					for (UIActionInvocation actInv : path) {
		// 						System.out.print(actInv.astSourceNode.getExpression()
		// 							+ "." + actInv.astSourceNode.getName() + " -> ");
		// 					}

		// 					System.out.println(".");
		// 				}	
		// 			}
		// 	}
		// 	}

		// 	if (obj.topEventActions != null)
		// 	for (UIAction event : obj.topEventActions.values()) {
		// 		System.out.println("\t -> " + event.methodBinding.getKey());
		// 	}
		// }

		// 4b. link start modals with their target
		ASTNodeUtils.bindStartModals(allActions, allUIObjects);

		// 4c. link enable widgets with the event they affect
		ASTNodeUtils.bindEnableWidgetWithEvents(allActions, allUIObjects);

		// 7. now we are ready to build the LTS
		/*
			Each state of the LTS is identified by the possible events at that 
			state.

			In the initial states, possible events includes top-events and linked
			events that are set up by the initializers

			All the process follows from finding the effects of each event handler
			or initializer
		*/
		LTS<Set<UIAction>, UIAction> lts = new LTS<Set<UIAction>, UIAction>();
		HashMap<Set<UIAction>, String> stateIDs = new HashMap<Set<UIAction>, String>();
		HashMap<UIAction, String> transIDs = new HashMap<UIAction, String>();

		class IntegerIDGenerator {
			private Integer counter;

			public IntegerIDGenerator() {
				counter = 0;
			}

			public Integer next() {
				return counter++;
			}
		}

		class StateInfo {
			private String stateName;
			private  int stateOrder;

			public StateInfo(String s, int i) {
				stateName = s;
				stateOrder = i;
			}

			public String getName() {
				return stateName + Integer.toString(stateOrder);
			}
		}


		IntegerIDGenerator idSGen = new IntegerIDGenerator();
		//IntegerIDGenerator idTGen = new IntegerIDGenerator();

		for (UIObject obj : allUIObjects.values()) {
			
			// set up initial state
			Set<UIAction> initialState = obj.getAllInitialEvents();
			
			lts.actions.addAll(initialState);


			lts.states.add(initialState);
			// id for a state
			stateIDs.put(initialState, obj.getName());


			lts.initialStates.add(initialState);

			

			// System.out.println(obj.typeBinding.getQualifiedName());
			// for (UIAction event : initialState) {
			// 	System.out.println("\t -> " + event.methodBinding.getKey());
			// }
		}

		/**
		 * After obtaining the initially available events, we can start a DFS/BFS
		 *	to visit and track down all of the possible states of a single window
		 *	Then we can try to link state between windows
		 * 	But first, we have to make clear the semantics of the other action
		 *	types, their required information, and their final effects
		 *	BIND_EVENT
		 *	START_MODAL
		 *	END_MODAL
		 *	OPEN_MENU
		 *	ENABLE_WIDGET
		 *	
		 *	We also have to add the effect of branching: possible and/or state
		 */

		for (Set<UIAction> initialState : lts.initialStates) {
			Deque<Set<UIAction>> stateStack = new ArrayDeque<Set<UIAction>>();

			stateStack.addFirst(initialState);

			Set<UIAction> currentState;
			Set<UIAction> nextState;

			while ((currentState = stateStack.peekFirst()) != null) {
				stateStack.removeFirst();

			/*
			For going to the next state, we shall check each event allowed in
			the current state:
				- if the event change the current possible event set, then it
					create a transition from the current state to another state
				- if the event does not change the possible event set, then it
					create a transition from and to the current state itself
			*/
				for (UIAction actInv : currentState) {

					// identify effect of act
					// get all possible effects
					if (actInv instanceof UIActionInternal) {
						
						UIActionInternal act = (UIActionInternal)actInv;

						boolean terminal = false;
						// if it is a terminal state
						if (act.getStartEndModals().size() > 0) {

							
							nextState = new HashSet<UIAction>();

							nextState.add(new UIAction());

							lts.terminalStates.add(nextState);
							terminal = true;
						} else {
							nextState = new HashSet<UIAction>(currentState);

							Set<UIAction> enabledEvents = 
								act.getEnabledEvents();

							Set<UIAction> disabledEvents = 
								act.getDisabledEvents();

							nextState.addAll(enabledEvents);
							nextState.removeAll(disabledEvents);
						}

						if (!lts.states.contains(nextState)) {
							// if this is a new state, add it to the set
							lts.states.add(nextState);
							stateIDs.put(nextState, "s" + Integer.toString(idSGen.next()));

							if (terminal == false) {
								// and also add it to the stack for transition building
								// if it is not a terminal state
								stateStack.addFirst(nextState);	
							}
						}

						// add the transition
						LTS.Transition<Set<UIAction>, UIAction> newTransition
							= new LTS.Transition<Set<UIAction>, UIAction>();

						newTransition.fromState = currentState;
						newTransition.labelledAction = act;
						newTransition.toState = nextState;
						lts.transitions.add(newTransition);

						transIDs.put(act, act.getName());
					}
				}
			}
		}

		// // Print the LTS
		// for (Set<UIAction> state : lts.states) {
		// 	if (lts.initialStates.contains(state))
		// 		System.out.println("Init: ");
		// 	else if (lts.terminalStates.contains(state))
		// 		System.out.println("Term: ");
		// 	else 
		// 		System.out.println("Norm: ");

		// 	for (UIAction event : state) {
		// 		System.out.println("\t ->" + event.methodBinding.getKey());
		// 	}
		// }

		// for (LTS.Transition<Set<UIAction>, UIAction> trans : lts.transitions) {
		// 	for (UIAction event : trans.fromState) {
		// 		System.out.println("\t ->" + event.methodBinding.getKey());
		// 	}

		// 	System.out.println("====" + trans.labelledAction.methodBinding.getKey() 
		// 		+ "===>");

		// 	for (UIAction event : trans.toState) {
		// 		if (event.methodBinding != null)
		// 			System.out.println("\t ->" + event.methodBinding.getKey());
		// 		else 	
		// 			System.out.println("\t ->" + event.methodBinding);
		// 	}

		// 	System.out.println(TestSuit.LONG_DASH);
		// }



		try {
			File file = new File(graphOutput);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("digraph {");
			bw.newLine();

			bw.write("fontname=\"Helvetica\";");
			bw.newLine();
			bw.write("node[style=filled, fontname=\"Helvetica\", colorscheme=greens3, color=1];");
			bw.newLine();
    
			for (Set<UIAction> state : lts.states) {
				
				bw.write(stateIDs.get(state));

				for (UIAction event : state) {
					if (event.methodBinding == null) {
						// terminal
						bw.write("[peripheries=2]");
						break;
					}
				}

				bw.write(";");
				bw.newLine();
			}

			for (LTS.Transition<Set<UIAction>, UIAction> trans : lts.transitions) {

				bw.write(stateIDs.get(trans.fromState)
						+ " -> " + stateIDs.get(trans.toState));

				bw.write("[label=\"" + transIDs.get(trans.labelledAction)
							+ "\"");

				boolean terminal = false;

				for (UIAction act : trans.toState)
					if (act.methodBinding == null) {
						terminal = true;
						break;
					}

				if (terminal == true)
					bw.write(",style=dotted");

				bw.write("]");



				bw.write(";");
				bw.newLine();
			} 


			// bw.write("subgraph cluster_key {");
			// bw.newLine();

			// bw.write("ds" + "[shape=plaintext, style=solid," 
			// 				+ "label=\"");
			

			// for (Set<UIAction> state : lts.states) {
				
			// 	String sID = stateIDs.get(state);

			// 	for (UIAction event : state) {
			// 		if (event.methodBinding == null) {
			// 			// terminal
			// 			bw.write(
			// 				sID + " : "
			// 				+ "null"
			// 				+ "\\n");
			// 			break;
			// 		}
			// 		else {
			// 			if (transIDs.get(event) != null)
			// 			bw.write(
			// 				sID + " : "
			// 				+ transIDs.get(event)  + " : "
			// 				+ event.methodBinding.getKey()
			// 				+ "\\n");
			// 			else {

			// 				// bw.write(
			// 				// "s" + sID + " : "
			// 				// + "tNullTrans : "
			// 				// + event.methodBinding.getKey()
			// 				// + "\\n");

			// 				System.out.println("null trans " + event.methodBinding + " in " + event.containingType.getKey() + " isInitialState: " + lts.states.contains(state));

							
			// 			}
							
			// 		}
			// 	}
			// }


			// bw.write("\"" + "];");

			// bw.write("}");
			// bw.newLine();

			bw.write("}");

			bw.close();
 
			System.out.println("Done writing file.");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testProject() {
		String projectList = "/Users/hans/Desktop/android/app-projects.txt";
		String outPath = "/Users/hans/Desktop/ast/astparser/graphviz";

		String[] projectPaths = FileUtils.getAllLines(projectList);

		if (projectPaths == null)
			return; 

		String[] libs = new String[]{
			"lib/android/android-18.jar",
			"android-support-v4.jar",
			"android-support-v7-appcompat.jar",
			"android-support-v7-gridlayout.jar",
			"android-support-v7-mediarouter.jar",
			"android-support-v13.jar"
		};

		// String[] classes = new String[] {
		// 	"android.app.Activity",
		// 	"android.app.Dialog",
		// 	"android.app.Fragment",
		// 	"android.view.View"
		// };

		// Set<String> interestingClasses = new HashSet<String>(Arrays.asList(classes));

		for (String projectPath : projectPaths) {
			
			// getProjectOverview(projectPath, interestingClasses, libs);
			System.out.println("Project: " + projectPath);
			tryProject(projectPath, libs, 
				outPath + "/" + new File(projectPath).getName() + ".gv");			
			System.out.println(TestSuit.LONG_DASH);
		}


	}

	public void getProjectOverview(String folderFullPath, 
									Set<String> interestingClasses,
									String[] androidLibs) {

		HashMap<String,CompilationUnit> units;
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for (String s: interestingClasses) {
			counts.put(s,0);
		}

		System.out.println("Project: " + folderFullPath);

		units = parseProject(folderFullPath, androidLibs);
		Integer countAll = 0;
		for (Entry<String, CompilationUnit> e : units.entrySet()) {
			CompilationUnit u = e.getValue();
			List<AbstractTypeDeclaration> types = u.types();
			ITypeBinding tBind;
			String typeName;

			for (AbstractTypeDeclaration t : types) {
				tBind = t.resolveBinding();
				if ((typeName = ASTNodeUtils.matchSuperClass(tBind, interestingClasses)) != null) {
					System.out.println("\t class: " + tBind.getQualifiedName()
						+ " > " + typeName);

					counts.put(typeName, counts.get(typeName)+1);
				}
				countAll++;
			}
		}

		System.out.println("Numbers: ");
		for (String s: counts.keySet()) {
			System.out.println("\t" + s + " : " + counts.get(s));
		}
		System.out.println("\tAll : " + countAll);

		units.clear();
		units = null;

		System.out.println(TestSuit.LONG_DASH);
	}

	

	private HashMap<String,CompilationUnit> parseProject(String path,
														String[] androidLibs) {

		return 	Parser.parse(
				FileUtils.getFilePaths(path),
				androidLibs,
				/* path that contain the source files*/
				FileUtils.getFolderPaths(path)
			);
	}
}