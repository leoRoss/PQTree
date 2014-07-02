package contiguityTreeBuilder;

import java.util.ArrayList;

import java.util.List;

import contiguityTree.*;

public class Tester {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Tester tester  = new Tester();
//		List<String> list1 = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
//		List<String> list2 = Arrays.asList("E", "F", "D", "C", "H", "J", "G", "I", "A", "B");
		List<Task> list1 = new ArrayList<Task>(4);
		list1.add(new Primitive("A"));
		list1.add(new Primitive("B"));
		list1.add(new Primitive("C"));
		list1.add(new Primitive("D"));
		List<Task> list2 = new ArrayList<Task>(4);
		list2.add(new Primitive("A"));
		list2.add(new Primitive("B"));
		list2.add(new Primitive("D"));
		list2.add(new Primitive("C"));
		ContiguityTreeBuilder builder = new ContiguityTreeBuilder();
		builder.buildTree(list1, list2);
		tester.printLists(list1, list2);
		builder.printShiftedRange();
		System.out.println();
		System.out.println();
		System.out.println();
		tester.printLists(list1, list2);
		builder.printBooks();
	}
	
	public void printLists(List<Task>...lists){
		for (List<Task> list : lists) {
			System.out.print("      :: ");
			for(int j=0; j<list.size(); j++){
				System.out.print(String.format("%2s | ", ((Primitive)list.get(j)).getObject()));
			}
			System.out.println();
		}
	}

}
