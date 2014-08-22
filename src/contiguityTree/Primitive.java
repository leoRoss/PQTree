package contiguityTree;

import java.util.List;

public class Primitive extends Task {
	protected Object object;
	
	public Primitive (Label lab, Object obj) {
        super(lab, null, 1);
        object=obj;
    }
	
	public Primitive (Object obj) {
		this(new Label(), obj);
	}
	
	//All we have to do is correctly label the versions of ourselves in the demo
	public void incorporate(List<Task> demo) throws IncorporationError {
	    boolean alreadyFound = false;
		for (Task subTask : demo) {
			if (this.contentEquals(subTask)) { 
			    if (alreadyFound) throw new IncorporationError ("An object should only appear once in each demo!");
			    subTask.setLabel(label);
			    alreadyFound = true;
			}
		}
	}
	
	public boolean contentEquals (Task task) {
        return task!=null && task instanceof Primitive && ((Primitive)task).object.equals(object);
    }
	
	public Task fullCopy (){ return new Primitive(label.copyLabel(), object);}
	
	public int absoluteSize(){ return 1; }
	
	public void printMe(int depth) {
		printSpace(depth);
		System.out.print(object + ": " + label);
		printPreconditions();
		System.out.println();
	}
}
