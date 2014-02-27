package astparser;

/* astparser packages */
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.compiler.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import astparser.visitor.*;
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

  //   @Test
  //   @Ignore
  //   public void testNodeTypesNameRef() {
  //   	HashMap<String,CompilationUnit> units = Parser.parse(
		// 		FileUtils.getFiles("test-android/ast", 
		// 			new String[] {".java", ".JAVA"}),
		// 		new String[]{"lib/android/android-11.jar"},
		// 		new String[] { /* path that contain the source files*/
		// 			"test-android/ast"
		// 		});

  //   	String[] androidClassKeys;

		// try {
		// 	androidClassKeys = 
		// 	FileUtils.getAllLines("android-ui/class.key");
		// } catch (java.io.IOException ex) {
		// 	System.out.println(ex);
		// 	return;
		// }

		// NameReferenceVisitor refVisitor = new NameReferenceVisitor(
		// 		androidClassKeys
		// 	);

		// for (Entry<String, CompilationUnit> e : units.entrySet()) {
  //   		CompilationUnit u = e.getValue();
		// 	u.accept(refVisitor);
		// }

		// List<ReferenceInfo> refList = refVisitor.getNonEmpty();
		// List<ASTNode> nodes;

		// // 43: SimpleType
		// // http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.6/org.eclipse.jdt/core/3.6.0/org/eclipse/jdt/core/dom/ASTNode.java

		// for (ReferenceInfo ref : refList) {
		// 	System.out.println(ref.getKey());
		// 	nodes = ref.getNodes();

		// 	for (ASTNode node: nodes) {
		// 		System.out.println("\tType: " + node.getNodeType() + 
		// 				" -- parent: " + node.getParent());
		// 	}
		// }
  //   }

    // @Test
    // @Ignore
  //   public void testEdits() {
  //   	HashMap<String,CompilationUnit> units = Parser.parse(
		// 		FileUtils.getFiles("test-android/notepad", 
		// 			new String[] {".java", ".JAVA"}),
		// 		new String[]{"lib/android/android-11.jar"},
		// 		new String[] { /* path that contain the source files*/
		// 			"test-android/notepad"
		// 		});

  //   	String[] androidClassKeys;
  //   	try {
		// 	androidClassKeys = 
		// 	FileUtils.getAllLines("android-ui/class.key");
		// } catch (java.io.IOException ex) {
		// 	System.out.println(ex);
		// 	return;
		// }

		
		// for (Entry<String, CompilationUnit> e : units.entrySet()) {
  //   		CompilationUnit u = e.getValue();
  //   		IProblem[] probs = u.getProblems();
  //   		for (IProblem p: probs) {
  //   			System.out.println(p);
  //   		}
    		

  //   		NodeTracker tracker = new ParentTracker(u);
  //   		TypeReferenceVisitor visitor 
  //   				= new TypeReferenceVisitor(androidClassKeys, tracker);

		// 	u.accept(visitor);

		// 	Set<ASTNode> allNodes = tracker.getAllNodes();

		// 	if (allNodes.size() < 2) {
		// 		continue;
		// 	}
				

		// 	RemoverVisitor rVisitor = new RemoverVisitor(allNodes);

		// 	u.recordModifications();
		// 	u.accept(rVisitor);

		// 	//System.out.println("========== " + e.getKey() + " ==========");
		// 	//System.out.println(u);


		// 	String source;
  //   		try {
  //   			source = org.apache.commons.io.FileUtils.readFileToString(
  //   				new java.io.File(e.getKey()));
  //   		} catch (Exception ex) {
  //   			return;
  //   		}

		// 	Document doc = new Document(source);

		// 	TextEdit edits = u.rewrite(doc, null);

		// 	UndoEdit undo = null;

		// 	try {
		// 		undo = edits.apply(doc);

		// 		org.apache.commons.io.FileUtils.writeStringToFile(
		// 			new File(e.getKey().replace("test-android", "test-android-out")),
		// 			doc.get(),  // u.toString(), 
		// 			(String)null);

		// 	} catch(MalformedTreeException ex) {
		// 		ex.printStackTrace();
		// 	} catch(BadLocationException ex) {
		// 		ex.printStackTrace();
		// 	} catch (Exception ex) {
		// 		ex.printStackTrace();
		// 	}
			
		// }
  //   }


  //   @Test
  //   //@Ignore
  //   public void testWindowInits() {

  //   	// 1. build ASTs
  //   	HashMap<String,CompilationUnit> units = Parser.parse(
		// 		FileUtils.getFiles("test-android/notepad", 
		// 			new String[] {".java", ".JAVA"}),
		// 		new String[]{"lib/android/android-11.jar"},
		// 		new String[] { /* path that contain the source files*/
		// 			"test-android/notepad"
		// 		});

  //   	// 2. extract window classes

		// String windowClassKey = "Landroid/app/Activity;";
		// List<AbstractTypeDeclaration> windowClasses = new 
		// 	ArrayList<AbstractTypeDeclaration>();


  //   	for (Entry<String, CompilationUnit> e : units.entrySet()) {
  //   		CompilationUnit u = e.getValue();
  //   		List<AbstractTypeDeclaration> types = u.types();
    		
  //   		ITypeBinding tbind = null;
  //   		for (AbstractTypeDeclaration type : types) {
  //   			tbind = type.resolveBinding();
  //   			if (tbind != null && matchSuperClass(windowClassKey, tbind)) {
  //   				windowClasses.add(type);
  //   			}
  //   		}
  //   	}

  //   	// 3. for each window class, extract init steps

  //   	// Set<String> initMethods = new HashSet<String> ();

  //   	// initMethods.add("onCreate");
  //   	// initMethods.add("onRestart");
  //   	// initMethods.add("onStart");
  //   	// initMethods.add("onResume");

    	
  //   	String[] methodNames = new String[] {
  //   		"onCreate",
  //   		"onRestart",
  //   		"onResume",
  //   		"onStart"
  //   	};

  //   	MethodDeclaration[] methods = new MethodDeclaration[methodNames.length];

  //   	String[] widgetClassKeys;

  //   	try {
		// 	widgetClassKeys = 
		// 	FileUtils.getAllLines("android-ui/widget.key");
		// } catch (java.io.IOException ex) {
		// 	System.out.println(ex);
		// 	return;
		// }

		// TypeReferenceVisitor typerefVisitor = 
		// 		new TypeReferenceVisitor(widgetClassKeys);

  //   	for (AbstractTypeDeclaration wClass : windowClasses) {
  //   		List<BodyDeclaration> bodyDecls = wClass.bodyDeclarations();

  //   		for (BodyDeclaration body : bodyDecls) {
  //   			if (body instanceof MethodDeclaration) {
  //   				MethodDeclaration m = (MethodDeclaration)body;
  //   				String name = m.getName().toString();
  //   				for (int i = 0; i < methods.length; i++) {
  //   					if (name.equals(methodNames[i]))
  //   						methods[i] = m;
  //   				}
    				
  //   			}
  //   		}

  //   		System.out.println(wClass.getName());

  //   		for (int i = 0; i < methods.length; i++) {
  //   			if (methods[i] != null) {

  //   				typerefVisitor.clearReferenceInfo();

  //   				methods[i].accept(typerefVisitor);

  //   				List<ReferenceInfo> found = typerefVisitor.getNonEmpty();

  //   				if (found.size() > 0) {
  //   					System.out.println(" -- " + methodNames[i] 
  //   						+ " ---------------- ");
    					
  //   					for (ReferenceInfo ref : found) {
  //   						System.out.println(" --- " + ref.getKey());
  //   						for (ASTNode node : ref.getNodes()) {
  //   							System.out.println(" ----  " + node);
  //   						}
  //   					}
  //   				}

  //   			}

  //   			methods[i] = null; // reset for next interation
  //   		}
  //   	}
    	
  //   }

    private boolean matchSuperClass(String superKey, ITypeBinding tbind) {
		while (tbind != null && tbind.getKey() != "Ljava/lang/Object;") {
			String key = tbind.getKey();
			if (superKey.equals(key))
				return true;
			tbind = tbind.getSuperclass();
		}
		
		return false;
	}

	@Test
	@Ignore
	public void testMethodDeclarationKey() {
		HashMap<String,CompilationUnit> units = Parser.parse(
				FileUtils.getFiles("test-android/notepad", 
					new String[] {".java", ".JAVA"}),
				new String[]{"lib/android/android-11.jar"},
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

		final HashMap<IMethodBinding, List<ASTNode>> nodeRefs 
			= new HashMap<IMethodBinding, List<ASTNode>>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				IMethodBinding methodBinding = node.resolveBinding();

				if (methodBinding != null && !nodeRefs.containsKey(methodBinding)) {

					List<ASTNode> refs = new ArrayList<ASTNode>();
					//refs.add(node);

					nodeRefs.put(methodBinding, refs);
				}
					
				return true;
			}
		};
		

		for (Entry<String, CompilationUnit> e : units.entrySet()) {
    		CompilationUnit u = e.getValue();
    		//IProblem[] probs = u.getProblems();
    		// for (IProblem p: probs) {
    		// 	System.out.println(p);
    		// }

    		u.accept(visitor);
    	}

    	ASTVisitor visitorMethodRef = new ASTVisitor() {

			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding methodBinding = node.resolveMethodBinding();

				if (methodBinding != null && nodeRefs.containsKey(methodBinding)) {
					nodeRefs.get(methodBinding).add(node);
				}
				return true;
			}
		};

		for (Entry<String, CompilationUnit> e : units.entrySet()) {
    		CompilationUnit u = e.getValue();
    		//IProblem[] probs = u.getProblems();
    		// for (IProblem p: probs) {
    		// 	System.out.println(p);
    		// }

    		u.accept(visitorMethodRef);
    	}

    	for (Entry<IMethodBinding, List<ASTNode>> e : nodeRefs.entrySet()) {
    		IMethodBinding methodBinding = e.getKey();
    		List<ASTNode> nodes = e.getValue();

    		if (nodes.size() > 0) {
    			System.out.println("-------------- " + methodBinding.getKey());

	    		for (ASTNode node : nodes) {
	    			System.out.println(node);
	    		}
	    		System.out.println("============== ");	
    		}
    	}


	}

	// @Test
	// @Ignore
	// public void testMarkingInterestedNode() {

	// 	// 1. parse all units
	// 	HashMap<String,CompilationUnit> units = Parser.parse(
	// 			FileUtils.getFiles("test-android/ast", 
	// 				new String[] {".java", ".JAVA"}),
	// 			new String[]{"lib/android/android-11.jar"},
	// 			new String[] { /* path that contain the source files*/
	// 				"test-android/ast"
	// 			});

 //    	String[] androidClassKeys;
 //    	try {
	// 		androidClassKeys = 
	// 		FileUtils.getAllLines("android-ui/class.key");
	// 	} catch (java.io.IOException ex) {
	// 		System.out.println(ex);
	// 		return;
	// 	}


	// 	// 2. visit all references to interested UI types
	// 	// mark all those references and their ancestor nodes
	// 	String[] keys = androidClassKeys;
	// 	Set<ASTNode> interestingUINodes = new HashSet<ASTNode>();

	// 	ReferenceVisitor refVisitor = new TypeReferenceVisitor(keys);

	// 	while (keys.length > 0) {

	// 		// 2a. find all references to current keys
	// 		for (Entry<String, CompilationUnit> e : units.entrySet()) {
 //    			CompilationUnit u = e.getValue();
    		
	// 			u.accept(refVisitor);
	// 		}

	// 		// 2b. mark these references as newly marked
	// 		Set<ASTNode> newlyMarkedNodes = refVisitor.getAllReferences();


			
	// 		// 2c. find all keys of the containers of the newly marked
	// 		List<String> newKeys = new ArrayList<String>();

	// 		for (ASTNode node : newlyMarkedNodes) {
	// 			// make sure to check only new marked ones
	// 			if (!interestingUINodes.contains(node)) {

	// 				MethodDeclaration containingMethod
	// 					= ASTNodeUtils.getContainingMethod(node);

					
	// 				if (containingMethod != null
	// 					&& !interestingUINodes.contains(containingMethod)) {

	// 					// add the newly marked's containing method
	// 					// interestingUINodes.add(containingMethod);

	// 					IMethodBinding methodBinding = null;
	// 					if ((methodBinding = containingMethod.resolveBinding()) != null) {
	// 						// containing method might be referenced by others
	// 						// add to check another round
	// 						newKeys.add(methodBinding.getKey());

	// 					}
	// 				}
	// 				// add the newly marked and its parents
	// 				addParentNodes(interestingUINodes, node);
	// 			}
	// 		}

	// 		// 2d. reset current keys 
	// 		// keys for next reference search tour
	// 		keys = newKeys.toArray(new String[0]);
	// 		refVisitor = new MethodReferenceVisitor(keys);
	// 	}


	// 	RemoverVisitor removerVisitor = new RemoverVisitor(interestingUINodes);


	// 	for (Entry<String, CompilationUnit> e : units.entrySet()) {
 //    		CompilationUnit u = e.getValue();

	// 		u.recordModifications();
	// 		u.accept(removerVisitor);

	// 		//System.out.println("========== " + e.getKey() + " ==========");
	// 		//System.out.println(u);


	// 		String source;
 //    		try {
 //    			source = org.apache.commons.io.FileUtils.readFileToString(
 //    				new java.io.File(e.getKey()));
 //    		} catch (Exception ex) {
 //    			return;
 //    		}

	// 		Document doc = new Document(source);

	// 		TextEdit edits = u.rewrite(doc, null);

	// 		UndoEdit undo = null;

	// 		try {
	// 			undo = edits.apply(doc);

	// 			org.apache.commons.io.FileUtils.writeStringToFile(
	// 				new File(e.getKey().replace("test-android", "test-android-out")),
	// 				doc.get(),  // u.toString(), 
	// 				(String)null);

	// 		} catch(MalformedTreeException ex) {
	// 			ex.printStackTrace();
	// 		} catch(BadLocationException ex) {
	// 			ex.printStackTrace();
	// 		} catch (Exception ex) {
	// 			ex.printStackTrace();
	// 		}
			
	// 	}
    	
	// }

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
	@Ignore
	public void testSubString() {
		String classMethodNode = "android.view.View#setOnClickListener";

		assertTrue("method extraction failed: "
				 + ASTNodeUtils.getMethodName(classMethodNode),

				 ASTNodeUtils.getMethodName(classMethodNode).equals("setOnClickListener")
				 );


		assertTrue("class extraction failed: "
				 + ASTNodeUtils.getClassName(classMethodNode),

				 ASTNodeUtils.getClassName(classMethodNode).equals("android.view.View")
				 );
	}

	// @Test 
	// @Ignore
	// public void testXMLReader() {
		
	// 	String[] args = new String[] {
	// 		"android-ui/android.xml"
	// 	};

	// 	AndroidUIClassReader structureReader = new AndroidUIClassReader();

	// 	structureReader.parseXML(args);

	// 	final HashMap<String, UIObjectClass>
	// 		androidUIStructures = structureReader.getUIStructures();

	// 	final Set<String> androidUIKeys = androidUIStructures.keySet();

	// 	final HashMap<String, UIEventClass>
	// 		androidUIEvents = structureReader.getUIEvents();		


	// 	// 1. parse all units
	// 	HashMap<String,CompilationUnit> units = Parser.parse(
	// 			FileUtils.getFiles("test-android/todomanager", 
	// 				new String[] {".java", ".JAVA"}),
	// 			new String[]{"lib/android/android-18.jar"},
	// 			new String[] { /* path that contain the source files*/
	// 				"test-android/todomanager"
	// 			});


	// 	class TypeMethodVisitor extends ASTVisitor {

	// 		private HashMap<IMethodBinding, UIEventClass>
	// 			methodEventBindings;

	// 		public TypeMethodVisitor() {
	// 			methodEventBindings = new HashMap<IMethodBinding, UIEventClass>();
	// 		}

	// 		public HashMap<IMethodBinding, UIEventClass> 
	// 			getMethodEventBinding() {

	// 			return methodEventBindings;
	// 		}

	// 		private List<UIEventClass> getEventsByMethodName(
	// 				String methodName, Collection<UIEventClass> eventList) {

	// 			List<UIEventClass> filteredList = 
	// 					new ArrayList<UIEventClass> ();
	// 			for (UIEventClass e : eventList) {
	// 				if (e.methodName.equals(methodName))
	// 					filteredList.add(e);
	// 			}

	// 			return filteredList;
	// 		}

	// 		@Override
	// 		public boolean visit(MethodDeclaration node) {
	// 			String methodName = node.getName().toString();
	// 			IMethodBinding methodBinding = node.resolveBinding();
	// 			List<UIEventClass> filteredList
	// 				= getEventsByMethodName(methodName, androidUIEvents.values());

	// 			if (methodBinding != null 
	// 				&& filteredList.size() > 0) {

	// 				ITypeBinding declaringClass = methodBinding.getDeclaringClass();

	// 				Set<String> superTypeNames
	// 					= getSuperTypeQualifiedNames(declaringClass);

	// 				if (superTypeNames == null)
	// 					return true;

	// 				UIEventClass interestedEvent = null;

	// 				for (UIEventClass event : filteredList) {
	// 					if (superTypeNames.contains(event.classKey)) {
							
	// 						methodEventBindings.put(methodBinding, event);
	// 						break;
	// 					}

	// 				}

	// 			}

	// 			return true;
	// 		}	
	// 	}

	// 	TypeMethodVisitor visitor = new TypeMethodVisitor();


	// 	for (Entry<String,CompilationUnit> e : units.entrySet()) {
	// 		CompilationUnit u = e.getValue();
	// 		u.accept(visitor);
	// 	}

	// 	HashMap<IMethodBinding, UIEventClass> 
	// 		bindings = visitor.getMethodEventBinding();


	// 	for (Entry<IMethodBinding,UIEventClass> e 
	// 			: bindings.entrySet()) {
	// 		IMethodBinding b = e.getKey();
	// 		UIEventClass event = e.getValue();

	// 		System.out.println(b.getKey());
	// 		System.out.println("-- " + event.classKey + "#" + event.methodName
	// 				+ " in " + b.getDeclaringClass().getKey());
			
	// 	}


	// }

	// @Test
	// @Ignore
	// public void testApproach1() {

	// 	// 1. get meta info: Android structures
	// 	AndroidUIClassReader structureReader = new AndroidUIClassReader();

	// 	structureReader.parseXML(
	// 				new String[] {
	// 					"android-ui/android.xml"
	// 				});

	// 	final HashMap<String, UIObjectClass>
	// 		androidUIStructures = structureReader.getUIStructures();


	// 	final HashMap<String, UIEventClass>
	// 		androidUIEvents = structureReader.getUIEvents();		

	// 	final HashMap<String, UIActionClass>
	// 		androidUIActions = structureReader.getUIActions();		



	// 	// 2. parse project
	// 	HashMap<String,CompilationUnit> units = Parser.parse(
	// 			FileUtils.getFiles("test-android/todomanager", 
	// 								new String[] {".java", ".JAVA"}),
	// 			new String[]{"lib/android/android-18.jar"},
	// 			new String[] { /* path that contain the source files*/
	// 				"test-android/todomanager"
	// 			});

	// 	// 3. get all types
	// 	List<ASTNode> typeNodes 
	// 	= ASTNodeUtils.getAllTypeNodes(units.values().toArray(new CompilationUnit[0]));

	// 	// 3a. filter class declaration and local class, 
	// 	// ignore interfaces and anonymous class
	// 	List<TypeDeclaration> realTypeNodes = new ArrayList<TypeDeclaration>();

	// 	for (ASTNode node : typeNodes) {
	// 		if (node instanceof TypeDeclaration) {
	// 			TypeDeclaration typeDecl = (TypeDeclaration)node;
	// 			if (!typeDecl.isInterface())
	// 				realTypeNodes.add(typeDecl);
	// 		}
	// 	}

	// 	// 4. check if there are any classes 
	// 	// that extends any UI structures (windows, widgets, etc)
	// 	List<AndroidUIObject> androidUIObjects = new ArrayList<AndroidUIObject>();

	// 	for (TypeDeclaration realType : realTypeNodes) {
	// 		ITypeBinding realTypeBinding;
	// 		if ((realTypeBinding = realType.resolveBinding()) != null) {
	// 			List<String> superClassQNames 
	// 				= ASTNodeUtils.getSuperClassQualifiedNames(realTypeBinding);
	// 			List<UIObjectClass> androidSuperClasses = null;

	// 			for (String name : superClassQNames) {
	// 				if (androidUIStructures.containsKey(name)) {
	// 					if (androidSuperClasses == null) 
	// 						androidSuperClasses = new ArrayList<UIObjectClass>();

	// 					UIObjectClass superClass = androidUIStructures.get(name);

	// 					androidSuperClasses.add(superClass);
	// 				}
	// 			}

	// 			if (androidSuperClasses != null) {
	// 				androidUIObjects.add(new AndroidUIObject(realType, androidSuperClasses));
	// 			}
	// 		}
	// 	}

		
	// 	for (AndroidUIObject object : androidUIObjects) {
	// 		System.out.println(TestSuit.LONG_DASH);
	// 		System.out.println(object.astSourceNode.getName());


	// 		// find all possible events
	// 		List<String> eventKeys = new ArrayList<String>();
	// 		for (UIObjectClass uiClass : object.superClassInfo) {
	// 			// System.out.println(uiClass.classKey);
	// 			for (UIEventClass e : uiClass.eventsInfo) {
	// 				// for each event that this class might have
	// 				eventKeys.add(e.getKey());


	// 			}
	// 		}

	// 		// find what this object actually has
	// 		HashMap<String,List<MethodDeclaration>>
	// 			foundBaseEvents = 
	// 			ASTNodeUtils.matchAndroidUIMethodByNameAndClass(eventKeys, 
	// 					new ASTNode[] {object.astSourceNode});

	// 		for (Entry<String, List<MethodDeclaration>> e : foundBaseEvents.entrySet()) {
	// 			System.out.println(e.getKey());
	// 			for (MethodDeclaration m : e.getValue()) 
	// 				System.out.println(m.getName());
	// 		}
	// 	}

	// }


	@Test
	@Ignore
	public void testApproach2() {

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

		final HashMap<IMethodBinding, MethodReference>
			methodReferences = new HashMap<IMethodBinding, MethodReference>();

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
					MethodReference mRef;
					if (!methodReferences.containsKey(mBinding)) {
						mRef = new MethodReference();
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
					MethodReference mRef;

					if (!methodReferences.containsKey(mBinding)) {
						mRef = new MethodReference();
						mRef.methodBinding = mBinding;
						methodReferences.put(mBinding, mRef);
					} else {
						mRef = methodReferences.get(mBinding);	
					}

					
					if (mRef.invokedList == null)
						mRef.invokedList = new ArrayList<ActionInvocation>();

					ActionInvocation actionInvoke = new ActionInvocation();
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
		
		EXTERNAL_NON_UI -> isFromSource == false, metaClassInfo == null

		EXTERNAL_UI -> isFromSource == false, metaClassInfo.category == OUTSOURCE

		INTERNAL_INIT -> isFromSource == true, metaClassInfo.category == INSOURCE,
							type = "init"

		INTERNAL_TOP_EVENT, -> isFromSource == true, metaClassInfo.category == INSOURCE,
							type = "top-event"

		INTERNAL_LINKED_EVENT, -> isFromSource == true, metaClassInfo.category == INSOURCE,
							type = "linked-event"

		INTERNAL_APP_DEFINED -> isFromSource == true, metaClassInfo == null


		INTERNAL_INIT and INTERNAL_APP_DEFINED are then filtered again to remove non-UIs
	
			4a. set up metaClassInfo and type
			
		*/	
		List<IMethodBinding> nonUIExternals = new ArrayList<IMethodBinding>();
		List<IMethodBinding> uiExternals = new ArrayList<IMethodBinding>();
		Set<IMethodBinding> allInterestingThings = new HashSet<IMethodBinding>();


		for (Entry<IMethodBinding, MethodReference> e : methodReferences.entrySet()) {

			IMethodBinding methodBind = e.getKey();
			MethodReference mRef = e.getValue();
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
					mRef.type = MethodReference.MethodType.EXTERNAL_UI;

					uiExternals.add(methodBind);
				} else 
					/*  INTERNAL_UI */
					if (declaringClass.isFromSource()
						&& interestingActionClass.category == UIActionClass.UIActionCategory.INSOURCE) {
					switch (interestingActionClass.type) {
						case INIT:
							mRef.type = MethodReference.MethodType.INTERNAL_INIT;

							allInterestingThings.add(methodBind);
							break;
						case TOP_EVENT:
							mRef.type = MethodReference.MethodType.INTERNAL_TOP_EVENT;

							allInterestingThings.add(methodBind);

							break;
						case LINKED_EVENT:
							mRef.type = MethodReference.MethodType.INTERNAL_LINKED_EVENT;

							allInterestingThings.add(methodBind);

							break;
					}
				}

			} else {
				/* EXTERNAL_NON_UI */
				if (declaringClass.isFromSource() == false) {
					mRef.type = MethodReference.MethodType.EXTERNAL_NON_UI;
					nonUIExternals.add(methodBind);
				// can be remove!!!
				}
				else {
				/* INTERNAL, not known yet */
					mRef.type = MethodReference.MethodType.INTERNAL_APP_DEFINED;
					allInterestingThings.add(methodBind);	
				}
				
			}	
		}

		// 4b. remove non-UIs
		// 4b.1. remove EXTERNAL_NON_UI => by ignoring nonUIExternals
		// for (IMethodBinding nonUIExternal : nonUIExternals) {
		// 	methodReferences.remove(nonUIExternal);
		// }

		// 4b.2. identify if any INTERNAL_INIT or INTERNAL_APP_DEFINED is non-UI,
		// those can be ignored
		// INTERAL_TOP_EVENT, INTERNAL_LINKED_EVENT if is non-UI, will be track for 
		// distinguishing non-pure event
		// Set<IMethodBinding> uiInits = new HashSet<IMethodBinding>();
		// Set<IMethodBinding> uiTopEvents = new HashSet<IMethodBinding>();
		// Set<IMethodBinding> uiLinkedEvents = new HashSet<IMethodBinding>();
		// Set<IMethodBinding> uiAppInternals = new HashSet<IMethodBinding>();

		// // a BFS:
		// Queue<IMethodBinding> mQueue = new LinkedList<IMethodBinding>(uiExternals);

		// IMethodBinding current;

		// // a BFS to visit all references (method calls)
		// // while the queue is not empty
		// while ((current = mQueue.poll()) != null) {
		// 	MethodReference meRef = methodReferences.get(current);
		// 	if (meRef != null && meRef.invokedList != null) {
		// 		for (ActionInvocation actInv : meRef.invokedList) {
		// 			// for each invocation of the current method
		// 			// check if the invoker is not yet checked
		// 			if (actInv.invokingMethod != null
		// 				&& allInterestingThings.contains(actInv.invokingMethod)) {
		// 				// get the information of the invoker
		// 				MethodReference mRef = methodReferences.get(actInv.invokingMethod);
		// 				// the invoker is definitely INTERNAL
		// 				// can be INTERNAL_INIT, INTERNAL_TOP_EVENT,
		// 				// INTERNAL_LINKED_EVENT, INTERNAL_APP_DEFINED

		// 				// add invoker to tracked sets
		// 				switch (mRef.type) {
		// 					case INTERNAL_INIT:
		// 						uiInits.add(actInv.invokingMethod);
		// 						break;

		// 					case INTERNAL_TOP_EVENT:
		// 						uiTopEvents.add(actInv.invokingMethod);
		// 						break;

		// 					case INTERNAL_LINKED_EVENT:
		// 						uiLinkedEvents.add(actInv.invokingMethod);
		// 						break;

		// 					case INTERNAL_APP_DEFINED:
		// 						uiAppInternals.add(actInv.invokingMethod);
		// 					// add to tracking set (the queue)
		// 					// only INTERNAL_APP_DEFINED can be possibly called
		// 						mQueue.offer(actInv.invokingMethod);
		// 						break;
		// 				}	

		// 				// remove from the untracked set
		// 				allInterestingThings.remove(actInv.invokingMethod);
		// 			}
		// 		}
		// 	}
		// }

		// after step 4:
		/*
		EXTERNAL_NON_UI -> nonUIExternals

		EXTERNAL_UI -> uiExternals

		INTERNAL_INIT -> uiInits + some in allInterestingThings (non-UI)

		INTERNAL_TOP_EVENT, -> uiTopEvents + some in allInterestingThings (non-UI)

		INTERNAL_LINKED_EVENT, -> uiLinkedEvents + some in all InterestingThings (non-UI)

		INTERNAL_APP_DEFINED -> uiAppInternals + some in all InterestingThings (non-UI)

		allInterestingThings: non-UI inits, top events, linked-events, app-defines
		can remove inits & app-defines

		for each window/widget: use uiInits to identify top-events that are automatically enabled,
		and linked-events that are enabled by the callings of uiExternals

		for each uiTopEvents and uiLinkedEvents, identify that which events are added or removed
			by the callings of uiExternals
		*/
		// class Printer {
		// 	public void print(String mes, Set<ActionInvocation> actList) {
		// 		System.out.print(mes);
		// 		for (ActionInvocation act : actList) {
		// 			System.out.print(act.astSourceNode.getExpression()
		// 				 + "." + act.astSourceNode.getName() + " -> ");
		// 		}
		// 		System.out.println(".");
		// 	}
		// }

		// int i = 0;
		// Printer printer = new Printer();

		for (IMethodBinding method : uiExternals) {
			MethodReference mRef = methodReferences.get(method);

			if (mRef != null && mRef.type == MethodReference.MethodType.EXTERNAL_UI
					//&& mRef.metaClassInfo.type == UIActionClass.UIActionType.BIND_EVENT
					) {

				List<Set<ActionInvocation>> allPaths = new ArrayList<Set<ActionInvocation>>();

				Deque<ActionInvocation> stack = new ArrayDeque<ActionInvocation>();
				Deque<Set<ActionInvocation>> pathStack = new ArrayDeque<Set<ActionInvocation>>();

				Set<ActionInvocation> currentPath;

				// System.out.println(method.getKey());
				// System.out.println("Run start stack");
				for (ActionInvocation act : mRef.invokedList) {
					currentPath = new LinkedHashSet<ActionInvocation>();

					currentPath.add(act);

					stack.addFirst(act);
					pathStack.addFirst(currentPath);	

					// System.out.println("Push " + act.astSourceNode.getExpression() + "." + act.astSourceNode.getName());
					// printer.print("Push ", currentPath);
				}
				

				ActionInvocation currentAct;
				// DFS
				//System.out.println("Run DFS");
				while ((currentAct = stack.peekFirst()) != null) {
					stack.removeFirst();
					currentPath = pathStack.removeFirst();
					

					// System.out.println("Pop " + currentAct.astSourceNode.getExpression() + "." + currentAct.astSourceNode.getName());
					// printer.print("Pop ", currentPath);

					MethodReference invoker = methodReferences.get(currentAct.invokingMethod);

					if (invoker.invokedList == null || invoker.invokedList.size() == 0) {
						// if the invoker is not available: we have reach the sink node
						allPaths.add(currentPath);
					} else {
						for (ActionInvocation invokingAct : invoker.invokedList) {
							if (!currentPath.contains(invokingAct)) {
								Set<ActionInvocation> nextPath 
									= new LinkedHashSet<ActionInvocation>(currentPath);

								nextPath.add(invokingAct);

								stack.addFirst(invokingAct);
								pathStack.addFirst(nextPath);

								// System.out.println("Push " + invokingAct.astSourceNode.getExpression() + "." + invokingAct.astSourceNode.getName());
								// printer.print("Push ", nextPath);
							}
						}
					}
					// System.out.println();
				}

				System.out.println(TestSuit.LONG_DASH);

				// System.out.println("Run");
				for (Set<ActionInvocation> path : allPaths) {
				//	System.out.println("Path " + i++);
					for (ActionInvocation act : path) {
						System.out.println("\t -> " + act.astSourceNode.getExpression()
							 + "." + act.astSourceNode.getName()
							 + " in " + act.invokingMethod.getKey());
					}

					System.out.println(TestSuit.LONG_DASH);
				}




				// Set<MethodReference> uiCallingInits = new HashSet<MethodReference>();
				// Set<MethodReference> uiCallingEvents = new HashSet<MethodReference>();
				// Set<MethodReference> uiVisited = new HashSet<MethodReference>();

				// Queue<MethodReference> externalCallingQueue = new LinkedList<MethodReference>();

				// externalCallingQueue.offer(mRef);

				// MethodReference currentRef;

				// while ((currentRef = externalCallingQueue.poll()) != null) {
				// 	if (!uiVisited.contains(currentRef)) {
				// 		// if it is invoked by others
				// 		if (currentRef.invokedList != null) {
				// 			for (ActionInvocation actInvoke : currentRef.invokedList) {
				// 				MethodReference meRef = methodReferences.get(actInvoke.invokingMethod);

				// 				if (meRef != null && !uiVisited.contains(meRef))
				// 					externalCallingQueue.offer(meRef);
				// 			}
				// 		}

				// 		if (currentRef.metaClassInfo != null) {
				// 			if (currentRef.metaClassInfo.type == UIActionClass.UIActionType.INIT)
				// 				uiCallingInits.add(currentRef);
				// 			else if (currentRef.metaClassInfo.type == UIActionClass.UIActionType.TOP_EVENT
				// 				|| currentRef.metaClassInfo.type == UIActionClass.UIActionType.LINKED_EVENT)
				// 			uiCallingEvents.add(currentRef);	
				// 		}
						

				// 		// visited
				// 		uiVisited.add(currentRef);
				// 	}
				// }

				// System.out.println("Event Binder: " + method.getKey());

				// for (MethodReference callEventRef : uiCallingEvents)
				// 	System.out.println("Event: " + callEventRef.methodBinding.getKey());

				// for (MethodReference callInitRef : uiCallingInits)
				// 	System.out.println("Init: " + callInitRef.methodBinding.getKey());

				// System.out.println(TestSuit.LONG_DASH);
			}
		}
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

		final HashMap<IMethodBinding, MethodReference>
			methodReferences = new HashMap<IMethodBinding, MethodReference>();

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
					MethodReference mRef;
					if (!methodReferences.containsKey(mBinding)) {
						mRef = new MethodReference();
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
					MethodReference mRef;

					if (!methodReferences.containsKey(mBinding)) {
						mRef = new MethodReference();
						mRef.methodBinding = mBinding;
						methodReferences.put(mBinding, mRef);
					} else {
						mRef = methodReferences.get(mBinding);	
					}

					
					if (mRef.invokedList == null)
						mRef.invokedList = new ArrayList<ActionInvocation>();

					ActionInvocation actionInvoke = new ActionInvocation();
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


		for (Entry<IMethodBinding, MethodReference> e : methodReferences.entrySet()) {

			IMethodBinding methodBind = e.getKey();
			MethodReference mRef = e.getValue();
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
					mRef.type = MethodReference.MethodType.EXTERNAL_UI;

					uiExternals.add(methodBind);
				} else 
					/*  INTERNAL_UI */
					if (declaringClass.isFromSource()
						&& interestingActionClass.category == UIActionClass.UIActionCategory.INSOURCE) {
					switch (interestingActionClass.type) {
						case INIT:
							mRef.type = MethodReference.MethodType.INTERNAL_INIT;
							break;
						case TOP_EVENT:
							mRef.type = MethodReference.MethodType.INTERNAL_TOP_EVENT;
							break;
						case LINKED_EVENT:
							mRef.type = MethodReference.MethodType.INTERNAL_LINKED_EVENT;
							break;
					}
				}

			} else {
				/* EXTERNAL_NON_UI */
				if (declaringClass.isFromSource() == false) {
					mRef.type = MethodReference.MethodType.EXTERNAL_NON_UI;
				// can be remove!!!
				}
				else {
				/* INTERNAL, not known yet */
					mRef.type = MethodReference.MethodType.INTERNAL_APP_DEFINED;
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
			MethodReference mRef = methodReferences.get(method);

			if (mRef != null && mRef.type == MethodReference.MethodType.EXTERNAL_UI) {


				Deque<ActionInvocation> stack = new ArrayDeque<ActionInvocation>();
				Deque<Set<ActionInvocation>> pathStack = new ArrayDeque<Set<ActionInvocation>>();

				Set<ActionInvocation> currentPath;

				for (ActionInvocation act : mRef.invokedList) {
					currentPath = new LinkedHashSet<ActionInvocation>();

					currentPath.add(act);

					stack.addFirst(act);
					pathStack.addFirst(currentPath);	
				}
				

				ActionInvocation currentAct;
				// DFS
				while ((currentAct = stack.peekFirst()) != null) {
					stack.removeFirst();
					currentPath = pathStack.removeFirst();
				
					MethodReference invoker = methodReferences.get(currentAct.invokingMethod);

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
										HashMap<IMethodBinding,List<Set<ActionInvocation>>>();

								if (!uiO.initPaths.containsKey(invoker.methodBinding))
									uiO.initPaths.put(invoker.methodBinding,
											new ArrayList<Set<ActionInvocation>>());

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
										HashMap<IMethodBinding,List<Set<ActionInvocation>>>();

								if (!uiO2.topEventPaths.containsKey(invoker.methodBinding))
									uiO2.topEventPaths.put(invoker.methodBinding,
											new ArrayList<Set<ActionInvocation>>());

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
										HashMap<IMethodBinding,List<Set<ActionInvocation>>>();

								if (!uiEObj.eventPaths.containsKey(invoker.methodBinding))
									uiEObj.eventPaths.put(invoker.methodBinding,
											new ArrayList<Set<ActionInvocation>>());

								uiEObj.eventPaths.get(invoker.methodBinding).add(currentPath);
								break;
						}

					} else {
						for (ActionInvocation invokingAct : invoker.invokedList) {
							if (!currentPath.contains(invokingAct)) {
								Set<ActionInvocation> nextPath 
									= new LinkedHashSet<ActionInvocation>(currentPath);

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
			MethodReference mRef = methodReferences.get(method);

			if (mRef != null && mRef.type == MethodReference.MethodType.EXTERNAL_UI) {

				for (ActionInvocation act : mRef.invokedList) {

					List<Expression> args = act.astSourceNode.arguments();
					ITypeBinding argTypeBinding;
					
					for (Expression exp : args) {
						argTypeBinding = exp.resolveTypeBinding();
						if (argTypeBinding != null 
							&& uiEventObjs.containsKey(argTypeBinding)) {
							UIObject.UILinkedEventObject obj =
								uiEventObjs.get(argTypeBinding);

							if (obj.setters == null)
								obj.setters = new HashSet<ActionInvocation>();

							obj.setters.add(act);
						}
					}
				}
			}
		}	
		

		for (UIObject.UILinkedEventObject uiLinkedEventObj : uiEventObjs.values()) {
			for (ActionInvocation setter : uiLinkedEventObj.setters) {
				// find which Object's init method that make the set.
				for (UIObject uiO : uiObjs.values()) {
					for (List<Set<ActionInvocation>> initMethodPath : uiO.initPaths.values()) {
						for (Set<ActionInvocation> initPath : initMethodPath) {
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
			public void print(String mes, Set<ActionInvocation> actList) {
				System.out.print(mes);
				for (ActionInvocation act : actList) {
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
				for (Entry<IMethodBinding, List<Set<ActionInvocation>>> e : 
									obj.initPaths.entrySet()) {
				System.out.println("\t -> " + e.getKey().getKey());

				for (Set<ActionInvocation> initMethodPath : e.getValue())
					printer.print("\t\t |- ", initMethodPath);
				}
				System.out.println(TestSuit.SHORT_DASH);
			}
			
			if (obj.topEventPaths != null) {
				System.out.println("\t -> TOPEVENTS");
				for (Entry<IMethodBinding, List<Set<ActionInvocation>>> e : 
										obj.topEventPaths.entrySet()) {
					System.out.println("\t -> " + e.getKey().getKey());

					for (Set<ActionInvocation> topEventMethodPath : e.getValue())
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

					for (Entry<IMethodBinding, List<Set<ActionInvocation>>> e : 
											ieObj.eventPaths.entrySet()) {
						System.out.println("\t -> " + e.getKey().getKey());

						for (Set<ActionInvocation> eventMethodPath : e.getValue())
							printer.print("\t\t |- ", eventMethodPath);
					}

					System.out.println(TestSuit.SHORT_DASH);

					System.out.println("\t Set up by:");
					for (ActionInvocation setter : ieObj.setters) {
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

			for (Entry<IMethodBinding, List<Set<ActionInvocation>>> e : 
									obj.eventPaths.entrySet()) {
				System.out.println("\t -> " + e.getKey().getKey());

				for (Set<ActionInvocation> eventMethodPath : e.getValue())
					printer.print("\t\t |- ", eventMethodPath);

				
			}
			System.out.println(TestSuit.SHORT_DASH);

			System.out.println("\t Set up by:");
			for (ActionInvocation setter : obj.setters) {
				System.out.println("\t\t " + setter.astSourceNode.getExpression()
						 + "." + setter.astSourceNode.getName());
			}

			System.out.println(TestSuit.LONG_DASH);
		}
	}
}