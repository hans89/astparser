package vn.edu.hcmus.dhhai.android.graphextractor;

/* junit packages */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import vn.edu.hcmus.dhhai.android.graphextractor.UIModel.SetSelector;

import java.util.*;

/**
 * SetSelection TestSuite for {@link astparser}.
 *
 * @author dhhai.uns@gmail.com (Hai Dang)
 */
@RunWith(JUnit4.class)
public class SetSelectionTestSuite extends AbstractTestSuite {

	@Test
	public void testSetSelection() {
		List<Integer> bins = new ArrayList<Integer>();

		int radix = 10;
		for (int i = 0; i < radix; i++)
			bins.add(i);

		List<List<Integer>> binNums = new ArrayList<List<Integer>>();

		int numBit = 3;
		for (int i = 0; i < numBit; i++)
			binNums.add(bins);
		

		SetSelector<Integer> selector = new SetSelector<Integer>(binNums);

		List<List<Integer>> selections = selector.getSelectionSet();

		for (List<Integer> selection : selections) {
			for (Integer digit : selection)
				System.out.print(digit);
			System.out.println();
		}
	}
}
