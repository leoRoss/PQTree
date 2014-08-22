package contiguityTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Ordered groups are sequential, and can sometimes be executed in the reverse order
//Unordered groups can be scrambled

public abstract class Group extends Task {
    protected Set<Task> subTasks;
    protected Incorporator incorporator;

    public Group(Label lab, Task par, int numSubTasks) {
        super(lab, par, numSubTasks);
        subTasks = new HashSet<Task>((int) Math.ceil(numSubTasks / 0.75)); // initialize so will not need be resized
    }

    // INCORPORATION METHODS
    public void incorporate(List<Task> demo) {
        // Let my subTasks try to incorporate themselves in the demo
        encorporateChildren(demo);
        // At this point, each of my subTasks is either in the demo or all of
        // its pieces are in the demo

        System.out.println();
        System.out.println();
        System.out.println("Incorporating:");
        printMe(4);
        System.out.println("Into Demo:");
        printTaskList(demo);
        System.out.println();
        // printTaskListLabels(demo);
        createNewIncorporator(demo);

        incorporator.incorporate();

    }

    public abstract void encorporateChildren(List<Task> demo);

    public abstract void createNewIncorporator(List<Task> demo);

    // ILLEGAL FOR A SUBTASK TO HAVE A PIECE LABEL!
    protected void addTaskSafe(Task task) {
        if (task.isPiece()) {
            task.setLabel(new Label());
        }
        task.setParent(this);
        addTask(task);
    }

    protected abstract void addTask(Task task);

    // Will return true even if Task is a Piece
    public boolean contains(Task task) {
        return subTasks.contains(task);
    }

    // PRINTING
    public void printMe(int depth) {
        printSpace(depth);
        System.out.print(name() + ": " + label + " {");
        printPreconditions();
        System.out.println();
        for (Task subTask : getPrintSubTasks()) {
            subTask.printMe(depth + 1);
        }
        printSpace(depth);
        System.out.println("}");
    }

    // GETTERS
    protected Collection<Task> getSetSubTasks() {
        return subTasks;
    }

    public int absoluteSize() {
        int sum = 0;
        for (Task subTask : subTasks) {
            sum += subTask.absoluteSize();
        }
        return sum;
    }

    protected abstract String name();

    protected abstract boolean sameType(Task task);

    public abstract boolean isOrdered();

    public abstract boolean isReversible();

    protected abstract Collection<Task> getPrintSubTasks();

    // DEBUGGING ONLY
    private void printTaskList(List<Task> demo) {
        for (Task task : demo) {
            task.printMe(4);
        }
        System.out.println();
    }
}
