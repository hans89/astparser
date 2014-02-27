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


    private boolean matchSuperClass(String superKey, ITypeBinding tbind) {
		while (tbind != null && tbind.getKey() != "Ljava/lang/Object;") {
			String key = tbind.getKey();
			if (superKey.equals(key))
				return true;
			tbind = tbind.getSuperclass();
		}
		
		return false;
	}


	private void addParentNodes(Set<ASTNode> container, ASTNode node) {
		// add all parents of interesting nodes
		while (node != null) {
			container.add(node);
			node = node.getParent();
		}
	}

	private Set<String> getSuperTypeQualifiedNames(ITypeBinding startClass) {
		Set<String> qualNames = null;
		if (startClass != null) {
			qualNames = new HashSet<String>();
			
			Queue<ITypeBinding> tbQueue = new LinkedList<ITypeBinding>();

			tbQueue.offer(startClass);

			ITypeBinding current;

			// a BFS to visit all super types
			// while the queue is not empty
			while ((current = tbQueue.poll()) != null) {
				String currentTypeName = current.getQualifiedName();
				if (!qualNames.contains(currentTypeName)) {
					// if we haven't seen this class yet

					qualNames.add(currentTypeName);

					// we find its direct super types
					tbQueue.addAll(Arrays.asList(current.getInterfaces()));

					ITypeBinding superClass = current.getSuperclass();
					if (superClass != null)
						tbQueue.offer(superClass);
				}
				// or else we have seen this type before,
				// so its super types have been listed,
				// we don't need to do anything
			}
		}

		return qualNames;
	}


	@Test
	public void testApproach3() {

		// 1. get meta info: Android structures
		AndroidUIClassReader structureReader = new AndroidUIClassReader();

		structureReader.parseXML(
					new String[] {
						"android-ui/android.xml"
					});

		// 1a. interesting Android UI Structures
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

		// find all actions

		final HashMap<IMethodBinding, UIAction>
			methodReferences = new HashMap<IMethodBinding, UIAction>();

		// 3. visit the CUs
		/* the visitor will: identify all methods, including definition (declaration) and
				references (invocation). It will identify:
				1. methodBinding
				2. declaration (if available)
				3. invokedList (if available)
		*/
		ASTVisitor methodVisitor = new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				IMethodBinding mBinding;
				if ((mBinding = node.resolveBinding()) != null) {
					UIAction mRef;
					if (!methodReferences.containsKey(mBinding)) {
						mRef = new UIAction();
						mRef.methodBinding = mBinding;
						methodReferences.put(mBinding, mRef);
					} else {
						mRef = methodReferences.get(mBinding);	
					}
					 
					mRef.declaration = node;
				}
				return true;
			}

			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding mBinding;

				if ((mBinding = node.resolveMethodBinding()) != null) {
					UIAction mRef;

					if (!methodReferences.containsKey(mBinding)) {
						mRef = new UIAction();
						mRef.methodBinding = mBinding;
						methodReferences.put(mBinding, mRef);
					} else {
						mRef = methodReferences.get(mBinding);	
					}

					
					if (mRef.invokedList == null)
						mRef.invokedList = new ArrayList<UIActionInvocation>();

					UIActionInvocation actionInvoke = new UIActionInvocation();
					actionInvoke.astSourceNode = node;
					actionInvoke.invokedMethod = mBinding;

					ASTNode parent = actionInvoke.astSourceNode;

					while ((parent = parent.getParent()) != null
					 	&& !(parent instanceof MethodDeclaration))
						;

					if (parent != null)
						actionInvoke.invokingMethod = ((MethodDeclaration)parent).resolveBinding();

					mRef.invokedList.add(actionInvoke);
				}
				return true;
			}
		};



		CompilationUnit[] unitsArray = units.values().toArray(new CompilationUnit[0]);

		for (CompilationUnit u : unitsArray) {
			u.accept(methodVisitor);
		}

		// 4. find the action types
		/* with each method identified by its binding, declaration and invoked list
		 	now identify its meta class and its type
		
		*/
		Set<IMethodBinding> uiExternals = new HashSet<IMethodBinding>();

		HashMap<ITypeBinding, UIObject> uiObjs = new HashMap<ITypeBinding, UIObject>();
		HashMap<ITypeBinding, UIObject.UILinkedEventObject> uiEventObjs
				 = new HashMap<ITypeBinding, UIObject.UILinkedEventObject>();


		for (Entry<IMethodBinding, UIAction> e : methodReferences.entrySet()) {

			IMethodBinding methodBind = e.getKey();
			UIAction mRef = e.getValue();
			ITypeBinding declaringClass = methodBind.getDeclaringClass();

			// check if current method is interesting as declared in android.xml
			Set<String> superTypeNames = ASTNodeUtils.getSuperTypeQualifiedNames(declaringClass);
			String methodName = methodBind.getName();

			UIActionClass interestingActionClass = null;
			for (String superTypeName : superTypeNames) {
				String methodClassName = superTypeName + "#" + methodName;
				if (androidUIActions.containsKey(methodClassName)) {
					interestingActionClass = androidUIActions.get(methodClassName);
					break;
				}
			}

			mRef.metaClassInfo = interestingActionClass;


			// ok, interesting
			if (interestingActionClass != null) {
				
				/*  EXTERNAL_UI */
				if (declaringClass.isFromSource() == false
					&& interestingActionClass.category == UIActionClass.UIActionCategory.OUTSOURCE) {
					mRef.type = UIAction.ActionType.EXTERNAL_UI;

					uiExternals.add(methodBind);
				} else 
					/*  INTERNAL_UI */
					if (declaringClass.isFromSource()
						&& interestingActionClass.category == UIActionClass.UIActionCategory.INSOURCE) {
					switch (interestingActionClass.type) {
						case INIT:
							mRef.type = UIAction.ActionType.INTERNAL_INIT;
							break;
						case TOP_EVENT:
							mRef.type = UIAction.ActionType.INTERNAL_TOP_EVENT;
							break;
						case LINKED_EVENT:
							mRef.type = UIAction.ActionType.INTERNAL_LINKED_EVENT;
							break;
					}
				}

			} else {
				/* EXTERNAL_NON_UI */
				if (declaringClass.isFromSource() == false) {
					mRef.type = UIAction.ActionType.EXTERNAL_NON_UI;
				// can be remove!!!
				}
				else {
				/* INTERNAL, not known yet */
					mRef.type = UIAction.ActionType.INTERNAL_APP_DEFINED;
				}
			}	
		}

		class UIObjectMetaClassFinder {
			public UIObjectClass find(ITypeBinding type, HashMap<String, UIObjectClass> map) {
				ITypeBinding parent = type;

				while (parent != null) {
					String name = parent.getQualifiedName();
					if (map.containsKey(name)) {
						return map.get(name);
					}

					parent = parent.getSuperclass();
				}
				return null;
			}
		}

		UIObjectMetaClassFinder objMetaClassFinder = new UIObjectMetaClassFinder();

		

		for (IMethodBinding method : uiExternals) {
			UIAction mRef = methodReferences.get(method);

			if (mRef != null && mRef.type == UIAction.ActionType.EXTERNAL_UI) {


				Deque<UIActionInvocation> stack = new ArrayDeque<UIActionInvocation>();
				Deque<Set<UIActionInvocation>> pathStack = new ArrayDeque<Set<UIActionInvocation>>();

				Set<UIActionInvocation> currentPath;

				for (UIActionInvocation act : mRef.invokedList) {
					currentPath = new LinkedHashSet<UIActionInvocation>();

					currentPath.add(act);

					stack.addFirst(act);
					pathStack.addFirst(currentPath);	
				}
				

				UIActionInvocation currentAct;
				// DFS
				while ((currentAct = stack.peekFirst()) != null) {
					stack.removeFirst();
					currentPath = pathStack.removeFirst();
				
					UIAction invoker = methodReferences.get(currentAct.invokingMethod);

					if (invoker.invokedList == null || invoker.invokedList.size() == 0) {
						// if the invoker of the invoker is not available: 
						// we have reach the sink node
						ITypeBinding invokerDeclaringClass 
							= currentAct.invokingMethod.getDeclaringClass();

						switch (invoker.type) {
							case INTERNAL_INIT:
								UIObject uiO;
								if (!uiObjs.containsKey(invokerDeclaringClass)) {
									uiO = new UIObject();
									uiO.typeBinding = invokerDeclaringClass;
									uiO.metaClassInfo = objMetaClassFinder.find(
										invokerDeclaringClass, androidUIStructures);

									uiObjs.put(invokerDeclaringClass, uiO);	
								}

								uiO = uiObjs.get(invokerDeclaringClass);
								if (uiO.initPaths == null)
									uiO.initPaths = new 
										HashMap<IMethodBinding,List<Set<UIActionInvocation>>>();

								if (!uiO.initPaths.containsKey(invoker.methodBinding))
									uiO.initPaths.put(invoker.methodBinding,
											new ArrayList<Set<UIActionInvocation>>());

								uiO.initPaths.get(invoker.methodBinding).add(currentPath);
								
								break;

							case INTERNAL_TOP_EVENT:
								UIObject uiO2;
								if (!uiObjs.containsKey(invokerDeclaringClass)) {
									uiO2 = new UIObject();
									uiO2.typeBinding = invokerDeclaringClass;
									uiO2.metaClassInfo = objMetaClassFinder.find(
										invokerDeclaringClass, androidUIStructures);

									uiObjs.put(invokerDeclaringClass, uiO2);	
								}

								uiO2 = uiObjs.get(invokerDeclaringClass);
								if (uiO2.topEventPaths == null)
									uiO2.topEventPaths = new 
										HashMap<IMethodBinding,List<Set<UIActionInvocation>>>();

								if (!uiO2.topEventPaths.containsKey(invoker.methodBinding))
									uiO2.topEventPaths.put(invoker.methodBinding,
											new ArrayList<Set<UIActionInvocation>>());

								uiO2.topEventPaths.get(invoker.methodBinding).add(currentPath);

								break;
							case INTERNAL_LINKED_EVENT:
								UIObject.UILinkedEventObject uiEObj;


								if (!uiEventObjs.containsKey(invokerDeclaringClass)) {
									uiEObj = new UIObject.UILinkedEventObject();
									uiEObj.typeBinding = invokerDeclaringClass;

									uiEventObjs.put(invokerDeclaringClass, uiEObj);	
								}

								uiEObj = uiEventObjs.get(invokerDeclaringClass);

								if (uiEObj.eventPaths == null)
									uiEObj.eventPaths = new 
										HashMap<IMethodBinding,List<Set<UIActionInvocation>>>();

								if (!uiEObj.eventPaths.containsKey(invoker.methodBinding))
									uiEObj.eventPaths.put(invoker.methodBinding,
											new ArrayList<Set<UIActionInvocation>>());

								uiEObj.eventPaths.get(invoker.methodBinding).add(currentPath);
								break;
						}

					} else {
						for (UIActionInvocation invokingAct : invoker.invokedList) {
							if (!currentPath.contains(invokingAct)) {
								Set<UIActionInvocation> nextPath 
									= new LinkedHashSet<UIActionInvocation>(currentPath);

								nextPath.add(invokingAct);

								stack.addFirst(invokingAct);
								pathStack.addFirst(nextPath);
							}
						}
					}
				}
			}
		}

		for (IMethodBinding method : uiExternals) {
			UIAction mRef = methodReferences.get(method);

			if (mRef != null && mRef.type == UIAction.ActionType.EXTERNAL_UI) {

				for (UIActionInvocation act : mRef.invokedList) {

					List<Expression> args = act.astSourceNode.arguments();
					ITypeBinding argTypeBinding;
					
					for (Expression exp : args) {
						argTypeBinding = exp.resolveTypeBinding();
						if (argTypeBinding != null 
							&& uiEventObjs.containsKey(argTypeBinding)) {
							UIObject.UILinkedEventObject obj =
								uiEventObjs.get(argTypeBinding);

							if (obj.setters == null)
								obj.setters = new HashSet<UIActionInvocation>();

							obj.setters.add(act);
						}
					}
				}
			}
		}	
		

		for (UIObject.UILinkedEventObject uiLinkedEventObj : uiEventObjs.values()) {
			for (UIActionInvocation setter : uiLinkedEventObj.setters) {
				// find which Object's init method that make the set.
				for (UIObject uiO : uiObjs.values()) {
					for (List<Set<UIActionInvocation>> initMethodPath : uiO.initPaths.values()) {
						for (Set<UIActionInvocation> initPath : initMethodPath) {
							if (initPath.contains(setter)) {
								if (uiO.initEvents == null)
									uiO.initEvents = new HashSet<UIObject.UILinkedEventObject>();

								uiO.initEvents.add(uiLinkedEventObj);
							}
						}
					}
				}
				// find which event that make the set
			}
		}
		
		class Printer {
			public void print(String mes, Set<UIActionInvocation> actList) {
				System.out.print(mes);
				for (UIActionInvocation act : actList) {
					System.out.print(act.astSourceNode.getExpression()
						 + "." + act.astSourceNode.getName() + " -> ");
				}
				System.out.println(".");
			}
		}

		Printer printer = new Printer();

		System.out.println(TestSuit.SHORT_DASH + " UIObjects " + TestSuit.SHORT_DASH);
		for (UIObject obj : uiObjs.values()) {
			System.out.println(obj.typeBinding.getQualifiedName());
			System.out.println(obj.typeBinding.getKey());
			if (obj.metaClassInfo != null)
				System.out.println("Meta: " + obj.metaClassInfo.classKey);
			else 
				System.out.println("No meta class");

			if (obj.initPaths != null) {
				System.out.println("\t -> INITS");
				for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
									obj.initPaths.entrySet()) {
				System.out.println("\t -> " + e.getKey().getKey());

				for (Set<UIActionInvocation> initMethodPath : e.getValue())
					printer.print("\t\t |- ", initMethodPath);
				}
				System.out.println(TestSuit.SHORT_DASH);
			}
			
			if (obj.topEventPaths != null) {
				System.out.println("\t -> TOPEVENTS");
				for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
										obj.topEventPaths.entrySet()) {
					System.out.println("\t -> " + e.getKey().getKey());

					for (Set<UIActionInvocation> topEventMethodPath : e.getValue())
						printer.print("\t\t |- ", topEventMethodPath);
				}
				System.out.println(TestSuit.SHORT_DASH);
			}

			if (obj.initEvents != null) {
				System.out.println("\t -> Init Events ");

				for (UIObject.UILinkedEventObject ieObj : obj.initEvents) {
					System.out.println(ieObj.typeBinding.getQualifiedName());
					System.out.println(ieObj.typeBinding.getKey());
					
					System.out.println("\t Affective range: ");

					for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
											ieObj.eventPaths.entrySet()) {
						System.out.println("\t -> " + e.getKey().getKey());

						for (Set<UIActionInvocation> eventMethodPath : e.getValue())
							printer.print("\t\t |- ", eventMethodPath);
					}

					System.out.println(TestSuit.SHORT_DASH);

					System.out.println("\t Set up by:");
					for (UIActionInvocation setter : ieObj.setters) {
						System.out.println("\t\t " + setter.astSourceNode.getExpression()
								 + "." + setter.astSourceNode.getName());
					}
				}
			}
			
			System.out.println(TestSuit.LONG_DASH);

		}


		System.out.println(TestSuit.SHORT_DASH + " LinkedEvents " + TestSuit.SHORT_DASH);

		for (UIObject.UILinkedEventObject obj : uiEventObjs.values()) {
			System.out.println(obj.typeBinding.getQualifiedName());
			System.out.println(obj.typeBinding.getKey());
			
			System.out.println("\t Affective range: ");

			for (Entry<IMethodBinding, List<Set<UIActionInvocation>>> e : 
									obj.eventPaths.entrySet()) {
				System.out.println("\t -> " + e.getKey().getKey());

				for (Set<UIActionInvocation> eventMethodPath : e.getValue())
					printer.print("\t\t |- ", eventMethodPath);

				
			}
			System.out.println(TestSuit.SHORT_DASH);

			System.out.println("\t Set up by:");
			for (UIActionInvocation setter : obj.setters) {
				System.out.println("\t\t " + setter.astSourceNode.getExpression()
						 + "." + setter.astSourceNode.getName());
			}

			System.out.println(TestSuit.LONG_DASH);
		}
	}
}