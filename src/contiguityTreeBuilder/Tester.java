package contiguityTreeBuilder;

import java.util.Arrays;
import java.util.List;

public class Tester {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Tester tester  = new Tester();
		List<String> list1 = Arrays.asList("A", "B", "C", "D");
		List<String> list2 = Arrays.asList("D", "C", "A", "B");
		ContiguityTreeBuilder<String> builder = new ContiguityTreeBuilder<String>();
		builder.buildTree(list1, list2);
		tester.printLists(list1, list2);
		builder.printShiftedRange();
	}
	
	public void printLists(List<String>...lists){
		for (List<String> list : lists) {
			System.out.print("      :: ");
			for(int j=0; j<list.size(); j++){
				System.out.print(String.format("%2s | ", list.get(j)));
			}
			System.out.println();
		}
	}

}
