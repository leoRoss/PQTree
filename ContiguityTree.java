package leo.ContiguityTree;

import java.util.ArrayList;
import java.util.List;

//Tree responsible for storing contiguity rules for a set of items
public class ContiguityTree {
	Task head;
	
	//Take in a list of unlabeled primitive actions
	public void encorporate (List<Task> demo){
		if (head==null) {
			head = new OrderedGroup(demo, false); //false because sequence is not reversible
		}
		else {
			List <Task> newTree = head.encorporate(demo);
			if (newTree.size()!=1) throw new Error ("Tree did not reduce the list to a single Group");
			head = newTree.get(0);
		}
	}
	
	
	public List<Task> observeDemo (List<Object> demo) {
		List<Task> wrappedDemo = new ArrayList<Task>(demo.size());
		for (Object obj : demo){
			wrappedDemo.add(new Primitive(obj));
		}
		return wrappedDemo;
	}
	
}
