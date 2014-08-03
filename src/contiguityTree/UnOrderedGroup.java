package contiguityTree;

import java.util.Collection;
import java.util.List;

//Group of items that always appear contiguously, with no hard ordering
public class UnOrderedGroup extends Group {
	
	public UnOrderedGroup (Label label, Task parent, int size) {
		super(label, parent, size);	
	}
	
	public UnOrderedGroup (Label label, Task parent, Collection <Task> col) {
		this(label, parent, col.size());
		copyCollection(col);
	}
	
	protected void addTask (Task task) {subTasks.add(task);}
	
	public void encorporateChildren (List<Task>demo){
		for (Task subTask : subTasks) {
			subTask.encorporate(demo);
		}
	}
	
	protected boolean sameType (Task task) {return task instanceof UnOrderedGroup;}
	protected String name () {return "Unordered Group";}
	public boolean isOrdered () {return false;}
	public boolean isReversible () {return false;}
}
