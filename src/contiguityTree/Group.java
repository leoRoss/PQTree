package contiguityTree;

import java.util.Collection;
import java.util.List;

//Ordered groups are sequential, and can sometimes be executed in the reverse order
//Unordered groups can be scrambled

public abstract class Group extends Task {
	Collection <Task> subTasks;
	Boolean ordered, reversible;
	
	public Group (Label lab, Task par) {
		super(lab,par);
	}
	
	public List<Task> encorporate (List<Task> demo) {
		//Let my subTasks try to encorporate themselves in the demo
		for (Task subTask : subTasks) {
			demo = subTask.encorporate(demo);
		}
		//At this point, each of my subTasks is either in the demo or all of its pieces are in the demo
		demo = Encorporator.encorporate(demo, this);
		return demo;
	}
	
	//copy over subTasks from a collection into my subTasks
	//assign myself as each subTask's parent
	protected void copyCollection (Collection <Task> col){
		for (Task temp : col){
			temp.setParent((Task)this);
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
