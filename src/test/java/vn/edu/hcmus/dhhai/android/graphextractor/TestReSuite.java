package vn.edu.hcmus.dhhai.android.graphextractor;

/* astparser packages */
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;


import vn.edu.hcmus.dhhai.android.graphextractor.UIModel.*;
/* junit packages */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
@RunWith(JUnit4.class)
public class TestReSuite extends AbstractTestSuite {

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
			
			CompilationUnit u = entry.getValue();

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
		// 					System.out.println(act.astSourceNode + " null binded");
		// 				} else {
		// 					System.out.println(act.astSourceNode + " binded " + AbstractTestSuite.SHORT_DASH);
		// 					for (UIAction bindedAct : bindEAct.bindedEvents) {
		// 						System.out.println("\t"+ bindedAct.methodBinding.getKey());
		// 					}
		// 					System.out.println(AbstractTestSuite.LONG_DASH);
		// 				}
		// 			}
		// 		}
		// 	}
		// }
		// -END DEBUG							
		
		

		// 5. find all ui external actions, and trace their way up to the
		// INTERNAL_UI methods. This completes all the UIAction info.
		ASTNodeUtils.traceExternalUIPaths(allActions);


		// UIActionInternal DEBUGGER
		class InternalActionPrinter {
			public void print(Collection<UIAction> actions) {
				for (UIAction act : actions) {
					if (act instanceof UIActionInternal) {
						UIActionInternal actInt = (UIActionInternal)act;
						System.out.println(actInt.methodBinding.getKey());

						if (actInt.executingPaths != null) {
							System.out.println(actInt.methodBinding.getKey());
							System.out.println("CHAINS:");
							for (LinkedHashSet<UIActionStatement> path : actInt.executingPaths) {
								for (UIActionStatement actStm : path) {
									System.out.print(actStm + " <- ");
								}
								System.out.println(".");
							}
							
						} 
						System.out.println(TestSuite.LONG_DASH);
					}
				}
			}
		}
		// END DEBUGGER
		

		// DEBUG - all the paths

		// InternalActionPrinter actPrinter = new InternalActionPrinter();
		// actPrinter.print(allActions.values());

		// END DEBUG

		// 6. find all UIObjects, attach their UIObjectClass and their init
		//	and top-event actions
		HashMap<ITypeBinding, UIObject> allUIObjects = 
			ASTNodeUtils.findAllUIObjects(androidUIStructures, allActions);

		// DEBUG
		// InternalActionPrinter actPrinter = new InternalActionPrinter();
		// for (UIObject obj : allUIObjects.values()) {
		// 	System.out.println(TestSuite.LONG_DASH);
		// 	System.out.println(obj.typeBinding.getQualifiedName());
		// 	if (obj.initActions != null) {
		// 		System.out.println("INITACTION");
		// 		actPrinter.print(obj.initActions.values());
		// 	}
			
		// 	if (obj.topEventActions != null) {
		// 		System.out.println("TOPEVENT");
		// 		actPrinter.print(obj.topEventActions.values());
		// 	}
		// }
		// END DEBUG

		// 7. link enable widgets with the event they affect
		ASTNodeUtils.bindEnableWidgetWithEvents(allActions, allUIObjects);

		// // 8. link start modals with their target
		IntentVisitor intentVisitor = new IntentVisitor();

		for (CompilationUnit u : units.values()) {
			u.accept(intentVisitor);
		}

		HashMap<IVariableBinding, IntentVisitor.IntentInfo>
			allIntents = intentVisitor.getAllIntents();


		ASTNodeUtils.bindStartModals(allActions, allUIObjects, allIntents);

		// DEBUG
		// InternalActionPrinter actPrinter = new InternalActionPrinter();
		// for (UIObject obj : allUIObjects.values()) {
		// 	System.out.println(TestSuite.LONG_DASH);
		// 	System.out.println(obj.typeBinding.getQualifiedName());
		// 	if (obj.initActions != null) {
		// 		System.out.println("INITACTION");
		// 		actPrinter.print(obj.initActions.values());
		// 	}
			
		// 	if (obj.topEventActions != null) {
		// 		System.out.println("TOPEVENT");
		// 		actPrinter.print(obj.topEventActions.values());
		// 	}
		// }
		// END DEBUG

		// for (UIObject obj : allUIObjects.values()) {
			
		// 	if (obj.initActions != null) {
		// 		for (UIAction act : obj.initActions.values()) {
		// 			if (act instanceof UIActionInternal) {
		// 				UIActionInternal actInt = (UIActionInternal)act;
		// 				actInt.buildExecutingTree();
		// 			}
		// 		}
		// 	}
			
