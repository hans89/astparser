package astparser;

import astparser.UIModel.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;

/**
 *	This class is used to read the interesting lists of UI classes and methods
 *	of a UI framework, defined in an XML file format.
 *
 *	The XML file starts with a list of all possible actions (methods) with the
 *	"action" tag, then a list of all UI objects (classes, tag "window", "widget"
 *	"dialog" or "menu") that link to the actions using tag "action-ref"
 *
 *	The inner class UIStructureXMLHandler extends the SAX ML reader listener
 *	DefaultHandler.
 *	
 *	The reader extracts 2 maps:
 *		- map from class key to UIObjectClass, which also contains list of actions
 *		- map from method key to UIActionClass
 *	The reader also binds the event actions (UIActionLinkedEventClass)
 *	 and event binder actions (UIActionEventBinderClass)
 *
 */
public class AndroidUIClassReader {

	private HashMap<String,UIObjectClass> structures;
	private HashMap<String,UIActionClass> actions;

	public AndroidUIClassReader() {
		structures = new HashMap<String,UIObjectClass>();
		actions = new HashMap<String,UIActionClass>();
	}

	public HashMap<String, UIObjectClass> getUIStructures() {
		return structures;
	}

	public HashMap<String, UIActionClass> getUIActions() {
		return actions;
	}

	public void parseXML(String[] paths) {
		
		structures = new HashMap<String,UIObjectClass>();
		actions = new HashMap<String,UIActionClass>();

		UIStructureXMLHandler handler 
			= new UIStructureXMLHandler(structures, actions);

		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();

			
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);


			for (int i = 0; i < paths.length; i++) {
				FileReader r = new FileReader(paths[i]);
		    	xr.parse(new InputSource(r));	
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}


	public static class UIStructureXMLHandler extends DefaultHandler {

		private UIObjectClass currentUI;
		private UIActionClass currentAction;

		private HashMap<String,UIObjectClass> structures;
		private HashMap<String,UIActionClass> actions;

		public UIStructureXMLHandler(
					HashMap<String, UIObjectClass> s,
					HashMap<String, UIActionClass> a) {
			structures = s;
			actions = a;
		}

		public void startDocument() throws SAXException {
			System.out.println("start doc");
		}

		public void endDocument() throws SAXException {
			System.out.println("end doc");

			// resolve actions and events binding
			for (Entry<String, UIActionClass> e : actions.entrySet()) {
				String actKey = e.getKey();
				UIActionClass act = e.getValue();

				if (act instanceof UIActionLinkedEventClass) {
					UIActionLinkedEventClass linkedEvent 
						= (UIActionLinkedEventClass) act;

					if (actions.containsKey(linkedEvent.setterKey)) {
						UIActionEventBinderClass binder = 
							(UIActionEventBinderClass)actions.get(linkedEvent.setterKey);

						linkedEvent.setterAction = binder;
						if (binder.linkedEventList == null)
							binder.linkedEventList 
								= new ArrayList<UIActionLinkedEventClass>();
						binder.linkedEventList.add(linkedEvent);
					}
				}
			}
		} 

		/*
		 * When the parser encounters plain text (not XML elements),
		 * it calls(this method, which accumulates them in a string buffer
		 */
		public void characters(char[] buffer, int start, int length) {
			
		}


		/*
		 * Every time the parser encounters the beginning of a new element,
		 * it calls this method, which resets the string buffer
		 */ 
		public void startElement(String uri, String localName,
				String qName, Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase("actions")) {
				// nothing
			} else if (qName.equalsIgnoreCase("window")) {

				currentUI = new UIObjectClass();
				currentUI.uiType = UIObjectClass.UIType.Window;
				currentUI.classKey = attributes.getValue("class");

			} else if (qName.equalsIgnoreCase("widget")) {

				currentUI = new UIObjectClass();
				currentUI.uiType = UIObjectClass.UIType.Widget;
				currentUI.classKey= attributes.getValue("class");

			} else if (qName.equalsIgnoreCase("dialog")) {

				currentUI = new UIObjectClass();
				currentUI.uiType = UIObjectClass.UIType.Dialog;
				currentUI.classKey = attributes.getValue("class");

			} else if (qName.equalsIgnoreCase("action")) {

				if (attributes.getValue("setter") != null) {
					currentAction = new UIActionLinkedEventClass();
					((UIActionLinkedEventClass)currentAction).setterKey
						= attributes.getValue("setter");
					currentAction.type = UIActionClass.UIActionType.LINKED_EVENT;
				} else if (attributes.getValue("type") != null
						&& attributes.getValue("type").equals("event-binder")) {
					currentAction = new UIActionEventBinderClass();
					currentAction.type = UIActionClass.UIActionType.BIND_EVENT;
				}
				else
					currentAction = new UIActionClass();


				currentAction.methodName = attributes.getValue("method");
				currentAction.classKey = attributes.getValue("class");

				currentAction.category = 
					attributes.getValue("category").equalsIgnoreCase("INSOURCE")
					? UIActionClass.UIActionCategory.INSOURCE 
					: UIActionClass.UIActionCategory.OUTSOURCE;

				/*
				INIT,   init
				TOP_EVENT,  top-event
				LINKED_EVENT, linked-event
				BIND_EVENT, event-binder
				START_MODAL, start-modal
				END_MODAL,	end-modal
				OPEN_MENU,	open-menu
				ENABLE_WIDGET, enable
				*/

				String actionType = attributes.getValue("type");

				if (actionType != null) {
					if (actionType.equalsIgnoreCase("init"))
						currentAction.type = UIActionClass.UIActionType.INIT;
					else if (actionType.equalsIgnoreCase("top-event"))
						currentAction.type = UIActionClass.UIActionType.TOP_EVENT;
					else if (actionType.equalsIgnoreCase("linked-event"))
						currentAction.type = UIActionClass.UIActionType.LINKED_EVENT;
					else if (actionType.equalsIgnoreCase("event-binder"))
						currentAction.type = UIActionClass.UIActionType.BIND_EVENT;
					else if (actionType.equalsIgnoreCase("start-modal"))
						currentAction.type = UIActionClass.UIActionType.START_MODAL;
					else if (actionType.equalsIgnoreCase("end-modal"))
						currentAction.type = UIActionClass.UIActionType.END_MODAL;
					else if (actionType.equalsIgnoreCase("open-menu"))
						currentAction.type = UIActionClass.UIActionType.OPEN_MENU;
					else if (actionType.equalsIgnoreCase("enable"))
						currentAction.type = UIActionClass.UIActionType.ENABLE_WIDGET;
				}
			} else if (qName.equalsIgnoreCase("action-ref")) {

				currentAction = actions.get(attributes.getValue("id"));
			}
						
		}

		/*
		 * When the parser encounters the end of an element, it calls this method
		 */
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase("window")
				|| qName.equalsIgnoreCase("dialog")
				|| qName.equalsIgnoreCase("widget")) {
				
				structures.put(currentUI.classKey, currentUI);
				currentUI = null;

			} else if (qName.equalsIgnoreCase("action")) {
				if (currentUI != null)
					currentUI.actionsInfo.add(currentAction);
				
				actions.put(currentAction.getKey(), currentAction);

				currentAction = null;

			} else if (qName.equalsIgnoreCase("action-ref")) {
				if (currentUI != null && currentAction != null) {
					currentUI.actionsInfo.add(currentAction);
				}

				currentAction = null;
			}
		}

	}
}