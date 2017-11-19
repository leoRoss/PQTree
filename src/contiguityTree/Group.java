package contiguityTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//Ordered groups are sequential, and can sometimes be executed in the reverse order
//Unordered groups can be scrambled

public abstract class Group extends Task {
    
    private final boolean DEBUGGING = false;
    
    protected Set<Task> subTasks;
    protected Incorporator incorporator;
    
    protected List<Task> completed; //used for traversals

    public Group(Label lab, Task par, int numSubTasks) {
        super(lab, par, numSubTasks);
        subTasks = new HashSet<Task>((int) Math.ceil(numSubTasks / 0.75)); // initialize so will not need be resized
        completed = new LinkedList<Task>();
    }

    // INCORPORATION METHODS
    public void incorporate(List<Task> demo) throws IncorporationError {
        // Let my subTasks try to incorporate themselves in the demo.
        incorporateChildren(demo);
        // At this point, each of my subTasks is either:
        //      has a single equivalent task with the same Label in the demo
        //      or has several equivalent task with PieceLabels in the demo
        
        if (DEBUGGING) { System.out.println(); System.out.println(); System.out.println("Incorporating:"); printMe(4); System.out.println("Into Demo:"); printTaskList(demo); System.out.println();  }

        createNewIncorporator(demo); //instantiate the appropriate type of incorporator
        incorporator.incorporate();
    }

    public abstract void incorporateChildren(List<Task> demo) throws IncorporationError;
    public abstract void createNewIncorporator(List<Task> demo) throws IncorporationError;

    
    //TRAVERSAL METHODS
    public abstract void getNextPossibleTasks(List<Primitive> list);
    
    public Task executeInTraversal(Task completedChild){
        completed.add(completedChild);
        //if all my tasks are completed, keep going up the chain. otherwise, stay here.
        if (completed.size()==size) {
            if (parent!=null) return parent.executeInTraversal(this);
            return null;
        }
        return this;
    }
    
    
    // ILLEGAL FOR A SUBTASK TO HAVE A PIECE LABEL!
    // Thus, we relabel tasks with PieceLabels
    protected void addTaskSafe(Task task) {
        if (task.isPiece()) {
            task.setLabel(new Label());
        }
        task.setParent(this);
        addTask(task);
    }

    protected abstract void addTask(Task task);
    
    public abstract boolean contentEquals (Task t); //encode same rules about the same set of Objects
    public abstract Task fullCopy ();
    
    // Will return true even if Task is a Piece
    public boolean contains(Task task) { 
        return subTasks.contains(task);
    }
    
    protected Collection<Task> getSetSubTasks() {
        return subTasks;
    }
    
    //How many primitive tasks are below this group?
    public int absoluteSize() {
        int sum = 0;
        for (Task subTask : subTasks) {
            sum += subTask.absoluteSize();
        }
        return sum;
    }

    //Is this group the same type (unordered, sequential, or reversible) as the Task passed in?
    protected abstract boolean sameType(Task task);

    public abstract boolean isOrdered();
    public abstract boolean isReversible();
    
    public void getGroupPermutationCounts(List<Integer> list, boolean ignoreOrderingInGroups) {
    	if (ignoreOrderingInGroups || !isOrdered() ) {
    		list.add(subTasks.size());
    	} else { // It is ordered and we care about ordering
    		if (isReversible()) {
    			list.add(2); // Two ways to permute
    		} else {
    			list.add(1);
    		}
    	}
    	// Recurse
    	for (Task task : getSubTasksForEfficientTraversal()) {
    		task.getGroupPermutationCounts(list, ignoreOrderingInGroups);
    	}
    }

    // PRINTING
    public void printMe(int depth) {
        printSpace(depth);
        System.out.print(name() + ": " + label + " {");
        printPreconditions();
        System.out.println();
        for (Task subTask : getSubTasksForEfficientTraversal()) {
            subTask.printMe(depth + 1);
        }
        printSpace(depth);
        System.out.println("}");
    }
    
    protected abstract String name();
    protected abstract Collection<Task> getSubTasksForEfficientTraversal();
    
    
    // DEBUGGING ONLY
    private void printTaskList(List<Task> demo) {
        for (Task task : demo) {
            task.printMe(4);
        }
        System.out.println();
    }
}
