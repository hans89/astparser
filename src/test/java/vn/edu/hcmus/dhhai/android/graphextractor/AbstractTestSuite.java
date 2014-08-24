package vn.edu.hcmus.dhhai.android.graphextractor;

/* junit packages */
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Abstract TestSuite for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
//@RunWith(JUnit4.class)
public abstract class AbstractTestSuite {

	public static final String LONG_DASH = "------------------------------------";

	public static final String SHORT_DASH = "---------";

	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName()
	      	 + " .............");
	   }
	};
}
