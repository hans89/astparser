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

		for (Entry<String,CompilationUnit> entry : units.entrySet()) {
			// DEBUG
			// System.out.println(entry.getKey());
			CompilationUnit u = entry.getValue();

			// for (IProblem prob : u.getProblems())
			// 	System.out.println(prob);
			u.accept(methodVisitor);
		}

		
		HashMap<IMethodBinding, UIAction> 
				allActions = methodVisitor.getAllActions();

		// DEBUG print all actions
		
		// for (UIAction action : allActions.values()) {
		// 	// find action that binds events
		// 	System.out.println("METHOD " + action.getName());

		// 	if (action.metaClassInfo == null) {
		// 		System.out.println("META: none");
		// 	} else {
		// 		System.out.println("META: " + action.metaClassInfo.getKey());
		// 	}

		// 	if (action.invokedList == null) {
		// 		System.out.println("INVOKE: none");
		// 	}
		// }
		// --END DEBUG

		// 3a. find the event objects (as variables)
		Set<String> eventClassKeys = new HashSet<String>();

		for (UIActionClass uAct : androidUIActions.values()) {
			if (uAct instanceof UIActionLinkedEventClass) {
				eventClassKeys.add(uAct.classKey);
			}
		}

		EventObjectVisitor eventObjectVisitor 
							= new EventObjectVisitor(eventClassKeys);

		for (CompilationUnit u : units.values()) {
			u.accept(eventObjectVisitor);
		}

		HashMap<IVariableBinding, UIEventObject> 
				allEventObjects = eventObjectVisitor.getAllEventObjects();

		// 4. link event setters with corresponding events
		ASTNodeUtils.bindEventSetters(allActions, allEventObjects);	


		// DEBUG: check null binded events

		// for (UIAction action : allActions.values()) {
		// 	// find action that binds events
		// 	// redundant check
		// 	if (action.type == UIAction.ActionType.EXTERNAL_UI
		// 		&& action.metaClassInfo != null
		// 		&& action.metaClassInfo.type == 
		// 								UIActionClass.UIActionType.BIND_EVENT) {

		// 		for (UIActionInvocation act : action.invokedList) {
		// 			if (act instanceof UIActionInvocationBindEvent) {
		// 				UIActionInvocationBindEvent bindEAct 
		// 					= (UIActionInvocationBindEvent)act;

		// 				if (bindEAct.bindedEvents == null) {
		// 					System.out.println("null binded " + act.astSourceNode);
		// 				}
		// 			}
		// 		}
		// 	}
		// }
		// -END DEBUG							

		// 5. find all ui external actions, and trace their way up to the
		// INTERNAL_UI methods. This completes all the UIAction info.
		ASTNodeUtils.traceExternalUIPaths(allActions);

		// DEBUG - all the paths

		// for (UIAction action : allActions.values()) {
		// 	if (action instanceof UIActionInternal) {
		// 		UIActionInternal internalAct 
		// 								= (UIActionInternal)action;

		// 		if (action.type == UIAction.ActionType.INTERNAL_UI)
		// 			System.out.println("INTERNAL_UI: " + internalAct.declaration);
		// 		else if (action.type == UIAction.ActionType.INTERNAL_APP_DEFINED)
		// 			System.out.println("INTERNAL_APP_DEFINED: " + internalAct.declaration);

		// 		if (internalAct.executingPaths != null) {
		// 			System.out.println("CHAINS:");
		// 			for (LinkedHashSet<UIActionInvocation> path : internalAct.executingPaths) {
		// 				for (UIActionInvocation actInv : path) {
		// 					System.out.print(actInv.astSourceNode + " <- ");
		// 				}
		// 				System.out.println(".");
		// 			}
		// 		} else {
		// 			System.out.println("CHAINS: null");
		// 		}
		// 		System.out.println(TestSuit.LONG_DASH);
		// 	}
		// }

		// END DEBUG

		// 6. find all UIObjects, attach their UIObjectClass and their init
		//	and top-event actions
		HashMap<ITypeBinding, UIObject> allUIObjects = 
			ASTNodeUtils.findAllUIObjects(androidUIStructures, allActions);

		// DEBUG
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
		// DEBUG
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
		// END DEBUG

		// 7. link enable widgets with the event they affect
		ASTNodeUtils.bindEnableWidgetWithEvents(allActions, allUIObjects);

		// 8. link start modals with their target
		IntentVisitor intentVisitor = new IntentVisitor();

		for (CompilationUnit u : units.values()) {
			u.accept(intentVisitor);
		}

		HashMap<IVariableBinding, IntentVisitor.IntentInfo>
			allIntents = intentVisitor.getAllIntents();


		ASTNodeUtils.bindStartModals(allActions, allUIObjects, allIntents);

		// DEBUG
		// for (UIObject obj : allUIObjects.values()) {
		// 	System.out.println(TestSuit.LONG_DASH);
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
		// 		}
		// 	}
		// }
		

		// for (UIObject obj : allUIObjects.values()) {
		// 	System.out.println(TestSuit.LONG_DASH);
		// 	System.out.println(obj.typeBinding.getQualifiedName());
		// 	if (obj.initActions != null)
		// 	for (UIAction act : obj.getAllInitialEvents()) {
		// 		if (act instanceof UIActionInternal) {
		// 			UIActionInternal actInt = (UIActionInternal)act;
		// 			if (actInt.executingPaths != null) {
		// 				System.out.println(actInt.methodBinding.getKey());
		// 				for (Set<UIActionInvocation> path : actInt.executingPaths) {
		// 					System.out.print("\t ");
		// 					for (UIActionInvocation actInv : path) {
		// 						if (actInv instanceof UIActionInvocationStartModal) {
		// 							UIActionInvocationStartModal actInvStart
		// 								= (UIActionInvocationStartModal)actInv;

		// 							if (actInvStart.targetObject != null)
		// 								System.out.print("Target: " + 
		// 								actInvStart.targetObject.typeBinding.getKey() + " | "); 
									
		// 							if (actInvStart.endTargetObject != null)
		// 								System.out.print("EndTarget: " + 
		// 								actInvStart.endTargetObject.typeBinding.getKey() + " | "); 

		// 						}
									
								
		// 						System.out.print(actInv.astSourceNode.getExpression()
		// 							+ "." + actInv.astSourceNode.getName() + " <- ");


		// 					}

		// 					System.out.println(".");
		// 				}	
		// 			}
		// 		}
		// 	}
		// }
		// END DEBUG

		// 9. now we are ready to build the LTS
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

		IntegerIDGenerator idSGen = new IntegerIDGenerator();
		//IntegerIDGenerator idTGen = new IntegerIDGenerator();

		// set up initial LTS, where each node represents the initial state of 
		// each ui object
		for (UIObject obj : allUIObjects.values()) {
			
			// set up initial state
			Set<UIAction> initialState = obj.getAllInitialEvents();
			
			lts.actions.addAll(initialState);


			lts.states.add(initialState);
			// id for a state
			stateIDs.put(initialState, obj.getName());


			lts.initialStates.add(initialState);
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

		// we chose to make a DFS here

		for (Set<UIAction> initialState : lts.initialStates) {
			Deque<Set<UIAction>> stateStack = new ArrayDeque<Set<UIAction>>();

			stateStack.addFirst(initialState);

			Set<UIAction> currentState;

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

						Set<UIActionInvocation> startModals = 
							act.getStartEndModals();

						if (startModals.size() == 0) {
							Set<UIAction> nextState = new HashSet<UIAction>(currentState);

							Set<UIAction> enabledEvents = 
								act.getEnabledEvents();

							Set<UIAction> disabledEvents = 
								act.getDisabledEvents();

							nextState.addAll(enabledEvents);
							nextState.removeAll(disabledEvents);

							// add the transition
							lts.addTransition(currentState, act, nextState);
							transIDs.put(act, act.getName());	

							if (!lts.states.contains(nextState)) {
							// if this is a new state, add it to the set
								lts.states.add(nextState);
								stateIDs.put(nextState, "s" + Integer.toString(idSGen.next()));

							// add it to the stack for transition building
								stateStack.addFirst(nextState);	
							}
						}
					}
				}
			}
		}

		Set<Set<UIAction>> newStates = new HashSet<Set<UIAction>>();

		// {
		// 	for (UIActionInvocation actStart : startModals) {

		// 		if (actStart instanceof UIActionInvocationStartModal) {
		// 			UIObject targetObject =
		// 				((UIActionInvocationStartModal)actStart).targetObject;
		// 			if (targetObject != null) {
		// 				nextState = targetObject.getAllInitialEvents();

		// 				// add the transition
		// 				lts.addTransition(currentState, act, nextState);
		// 				transIDs.put(act, act.getName());	
		// 				break;	
		// 			}
		// 		}
		// 	}

		// 	// if (nextState == null) {
		// 	// 	nextState = new HashSet<UIAction>();

		// 	// 	nextState.add(new UIAction());

		// 	// 	lts.terminalStates.add(nextState);
		// 	// 	terminal = true;	
		// 	// }	
		// } 

		for (Set<UIAction> state : lts.states) {
			for (UIAction actInv : state) {
				if (actInv instanceof UIActionInternal) {
						
					UIActionInternal act = (UIActionInternal)actInv;

					Set<UIAction> nextState = null;
					
					// if it is a terminal state
					Set<UIActionInvocation> startModals = 
						act.getStartEndModals();

					if (startModals.size() > 0) {

						for (UIActionInvocation actStart : startModals) {

							if (actStart instanceof UIActionInvocationStartModal) {
								UIObject targetObject =
									((UIActionInvocationStartModal)actStart).targetObject;
								if (targetObject != null) {
									found = true;
									break;	
								}
							}
						}

						if (found == false) {
							// if this state ends itself
							// it will to its caller, if any
							// if it has no caller, then it moves to a terminal state

							for (LTS.Transition<Set<UIAction>, UIAction>
									trans : lts.transitions) {
								if (trans.toState.equals(state)){
									nextState = 
										trans.fromState;
									//break;
								}
							}

							// no caller 
							if (nextState == null) {
								nextState = new HashSet<UIAction>();

								nextState.add(new UIAction());

								lts.terminalStates.add(nextState);
							}

							
							// add the transition
							lts.addTransition(state, actInv, nextState);
							transIDs.put(actInv, actInv.getName());	

							if (!lts.states.contains(nextState)) {
								// if this is a new state, add it to the set
								newStates.add(nextState);
								stateIDs.put(nextState, "s" + Integer.toString(idSGen.next()));
							}
						}
						// found a target object, this has been handled above
						//	continue;
					}
				}
			}
		}

		lts.states.addAll(newStates);

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

			bw.write("}");

			bw.close();
 
			System.out.println("Done writing file.");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	//@Ignore
	public void testProject() {
		String projectList = "/Users/hans/Desktop/android/app-projects copy.txt";
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

	// public void getProjectOverview(String folderFullPath, 
	// 								Set<String> interestingClasses,
	// 								String[] androidLibs) {

	// 	HashMap<String,CompilationUnit> units;
	// 	HashMap<String, Integer> counts = new HashMap<String, Integer>();
	// 	for (String s: interestingClasses) {
	// 		counts.put(s,0);
	// 	}

	// 	System.out.println("Project: " + folderFullPath);

	// 	units = parseProject(folderFullPath, androidLibs);
	// 	Integer countAll = 0;
	// 	for (Entry<String, CompilationUnit> e : units.entrySet()) {
	// 		CompilationUnit u = e.getValue();
	// 		List<AbstractTypeDeclaration> types = u.types();
	// 		ITypeBinding tBind;
	// 		String typeName;

	// 		for (AbstractTypeDeclaration t : types) {
	// 			tBind = t.resolveBinding();
	// 			if ((typeName = ASTNodeUtils.matchSuperClass(tBind, interestingClasses)) != null) {
	// 				System.out.println("\t class: " + tBind.getQualifiedName()
	// 					+ " > " + typeName);

	// 				counts.put(typeName, counts.get(typeName)+1);
	// 			}
	// 			countAll++;
	// 		}
	// 	}

	// 	// DEBUG
	// 	System.out.println("Numbers: ");
	// 	for (String s: counts.keySet()) {
	// 		System.out.println("\t" + s + " : " + counts.get(s));
	// 	}
	// 	System.out.println("\tAll : " + countAll);

	// 	units.clear();
	// 	units = null;

	// 	System.out.println(TestSuit.LONG_DASH);
	// }

	

	private HashMap<String,CompilationUnit> parseProject(String path,
														String[] androidLibs) {

		return 	Parser.parse(
				FileUtils.getFilePaths(path),
				androidLibs,
				/* path that contain the source files*/
				FileUtils.getFolderPaths(path)
			);
	}


	@Test
	@Ignore
	public void testEventLink() {		
		

		String[] projectPaths = new String[] {
							"/Users/hans/Desktop/ast/astparser/test-android/ast"
							};


		String[] libs = new String[]{
			"lib/android/android-18.jar",
			"android-support-v4.jar",
			"android-support-v7-appcompat.jar",
			"android-support-v7-gridlayout.jar",
			"android-support-v7-mediarouter.jar",
			"android-support-v13.jar"
		};

		for (String projectPath : projectPaths) {
			System.out.println("Project: " + projectPath);

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

			// for (Entry<String, UIActionClass> e : androidUIActions.entrySet()) {
			// 	if (e.getValue().type == UIActionClass.UIActionType.BIND_EVENT)
			// 		System.out.println(e.getKey());
			// }

			// 2. parse project
			HashMap<String,CompilationUnit> units = parseProject(projectPath, libs);


			// 3. find all actions, set up their types and invocations
			MethodVisitor methodVisitor 
						= new MethodVisitor(new UIActionBuilder(androidUIActions));

			for (CompilationUnit u : units.values()) {
				// DEBUG
				// for (IProblem prob : u.getProblems())
				// 	System.out.println(prob);

				u.accept(methodVisitor);
			}

			HashMap<IMethodBinding, UIAction> 
					allActions = methodVisitor.getAllActions();

			// 3a. find the event objects (as variables)
			Set<String> eventClassKeys = new HashSet<String>();

			for (UIActionClass uAct : androidUIActions.values()) {
				if (uAct instanceof UIActionLinkedEventClass) {
					eventClassKeys.add(uAct.classKey);
				}
			}

			// DEBUG
			// for (String s : eventClassKeys) {
			// 	System.out.println(s);
			// }

			// System.out.println(TestSuit.LONG_DASH);

			EventObjectVisitor eventObjectVisitor 
								= new EventObjectVisitor(eventClassKeys);

			for (CompilationUnit u : units.values()) {
				u.accept(eventObjectVisitor);
			}

			HashMap<IVariableBinding, UIEventObject> 
					allEventObjects = eventObjectVisitor.getAllEventObjects();

			// DEBUG
			// for (UIEventObject uiEO : allEventObjects.values()) {

			// 	System.out.println("Decl: " + uiEO.declaration);

			// 	for (String s : uiEO.superTypeKeys) {
			// 		System.out.println(s);
			// 	}

			// 	if (uiEO.references != null) {
			// 		System.out.println("References: ");
			// 		for (Expression ex : uiEO.references) {
			// 			System.out.println(ex + " in " + ex.getParent());
			// 		}
			// 	}
			// 	System.out.println(TestSuit.SHORT_DASH);
			// }

			// 4. link event setters with corresponding events
			ASTNodeUtils.bindEventSetters(allActions, allEventObjects);

			// System.out.println(TestSuit.LONG_DASH);
		}
	}

	@Test
	@Ignore
	public void testClassExtraction() {		
		

		String[] projectPaths = new String[] {
							"/Users/hans/Desktop/ast/astparser/test-android/ast"
							};


		String[] libs = new String[]{
			"lib/android/android-18.jar",
			"android-support-v4.jar",
			"android-support-v7-appcompat.jar",
			"android-support-v7-gridlayout.jar",
			"android-support-v7-mediarouter.jar",
			"android-support-v13.jar"
		};

		for (String projectPath : projectPaths) {
			System.out.println("Project: " + projectPath);

			

			// for (Entry<String, UIActionClass> e : androidUIActions.entrySet()) {
			// 	if (e.getValue().type == UIActionClass.UIActionType.BIND_EVENT)
			// 		System.out.println(e.getKey());
			// }

			// 2. parse project
			HashMap<String,CompilationUnit> units = parseProject(projectPath, libs);


			// 3. find all actions, set up their types and invocations
			ASTVisitor classVisitor 
						= new ASTVisitor() {
							@Override
							public boolean visit(ClassInstanceCreation node) {
								System.out.println(node.resolveTypeBinding().getQualifiedName());
								List<Expression> exps = node.arguments();
								for (Expression exp : exps) {
									ITypeBinding typeBinding = exp.resolveTypeBinding();

									if (typeBinding != null &&
										typeBinding.isParameterizedType() && 
										typeBinding.getErasure().getQualifiedName().toString().equals("java.lang.Class")) {

										System.out.println(exp + " " + typeBinding.getErasure().getQualifiedName());		

										for (ITypeBinding typeArg : typeBinding.getTypeArguments()){
											System.out.println(typeArg.getQualifiedName());
										}
									}
									
								
								}
								return true;
							}
						};

			for (CompilationUnit u : units.values()) {
				// DEBUG
				// for (IProblem prob : u.getProblems())
				// 	System.out.println(prob);

				u.accept(classVisitor);
			}

		}
	}
}