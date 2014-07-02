package contiguityTree;

import java.util.ArrayList;
import java.util.List;

//Tree responsible for storing contiguity rules for a set of items
public class ContiguityTree {
	Task head;

	public ContiguityTree () {
		
	}
	
	//Take in a list of unlabeled primitive actions
	public void encorporate (List<Task> demo){
		if (head==null) {
			head = new OrderedGroup(new Label(), null, demo, false); //null parent, false because sequence is not reversible
		}
		else {
			head.encorporate(demo);
			if (demo.size()!=1) throw new Error ("Tree did not reduce the list to a single Group");
			head = demo.get(0);
		}
	}
	
	public void observeDemo (List<Object> demo) {
		List<Task> wrappedDemo = new ArrayList<Task>(demo.size());
		for (Object obj : demo){
			wrappedDemo.add(new Primitive(new Label(), null, obj)); //null parents for now
		}
		encorporate(wrappedDemo);
	}
	
	public void observeDemo (Object[] demo) {
		List<Task> wrappedDemo = new ArrayList<Task>(demo.length);
		for (Object obj : demo){
			wrappedDemo.add(new Primitive(new Label(), null, obj)); //null parents for now
		}
		encorporate(wrappedDemo);
	}
	
	public void print() {
		head.printMe(0);
	}
	
}


