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
	
	// @Test
	// @Ignore
	// public void testApproach3() {

	// 	/*---- Find events that are set up in initializer implicit acts----*/
	// 	for (UIObject.UILinkedEventObject uiLinkedEventObj : uiEventObjs.values()) {
	// 		for (UIActionInvocation setter : uiLinkedEventObj.setters) {
	// 			// find which Object's init method that make the set.
	// 			for (UIObject uiO : uiObjs.values()) {
	// 				for (List<Set<UIActionInvocation>> initMethodPath : uiO.initPaths.values()) {
	// 					for (Set<UIActionInvocation> initPath : initMethodPath) {
	// 						if (initPath.contains(setter)) {
	// 							if (uiO.initEvents == null)
	// 								uiO.initEvents = new HashSet<UIObject.UILinkedEventObject>();

	// 							uiO.initEvents.add(uiLinkedEventObj);
	// 						}
	// 					}
	// 				}
	// 			}
	// 			// find which event that make the set
	// 		}
	// 	}
		
	// 	/*--------- Build LTS ----------*/
	// 	LTS<Set<UIAction>, UIAction> lts = new LTS<Set<UIAction>, UIAction>();

	// 	for (UIObject obj : uiObjs.values()) {
			
	// 		// set up initial state
	// 		Set<UIAction> initialState = new Set<UIAction>();
	// 		UIAction currentAct;
	// 		// get top event
	// 		for (IMethodBinding topEvent : obj.topEventPaths.keySet()) {
	// 			if (methodReferences.containsKey(topEvent)) {
	// 				currentAct = methodReferences.get(topEvent);
	// 				initialState.add(currentAct);
	// 				lts.actions.add(currentAct);
	// 			}
	// 		}

	// 		// get linked-event 
	// 		for (UIObject.UILinkedEventObject uiLEO : obj.initEvents) {
	// 			for (IMethodBinding linkEvent : uiLEO.eventPaths.keySet()){
	// 				if (methodReferences.containsKey(linkEvent)) {
	// 					currentAct = methodReferences.get(linkeEvent);
	// 					initialState.add(currentAct);
	// 					lts.actions.add(currentAct);
	// 				}
	// 			}
	// 		}

	// 		lts.states.add(initialState);
	// 		lts.initialStates.add(initialState);

	// 		Set<UIAction> nextState;
	// 		// now for each initialState's possible action, add transition
	// 		for (Entry<IMethodBinding,List<Set<UIActionInvocation>>>
	// 				 topEvent : obj.topEventPaths.entrySet()) {
	// 			if (methodReferences.containsKey(topEvent.getKey())) {
	// 				currentAct = methodReferences.get(topEvent);
					

	// 				List<Set<UIActionInvocation>> paths = topEvent.getValue();

	// 				for (Set<UIAction> path : paths) {
	// 					for (UIAction extAction : path) {

	// 			/* check if extAction 
	// 				- enabled a new event
	// 					set up new event handler
	// 					show up some menu (not sure for now)
	// 				- disabled some event
	// 					disable some widgets
	// 				- start a completely new window
	// 					startActivity
	// 					show dialog9
	// 				- close the current window
	// 					finish
	// 			BIND_EVENT
	// 			START_MODAL
	// 			END_MODAL
	// 			OPEN_MENU
	// 			ENABLE_WIDGET
	// 			*/			if (extAction.metaClassInfo != null 
	// 							&& extAction.metaClassInfo.type != null) {
	// 							switch (extAction.metaClassInfo.type) {
	// 								case BIND_EVENT:
	// 									break;
	// 								case START_MODAL:
	// 									break;
	// 								case END_MODAL:
	// 									break;
	// 								case OPEN_MENU:
	// 									break;
	// 								case ENABLE_WIDGET:
	// 									break;
	// 							}
	// 						}
							
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	/*------------------------- Printer code ------------------------------*/
	// 	class Printer {
	// 		public void print(String mes, Set<UIActionInvocation> actList) {
	// 			System.out.print(mes);
	// 			for (UIActionInvocation act : actList) {
	// 				System.out.print(act.astSourceNode.getExpression()
	// 					 + "." + act.astSourceNode.getName() + " -> ");
	// 			}
	// 			System.out.println(".");
	// 		}
	// 	}

	// 	Printer printer = new Printer();

	// 	System.out.println(TestSuit.SHORT_DASH + " UIObjects " + TestSuit.SHORT_DASH);
	// 	for (UIObject obj : uiObjs.values()) {
	// 		System.out.println(obj.typeBinding.getQualifiedName());
	// 		System.out.println(obj.typeBinding.getKey());
	// 		if (obj.metaClassInfo != null)
	// 			System.out.println("Meta: " + obj.metaClassInfo.classKey);
	// 		else 
	// 			System.out.println("No meta class");

	// 		if (obj.initPaths != null) {
	// 			System.out.println("\t -> INITS");
	// 			for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
	// 								obj.initPaths.entrySet()) {
	// 			System.out.println("\t -> " + e.getKey().getKey());

	// 			for (Set<UIActionInvocation> initMethodPath : e.getValue())
	// 				printer.print("\t\t |- ", initMethodPath);
	// 			}
	// 			System.out.println(TestSuit.SHORT_DASH);
	// 		}
			
	// 		if (obj.topEventPaths != null) {
	// 			System.out.println("\t -> TOPEVENTS");
	// 			for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
	// 									obj.topEventPaths.entrySet()) {
	// 				System.out.println("\t -> " + e.getKey().getKey());

	// 				for (Set<UIActionInvocation> topEventMethodPath : e.getValue())
	// 					printer.print("\t\t |- ", topEventMethodPath);
	// 			}
	// 			System.out.println(TestSuit.SHORT_DASH);
	// 		}

	// 		if (obj.initEvents != null) {
	// 			System.out.println("\t -> Init Events ");

	// 			for (UIObject.UILinkedEventObject ieObj : obj.initEvents) {
	// 				System.out.println(ieObj.typeBinding.getQualifiedName());
	// 				System.out.println(ieObj.typeBinding.getKey());
					
	// 				System.out.println("\t Affective range: ");

	// 				for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
	// 										ieObj.eventPaths.entrySet()) {
	// 					System.out.println("\t -> " + e.getKey().getKey());

	// 					for (Set<UIActionInvocation> eventMethodPath : e.getValue())
	// 						printer.print("\t\t |- ", eventMethodPath);
	// 				}

	// 				System.out.println(TestSuit.SHORT_DASH);

	// 				System.out.println("\t Set up by:");
	// 				for (UIActionInvocation setter : ieObj.setters) {
	// 					System.out.println("\t\t " + setter.astSourceNode.getExpression()
	// 							 + "." + setter.astSourceNode.getName());
	// 				}
	// 			}
	// 		}
			
	// 		System.out.println(TestSuit.LONG_DASH);

	// 	}


	// 	System.out.println(TestSuit.SHORT_DASH + " LinkedEvents " + TestSuit.SHORT_DASH);

	// 	for (UIObject.UILinkedEventObject obj : uiEventObjs.values()) {
	// 		System.out.println(obj.typeBinding.getQualifiedName());
	// 		System.out.println(obj.typeBinding.getKey());
			
	// 		System.out.println("\t Affective range: ");

	// 		for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
	// 								obj.eventPaths.entrySet()) {
	// 			System.out.println("\t -> " + e.getKey().getKey());

	// 			for (Set<UIActionInvocation> eventMethodPath : e.getValue())
	// 				printer.print("\t\t |- ", eventMethodPath);

				
	// 		}
	// 		System.out.println(TestSuit.SHORT_DASH);

	// 		System.out.println("\t Set up by:");
	// 		for (UIActionInvocation setter : obj.setters) {
	// 			System.out.println("\t\t " + setter.astSourceNode.getExpression()
	// 					 + "." + setter.astSourceNode.getName());
	// 		}

	// 		System.out.println(TestSuit.LONG_DASH);
	// 	}
	// 	/*------------------------- End Printer code --------------------------*/
	// }

	@Test
	public void testApproach4() {
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
		HashMap<String,CompilationUnit> units = Parser.parse(
				FileUtils.getFiles("test-android/todomanager", 
									new String[] {".java", ".JAVA"}),
				new String[]{"lib/android/android-18.jar"},
				new String[] { /* path that contain the source files*/
					"test-android/todomanager"
				});


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

		// 5. find all ui external actions, and trace their way up to the
		// INTERNAL_UI methods. This completes all the UIAction info.
		ASTNodeUtils.traceExternalUIPaths(allActions);

		// 6. find all UIObjects, attach their UIObjectClass and their init
		//	and top-event actions
		HashMap<ITypeBinding, UIObject> allUIObjects = 
			ASTNodeUtils.findAllUIObjects(androidUIStructures, allActions);

		// 7. now we are ready to build the LTS
		/*
			Each state of the LTS is identified by the possible events at that 
			state.

			In the initial states, possible events includes top-events and linked
			events that are set up by the initializers

			For going to the next state, we shall check each event allowed in
			the current state:
				- if the event change the current possible event set, then it
					create a transition from the current state to another state
				- if the event does not change the possible event set, then it
					create a transition from and to the current state itself

			All the process follows from finding the effects of each event handler
			or initializer
		*/



		/*------------------------- Printer code ------------------------------*/
		class Printer {
			public void printIntAct(String mes, UIActionInternal internalAct) {
				System.out.println(mes + internalAct.methodBinding.getKey());
				if (internalAct.executingPaths != null)
					for (Set<UIActionInvocation> path : internalAct.executingPaths) {
						for (UIActionInvocation act : path) {
							System.out.print(act.astSourceNode.getExpression()
								 + "." + act.astSourceNode.getName() + " -> ");
						}
						System.out.println(".");
					}
			}
		}

		Printer printer = new Printer();


		System.out.println(TestSuit.SHORT_DASH + " UIObjects " + TestSuit.SHORT_DASH);
		for (UIObject obj : allUIObjects.values()) {
			System.out.println(obj.typeBinding.getQualifiedName());
			
			if (obj.metaClassInfo != null)
				System.out.println("Meta: " + obj.metaClassInfo.classKey);
			else 
				System.out.println("No meta class");

			if (obj.initActions != null) {
				System.out.println("\t -> INITS");
				for (UIAction act : obj.initActions.values()) {
					if (act instanceof UIActionInternal)
						printer.printIntAct("Act: ", (UIActionInternal)act);
				}
				System.out.println(TestSuit.SHORT_DASH);
			}
			
			if (obj.topEventActions != null) {
				System.out.println("\t -> TOPEVENTS");
				for (UIAction act : obj.topEventActions.values()) {
					if (act instanceof UIActionInternal)
						printer.printIntAct("Act: ", (UIActionInternal)act);
				}
				System.out.println(TestSuit.SHORT_DASH);
			}
		}

			

		// System.out.println(TestSuit.SHORT_DASH + " LinkedEvents " + TestSuit.SHORT_DASH);

		// for (UIObject.UILinkedEventObject obj : uiEventObjs.values()) {
		// 	System.out.println(obj.typeBinding.getQualifiedName());
		// 	System.out.println(obj.typeBinding.getKey());
			
		// 	System.out.println("\t Affective range: ");

		// 	for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
		// 							obj.eventPaths.entrySet()) {
		// 		System.out.println("\t -> " + e.getKey().getKey());

		// 		for (Set<UIActionInvocation> eventMethodPath : e.getValue())
		// 			printer.print("\t\t |- ", eventMethodPath);

				
		// 	}
		// 	System.out.println(TestSuit.SHORT_DASH);

		// 	System.out.println("\t Set up by:");
		// 	for (UIActionInvocation setter : obj.setters) {
		// 		System.out.println("\t\t " + setter.astSourceNode.getExpression()
		// 				 + "." + setter.astSourceNode.getName());
		// 	}

		// 	System.out.println(TestSuit.LONG_DASH);
		// }
		/*------------------------- End Printer code --------------------------*/
	}	
}