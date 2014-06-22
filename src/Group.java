import java.util.Collection;
import java.util.List;

//Ordered groups are sequential, and can sometimes be executed in the reverse order
//Unordered groups can be scrambled

public abstract class Group extends Task {
	Collection <Task> subTasks;
	Boolean ordered, reversible;
	String type;
	
	public List<Task> encorporate (List<Task> demo) {
		for (Task subTask : subTasks) {
			demo = subTask.encorporate(demo);
		}
		//TODO encorporate my knowledge now that all primitives and subTasks are properly encorporated/labeled
		return demo;
	}
	
	//simply copy over children from another collection
	protected void copyCollection (Collection <Task> col){
		for (Task temp : col){
			subTasks.clear();
			subTasks.add(temp);
		}
	}
	
	//PRINTING
	public void printMe(int depth) {
	    printSpace(depth);
	    System.out.print(name() + ": " + label + " {");
	    printPreconditions();
	    System.out.println();
	    for (Task subTask : subTasks){
	    	subTask.printMe(depth+1);
		}
	    printSpace(depth);
	    System.out.println(")");
	}
	
	private String name () {
		if (!ordered) return "Unordered";
		if (reversible) return "Reversible";
		return "Sequential";
	}
}
