package contiguityTree;

import java.util.Collection;
import java.util.List;

//Group of items that always appear contiguously, with no hard ordering
public class UnOrderedGroup extends Group {
	
	public UnOrderedGroup (Label label, Task parent, Collection <Task> col) {
		super(label, parent, col.size());
		copyCollection(col);
	}
	
	//copy over subTasks from a collection into my subTasks
    //assign myself as each subTask's parent
    private void copyCollection (Collection <Task> col){
        for (Task task : col){
            addTaskSafe(task);
        }
    }
	
	protected void addTask (Task task) {subTasks.add(task);}
	
	public void encorporateChildren (List<Task>demo){
		for (Task subTask : subTasks) {
			subTask.incorporate(demo);
		}
	}
	
	public void createNewIncorporator(List<Task> demo) {
        incorporator = new UnOrderedIncorporator(this, demo);
    }
	
	protected boolean sameType (Task task) {return task instanceof UnOrderedGroup;}
	protected String name () {return "Unordered Group";}
	public boolean isOrdered () {return false;}
	public boolean isReversible () {return false;}
	
	protected Collection<Task> getPrintSubTasks (){
        return getSetSubTasks();
    }
}
