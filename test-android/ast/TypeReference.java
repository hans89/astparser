// package astparser.tests;

// import android.view.View;
// import android.view.View.OnClickListener;
// import android.widget.*;
// import java.util.HashMap;

// public class TypeReference implements View.OnClickListener {
// 	/* field decls */
// 	private View aView;
// 	private View aButton;

// 	public TypeReference(View v /* formal args */, 
// 				HashMap<String, Button> strButtons /* parameterized type ref */) { 
// 		/* assignment */
// 		aView = v; 
// 		/* assignment, constructor, casting */
// 		// aButton = new Button();



// 		MyOnClickListener myClick = new MyOnClickListener();
// 		if (strButtons.get("key") != null) {
// 			strButtons.get("key").setOnClickListener(
// 				new OnClickListener() {
// 					public void onClick(View view) {
// 						((Button)view).setText("abc");
// 						view.setVisibility(View.VISIBLE);
// 					}
// 				}
// 			);

// 			strButtons.get("key").setOnClickListener(
// 				(View.OnClickListener) null
// 			);

// 			strButtons.get("key").setOnClickListener(
// 				getListener()
// 			);
// 		}
// 	}

// 	private View.OnClickListener sNullListener;

// 	private View.OnClickListener sNullListener2 = new View.OnClickListener({
// 		public void onClick(View v) {

// 		}
// 	});

// 	public static View.OnClickListener getListener() {
// 		if (sNullListener == null) {
// 			sNullListener = new View.OnClickListener() {
// 				public void onClick(View v) {

// 				}
// 			}
// 		}
// 		return sNullListener;
// 	}

// 	@Override
// 	public void onClick(View view) {
// 		((Button)view).setText("abc");
// 		view.setVisibility(View.VISIBLE);
// 	}

// 	class MyOnClickListener implements View.OnClickListener {
// 		@Override
// 		public void onClick(View view) {
// 			((Button)view).setText("abc");
// 			view.setVisibility(View.VISIBLE);
// 		}
// 	}

// 	public Button getButton(View[] views /* array type ref */) {
// 		/* super class method invoke */
// 		views[0].setEnabled(true);
// 		views[0].setVisibility(View.VISIBLE);

// 		/* instanceof is what? */
// 		if (views[0] instanceof Button) {
// 			/* casting, subclass method invoke */
// 			((Button)views[0]).setVisibility(View.VISIBLE);
// 			/* casting, assignment */
// 			aButton = ((Button)views[0]);
// 		}

// 		/* local var decl, casting */
// 		Button a = (Button) aButton;
// 		/* return statement */
// 		return a;
// 	}

// 	public void testIndirectCall() {
// 		testDirectCall();
// 	}

// 	public void testDirectCall() {
// 		View[] views = new Views[5];

// 		int x = x + 2 * x;

// 		Button k = getButton(views);
// 	}
// }