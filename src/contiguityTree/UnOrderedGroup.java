package contiguityTree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

//Group of items that always appear contiguously, with no hard ordering (preconditions not included CT)
public class UnOrderedGroup extends Group {
	
	public UnOrderedGroup (Label label, Task parent, Collection <Task> col) {
		super(label, parent, col.size());
		copyCollection(col);
	}
	
	//add subTasks from a collection into my subTasks
    //assign myself as each subTask's parent
    private void copyCollection (Collection <Task> col){
        for (Task task : col){
            addTaskSafe(task);
        }
    }
	
	protected void addTask (Task task) {subTasks.add(task);}
	
	//INCORPORATION METHODS
	public void incorporateChildren (List<Task>demo) throws IncorporationError {
		for (Task subTask : subTasks) {
			subTask.incorporate(demo);
		}
	}
	
	public void createNewIncorporator(List<Task> demo) throws IncorporationError { incorporator = new UnOrderedIncorporator(this, demo); }
	
	
	//TRAVERSAL METHODS
    public void getNextPossibleTasks(List<Primitive> list){
        for (Task subTask : subTasks) {
            if (! completed.contains(subTask)) {
                subTask.getNextPossibleTasks(list);
            }
        }
    }
    
    
	//TASK TO TASK METHODS
	public boolean contentEquals (Task task) {
        //early abort
        if (task==null) return false;
        if (size!=task.getSize()) return false;
        if (absoluteSize()!=task.absoluteSize()) return false;
        if (!sameType(task)) return false;

        UnOrderedGroup ug = (UnOrderedGroup)task;
        
        List<Task> ugSubTasks = new LinkedList<Task>(); //his subTasks
        for (Task ugSubTask : ug.getSetSubTasks()) {
            ugSubTasks.add(ugSubTask);
        }
        
        //for each of my tasks, try to find myself in his subTasks
            //if found, remove the task from his subTasks and continue
            //if not found, return false
        for (Task subTask : subTasks) {
            boolean found = false;
            int index=0;
            for (Task ugSubTask: ugSubTasks) {
                if (subTask.contentEquals(ugSubTask)) {
                    ugSubTasks.remove(index);
                    found = true;
                    break;
                }
                index++;
            }
            if (!found) return false;
        }
   
        return true;
    }
	 
	public Task fullCopy (){ 
	    List <Task> subTaskCopies = new LinkedList <Task> ();
	    for (Task subTask : subTasks) {
	         subTaskCopies.add(subTask.fullCopy());
	    }
	    return new UnOrderedGroup(label.copyLabel(), null, subTaskCopies);
	}
	
	
	//GENERAL GROUP METHODS - DOCUMENTED IN GROUP CLASS
	protected boolean sameType (Task task) {return task instanceof UnOrderedGroup;}
	public boolean isOrdered () {return false;}
	public boolean isReversible () {return false;}
	
	protected String name () {return "Unordered Group";}
	protected Collection<Task> getPrintSubTasks (){ return subTasks; }
	    
}
