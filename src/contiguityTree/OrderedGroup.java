package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	private ArrayList<Task> orderedSubTasks;
	protected Boolean reversible;
	
	//Various Constructors
	public OrderedGroup (Label label, Task parent, int size, boolean rev) {
		super(label, parent, size);
		setUp(rev, size);
	}
	
	public OrderedGroup (Label label, Task parent,  Collection <Task> col, boolean rev) {
		this(label, parent, col.size(), rev);
		copyCollection(col);
	}
	
	public void setUp (boolean rev, int size){
		orderedSubTasks = new ArrayList<Task>(size);
		reversible = rev;
	}
	
	protected void addTask (Task task) {
		subTasks.add(task);
		orderedSubTasks.add(task);
	}
	
	public void encorporateChildren (List<Task>demo){
		for (Task subTask : orderedSubTasks) {
			subTask.encorporate(demo);
		}
	}
	
	protected String name () {
		if (reversible) return "Reversible";
		return "Sequential";
	}
	
	public boolean isReversible () {return reversible;}
	
}
