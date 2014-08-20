package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	private ArrayList<Task> orderedSubTasks;
	protected boolean reversible;
	
	//Various Constructors
	public OrderedGroup (Label label, Task parent, int size, boolean rev) {
		super(label, parent, size);
		setUp(rev, size);
	}
	
	public OrderedGroup (Label label, Task parent,  Collection <Task> col, boolean rev) {
		this(label, parent, col.size(), rev);
		copyCollection(col);
	}
	
	//This constructor will absorb any subTasks which share my direction
	public OrderedGroup (Label label, Task parent,  Collection <Task> entireCollectionToCopy, boolean rev, int [] subTaskDirections, int directionStartIndex, int myDirection) {
        this(label, parent, entireCollectionToCopy.size(), rev);
        copyCollection(entireCollectionToCopy, subTaskDirections, directionStartIndex, myDirection);
    }
	
	private void copyCollection(Collection <Task> entireCollectionToCopy, int [] subTaskDirections, int directionStartIndex, int myDirection) {
	    int index = 0;
        for (Task task : entireCollectionToCopy){
            if (subTaskDirections[directionStartIndex+index]==myDirection && task.absoluteSize()>1 ) { //time to absorb, that means delete the subtask and steal his kids :)
                copyCollection( ((Group)task).getSubTasks() );
            }
            else {
                task.setParent((Task)this);
                addTask(task);
            }
            index++;
        }
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
			subTask.incorporate(demo);
		}
	}
	
	public void createNewIncorporator(List<Task> demo) {
        incorporator = new OrderedIncorporator(this, demo);
    }
	
	protected boolean sameType (Task task) {
		if (task instanceof OrderedGroup){
			if ( ((OrderedGroup)task).isReversible() == reversible ) return true;
		}
		return false;
	}
	
	protected String name () {
		if (reversible) return "Reversible";
		return "Sequential";
	}
	
	public boolean isOrdered () {return true;}
	public boolean isReversible () {return reversible;}
	
	protected Collection<Task> getSubTasks (){return orderedSubTasks;}
	
	public Task getSubTask (int index) {
		return orderedSubTasks.get(index); 
	}
	
}
