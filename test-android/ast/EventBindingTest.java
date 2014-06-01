package astparser.tests;

import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;


public class EventBindingTest implements View.OnClickListener,
	AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {


	public void onClick(View view) {
		ListView list = (ListView)view;
		list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView parent, View v, int position, long id) {
        
    		}
		});

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
        
   			}
		});
	}
	
	public void test1(Button button) {
		button.setOnClickListener(this);
		button.setOnClickListener((View.OnClickListener)this);
	}

	public void onItemSelected(AdapterView parent, View v, int position, long id) {
        
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        
    }


	public static class OtherClass implements View.OnClickListener {
		
		public void onClick(View view) {
			((TextView)view).setText("OtherClass");
		}

		public void test2(Button button) {
			button.setOnClickListener(OtherClass.this);
			button.setOnClickListener((View.OnClickListener)OtherClass.this);
		}
	}
	
	public void test3(Button button) {
		button.setOnClickListener(new EventBindingTest());
		button.setOnClickListener((View.OnClickListener)new OtherClass());
	}

	public void test4(Button button) {
		button.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				((TextView)view).setText("test4");
			}
		});
	}

	public void test5(Button button) {
		button.setOnClickListener(null);
		button.setOnClickListener((View.OnClickListener)null);
	}

	class MyOnClickListener implements View.OnClickListener {
		
		public void onClick(View v) {
			((TextView)v).setText("MyOnClickListener");
		}
	}

	public void test6(Button button) {
		MyOnClickListener myClick = new MyOnClickListener();
		button.setOnClickListener(myClick);
	}

	public void test7(Button button) {
		View.OnClickListener myClick = new MyOnClickListener();
		button.setOnClickListener(myClick);
	}

	View.OnClickListener myListener = new View.OnClickListener() {
		
		public void onClick(View v) {

		}
	};

	View.OnClickListener myListener2 = null;

	View.OnClickListener myListener3 = new MyOnClickListener();
	public void test8(Button button) {
		button.setOnClickListener(myListener);
		button.setOnClickListener(this.myListener);
		button.setOnClickListener(this.myListener2);
		button.setOnClickListener(this.myListener3);
	}

	View.OnClickListener myListener4 = null;

	public View.OnClickListener getListener() {
		if (myListener4 == null) {
			myListener4 = new OnClickListener() {
				
				public void onClick(View v) {

				}
			};
		}

		return myListener4;
	}

	public void test9(Button button) {
		button.setOnClickListener(getListener());
	}

	public View.OnClickListener getListener2() {

		return new View.OnClickListener() {
				
				public void onClick(View v) {
					((TextView)v).setText("getListener2");
				}
			};
	}

	public void test10(Button button) {
		button.setOnClickListener(getListener2());
	}
}