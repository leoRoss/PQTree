package leo.ContiguityTree;

import java.util.Collection;
import java.util.HashSet;

//Group of items that always appear contiguously, with no hard ordering
public class UnOrderedGroup <T> extends Group {

	UnOrderedGroup () {
		subTasks = new HashSet<Task>();
		ordered = false;
		reversible = null;
	}
	
	UnOrderedGroup (Collection <Task> col) {
		this();
		copyCollection(col);
	}
	
}