		// 	if (obj.topEventActions != null) {
		// 		for (UIAction act : obj.topEventActions.values()) {
		// 			if (act instanceof UIActionInternal) {
		// 				UIActionInternal actInt = (UIActionInternal)act;
		// 				actInt.buildExecutingTree();
		// 			}
		// 		}
		// 	}
		// }


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

		// set up initial LTS, where each node represents the initial state of 
		// each ui object
		for (UIObject obj : allUIObjects.values()) {
			
			// set up initial state
			// DEBUG
			// System.out.println(TestSuite.LONG_DASH);
			// System.out.println(obj.typeBinding.getKey());
			// END DEBUG

			Collection<Set<UIAction>> initialStates = obj.getAllPossibleInitialActionSets();
			
			lts.states.addAll(initialStates);
			lts.initialStates.addAll(initialStates);

			for (Set<UIAction> initialState : initialStates) {
				lts.actions.addAll(initialState);
				
				// id for a state
				stateIDs.put(initialState, obj + Integer.toString(idSGen.next()));
			}			
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

						// TODO: better refine deltas
						List<StateDelta> stateDeltas
									 = act.getPossibleStateDeltas();

						// if no state delta can be found, then returns to the current state
						if (stateDeltas == null || stateDeltas.isEmpty()) {
							// add the transition
							lts.addTransition(currentState, act, currentState);

							if (!transIDs.containsKey(act))
									transIDs.put(act, act 
												 + Integer.toString(idSGen.next()));
							continue;
						}

						// else check for each possible effect by this action
						for (StateDelta stateDel : stateDeltas) {
							Set<UIAction> nextState;

							// start/end modal dominates
							if (stateDel.startModalEffects != null
									&& !stateDel.startModalEffects.isEmpty()) {
								// if multiple start/end modals appear
								// that would create a hypergraph!
								// 1 edge that connect one vertice to more than 1 other vertices

								// for now we make it a normal graph 
								// by selecting only 1 startObject
								
								for (UIActionInvocationStartModal startModal :
										stateDel.startModalEffects) {
									if (startModal.targetObject != null) {
										UIObject startObject = startModal.targetObject;

										Collection<Set<UIAction>> possibleInitstates
										= startObject.getAllPossibleInitialActionSets();

										// there might be multiple next states
										// for each delta
										// depending on the initial states of 
										// the target object
										for (Set<UIAction> targetInitState : 
												possibleInitstates) {
											// add the transition
											lts.addTransition(currentState, act, targetInitState);
											if (!transIDs.containsKey(act))
												transIDs.put(act, act
													 + Integer.toString(idSGen.next()));
										}
									// targetObject cannot be detected,
									// but if it is a START_MODAL
									} else {
										IMethodBinding invokedBinding
											= startModal.invokedMethod;

										UIAction invokedAction = allActions.get(invokedBinding);

										if (invokedAction != null 
											&& invokedAction.metaClassInfo != null
											&& invokedAction.metaClassInfo.type
											 == UIActionClass.UIActionType.START_MODAL) {

											nextState = new HashSet<UIAction>();

											nextState.add(UIAction.NullAction);
											
											stateIDs.put(nextState, 
											"s" + Integer.toString(idSGen.next()));

											// add the transition
											lts.addTransition(currentState, act, nextState);
											if (!transIDs.containsKey(act))
												transIDs.put(act, act
													 + Integer.toString(idSGen.next()));	
										}
									}
								}
							}
							// or not start/end modal
							// there should be only 1 next state for each delta
							else if ((stateDel.addedActions != null && 
										!stateDel.addedActions.isEmpty()) 
									|| (stateDel.removedActions != null &&
										!stateDel.removedActions.isEmpty())) {
								nextState = new HashSet<UIAction>(currentState);

								if (stateDel.addedActions != null)
									nextState.addAll(stateDel.addedActions);

								if (stateDel.removedActions != null)
									nextState.removeAll(stateDel.removedActions);

								if (!lts.states.contains(nextState)) {
									// if this is a new state, add it to the set
									lts.states.add(nextState);
									stateIDs.put(nextState, 
										"s" + Integer.toString(idSGen.next()));

									// add it to the stack for transition building
									stateStack.addFirst(nextState);	
								}

								// add the transition
								lts.addTransition(currentState, act, nextState);
								if (!transIDs.containsKey(act))
									transIDs.put(act, act
										 + Integer.toString(idSGen.next()));
							}
						}
					}
				}
			}
		}


		// we handle ending-modal-only actions after
		List<Set<UIAction>> endModalStates = new ArrayList<Set<UIAction>>();

		for (Set<UIAction> currentState : lts.states) {
			for (UIAction actInv : currentState) {
				// identify effect of act
				// get all possible effects
				if (actInv instanceof UIActionInternal) {
					UIActionInternal act = (UIActionInternal)actInv;

					List<StateDelta> stateDeltas
								 = act.getPossibleStateDeltas();

					if (stateDeltas == null)
						continue;
					
					// check for each possible effect by this action
					for (StateDelta stateDel : stateDeltas) {
						Set<UIAction> nextState;

						// start/end modal dominates
						if (stateDel.startModalEffects != null
								&& !stateDel.startModalEffects.isEmpty()) {
							
							Set<UIObject> endObjects = new HashSet<UIObject>();
							Set<UIActionInvocationStartModal> endActions 
								= new HashSet<UIActionInvocationStartModal>();
							

							for (UIActionInvocationStartModal startModal :
									stateDel.startModalEffects) {
								if (startModal.endTargetObject != null) {
									endObjects.add(startModal.endTargetObject);
									endActions.add(startModal);
								}
							}

							// current UI objects
							if (!endObjects.isEmpty() && !endActions.isEmpty()) {
								// dealing with multiple ends is not yet solved
								// for now we just deal with ending the current windows
								boolean endCurrentObject = false;
								for (UIActionInvocationStartModal end : endActions) {
									if (end.endCurrentObject == true) {
										endCurrentObject = end.endCurrentObject;
										break;
									}
								}

								if (endCurrentObject == true) {
									// we check if any state leads to the current state
									// if so, ending current states will return to
									// that states
									// or else, it will simply ends
									Set<Set<UIAction>> nextStates
										= new HashSet<Set<UIAction>>();

									for (LTS.Transition<Set<UIAction>, UIAction>
											 trans : lts.transitions) {
										if (trans.toState.equals(currentState) &&
											!trans.fromState.equals(currentState)) {
											
											nextStates.add(trans.fromState);
										}
										// there possibly many incoming states so
										// we keep checking all the transitions
										// TODO: optimize this to avoid checking
										// all the transitions
									}

									// simply ends
									if (nextStates.size() > 0) {
										for (Set<UIAction> nS : nextStates) {
											// add the transition
											lts.addTransition(currentState, act, nS);
											if (!transIDs.containsKey(act))
												transIDs.put(act, act
													 + Integer.toString(idSGen.next()));	
										}
									} else {
										nextState = new HashSet<UIAction>();

										nextState.add(UIAction.NullAction);

										endModalStates.add(nextState);
										
										stateIDs.put(nextState, 
										"s" + Integer.toString(idSGen.next()));

										// add the transition
										lts.addTransition(currentState, act, nextState);
										if (!transIDs.containsKey(act))
											transIDs.put(act, act
												 + Integer.toString(idSGen.next()));	
									}
								}
							}
						}
					}
				}
			}		
		}
		
		lts.states.addAll(endModalStates);
		lts.terminalStates.addAll(endModalStates);
		

		// 9bis. Output map into graphviz format (.gv)
		Map<Set<UIAction>, Map<Set<UIAction>, List<UIAction>>> 
					adjacencyMap = lts.makeAdjacencyMap();

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
			bw.write("node[style=\"filled,solid\", colorscheme=greys3, fillcolor=1, color=3];");
			bw.newLine();
    
			for (Set<UIAction> state : lts.states) {
				
				bw.write(stateIDs.get(state));

				if (lts.terminalStates.contains(state))
					bw.write("[peripheries=2]");

				bw.write(";");
				bw.newLine();
			}

			for (Entry<Set<UIAction>, Map<Set<UIAction>, List<UIAction>>> 
						entry : adjacencyMap.entrySet()) {
				Set<UIAction> fromState = entry.getKey();
				Map<Set<UIAction>, List<UIAction>> map2 = entry.getValue();

				for (Entry<Set<UIAction>, List<UIAction>> entry2
						: map2.entrySet()) {

					Set<UIAction> toState = entry2.getKey();

					List<UIAction> actions = entry2.getValue();

					if (actions.size() > 0) {
						bw.write(stateIDs.get(fromState)
									+ " -> " + stateIDs.get(toState));

						bw.write("[label=\""); 

						
						int size = actions.size();
						for (int i = 0; i < size - 1; i ++) {
							bw.write(transIDs.get(actions.get(i)) + "\n");
						}

						bw.write(transIDs.get(actions.get(size-1)));

						bw.write("\",style=dotted];");
						
						bw.newLine();	
					}
				}
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
		String projectList = "./test-run.txt";
		String outPath = "./graphviz";

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

		for (String projectPath : projectPaths) {
			System.out.println("Project: " + projectPath);
			tryProject(projectPath, libs, 
				outPath + "/" + new File(projectPath).getName() + ".gv");			
			System.out.println(TestSuite.LONG_DASH);
		}
	}

	private HashMap<String,CompilationUnit> parseProject(String path,
														String[] androidLibs) {
		return 	Parser.parse(
				FileUtils.getFilePaths(path),
				androidLibs,
				FileUtils.getFolderPaths(path) /* path containing source files*/
			);
	}
}