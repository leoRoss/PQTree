package contiguityTree;

import java.util.Collection;
import java.util.HashSet;

//Group of items that always appear contiguously, with no hard ordering
public class UnOrderedGroup <T> extends Group {
	
	public UnOrderedGroup (Label label, Task parent) {
		super(label, parent);
		subTasks = new HashSet<Task>();
		ordered = false;
	}
	
	public UnOrderedGroup (Label label, Task parent, Collection <Task> col) {
		this(label, parent);
		copyCollection(col);
	}
}
