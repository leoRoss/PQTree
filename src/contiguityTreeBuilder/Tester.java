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
		
		List<Task> list1 = new ArrayList<Task>(4);
		//tester.addToList(list1, "A B C D E F G H I J K L M N O");
		tester.addToList(list1, "A B C D");
		//tester.addToList(list1, "A B C D E F G H I J K L M N O");
		List<Task> list2 = new ArrayList<Task>(4);
		//tester.addToList(list2, "J H K I L M O N A B C G F E D");
		tester.addToList(list2, "A C D B");
		//tester.addToList(list2, "A D C B X Y E G F Z W N M L Q");
		
		
		ContiguityTreeBuilder builder = new ContiguityTreeBuilder();
		builder.buildContiguityTree(list1, list2);
		tester.printLists(list1, list2);
		System.out.println();
		System.out.println();
		System.out.println();
		//tester.printLists(list1, list2);
		//builder.printBooks();
	}
	
	private void addToList(List<Task> list, String str){
		String [] primitives = str.split(" ");
		for (int i=0; i<primitives.length; i++){
			list.add(new Primitive(primitives[i])); 
		}
	}
	
	private void printLists(List<Task>...lists){
		for (List<Task> list : lists) {
			System.out.print("      :: ");
			for(int j=0; j<list.size(); j++){
				System.out.print(String.format("%2s | ", ((Primitive)list.get(j)).getObject()));
			}
			System.out.println();
		}
	}

}
