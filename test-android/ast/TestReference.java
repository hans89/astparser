package astparser.tests;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import java.util.HashMap;


public class TestReference {
	private TypeReference t = new TypeReference();

	public void test1() {
		
		t.testIndirectCall();
		
	}

	public void test2()  {
		t.testDirectCall();
	}

	public void test3() {
		int x,y,z;
		y = x + z;
		z = x + y;
	}
}