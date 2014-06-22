package leo.ContiguityTree;

import java.util.ArrayList
import java.util.Collection;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	
	//Various Constructors
	OrderedGroup (boolean rev) {
		subTasks = new ArrayList<Task>();
		ordered = true;
		reversible = rev;
	}
	
	OrderedGroup (Collection <Task> col, boolean rev) {
		this(rev);
		copyCollection(col);
	}
	
}
