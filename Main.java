package astparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello ASTParser");

		class StringList {
			public String value;
			public StringList next;

			public StringList(String v, StringList n) {
				value = v;
				next = n;
			}

			public StringList(String v) {
				value = v;
				next = null;
			}

			@Override
			public String toString() {
				String s = value;
				if (next != null)
					s += " -> " + next.toString();
				return s;
			}
		}

		Set<StringList> map = new HashSet<StringList>();
		StringList strEnd = new StringList("end");
		StringList prev, next = null;
		int n = 5;
		prev = new StringList(Integer.toString(n), strEnd);
		map.add(prev);
		map.add(strEnd);
		for (int i = n-1; i >0; i--) {
			next = new StringList(Integer.toString(i), prev);
			map.add(next);
			prev = next;
		}

		StringList strStart = new StringList("start", next);
		map.add(strStart);
		
		for (StringList sList : map) {
			sList.value += "x";
		}

		System.out.println(strStart);
	}
}
