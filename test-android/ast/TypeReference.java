package astparser.tests

import android.view.View;
import android.widget.*;

public class TypeReference {
	/* field decls */
	private View aView;
	private View aButton;

	public TypeReference(View v /* formal args */, 
				HashMap<String, Button> strButtons /* parameterized type ref */) { 
		/* assignment */
		aView = v; 
		/* assignment, constructor, casting */
		aButton = new Button();

		if (strButtons.get("key") != null) {
			strButtons.get("key").setOnClickListener(
				new OnClickListener() {
					public void onClick(View view) {
						((Button)view).setText("abc");
					}
				}
			);
		}
	}

	public Button getButton(View[] views /* array type ref */) {
		/* super class method invoke */
		view[0].setEnabled(true);
		view[0].setVisibility(View.VISIBLE);

		/* instanceof is what? */
		if (view[0] instanceof Button) {
			/* casting, subclass method invoke */
			((Button)view[0]).setVisibility(View.VISIBLE);
			/* casting, assignment */
			aButton = ((Button)view[0]);
		}

		/* local var, casting */
		Button a = (Button) aButton;
		/* return statement */
		return a;
	}
}