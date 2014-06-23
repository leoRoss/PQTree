package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	
	//Various Constructors
	public OrderedGroup (Label label, Task parent, boolean rev) {
		super(label, parent);
		subTasks = new ArrayList<Task>();
		ordered = true;
		reversible = rev;
	}
	
	public OrderedGroup (Label label, Task parent, boolean rev,  Collection <Task> col) {
		this(label, parent, rev);
		copyCollection(col);
	}
	
}
