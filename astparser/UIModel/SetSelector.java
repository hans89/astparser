package astparser.UIModel;

import java.util.*;

public class SetSelector<S> {
	private List<List<S>> alphabetSets;
	private List<List<S>> selectionSet;
	private int[] ks;
	private int[] kMultiples;
	private int NumOfFields;
	private int No;

	public SetSelector(List<List<S>> alphabetSets) {
		this.alphabetSets = alphabetSets;
		
		NumOfFields = alphabetSets.size();

		ks = new int[NumOfFields];
		kMultiples = new int[NumOfFields + 1];

		for (int i = 0; i < NumOfFields; i++) {
			ks[i] = alphabetSets.get(i).size();
		}

		kMultiples[NumOfFields] = 1;
		for (int i = NumOfFields - 1; i >= 0; i--) {
			kMultiples[i] = kMultiples[i+1] * ks[i];
		}

		No = kMultiples[0];
	}

	public List<List<S>> getSelectionSet() {
		if (selectionSet == null) {
			selectionSet = new ArrayList<List<S>>(No);

			S sel;

			for (int i = 0; i < No; i++) {
				selectionSet.add(new ArrayList<S>(NumOfFields));

				List<S> selection = selectionSet.get(i);
				for (int j = 0; j < NumOfFields; j++) {
					sel = alphabetSets.get(j).get((i % kMultiples[j]) / kMultiples[j+1]);
					selection.add(sel);
				}
			}
		}

		return selectionSet;
	}
}

