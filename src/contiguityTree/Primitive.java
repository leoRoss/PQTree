package contiguityTree;

import java.util.List;

public class Primitive extends Task {
	protected Object object;
	
	public Primitive (Object obj) {
		super(new Label(), null, 1);
		object=obj;
	}
	
	//All we have to do is correctly label the versions of ourselves in the demo
	public void incorporate(List<Task> demo) {
	    boolean alreadyFound = false;
		for (Task subTask : demo) {
			if (this.contentEquals(subTask)) { 
			    if (alreadyFound) throw new Error ("An object should only appear once in each demo!");
			    subTask.setLabel(label);
			    alreadyFound = true;
			}
		}
	}
	
	public int absoluteSize(){ return 1; }
	
	public boolean contentEquals (Task task) {
		return task instanceof Primitive && ((Primitive)task).object.equals(object);
	}
	
	public void printMe(int depth) {
		printSpace(depth);
		System.out.print(object + ": " + label);
		printPreconditions();
		System.out.println();
	}
}
