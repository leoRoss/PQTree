package contiguityTree;

import java.util.List;

public class Primitive extends Task {
	Object object;

	public Primitive (Label label, Task parent, Object obj) {
		super(label, parent, 1);
		object = obj;
	}
	
	public Primitive (Object obj) {
		this(new Label(), null, obj);
	}
	
	//All we have to do is correctly label versions of ourselves in the demo
	public void encorporate(List<Task> demo) {
		for (Task subTask : demo) {
			if (this.equals(subTask)) subTask.setLabel(label);
		}
	}
	
	public int absoluteSize(){
		return 1;
	}
	
	public boolean equals (Object obj) {
		return obj instanceof Primitive && ((Primitive)obj).object.equals(object);
	}
	
	public void printMe(int depth) {
		printSpace(depth);
		System.out.print(object + ": " + label);
		printPreconditions();
		System.out.println();
	}
	
	//remove afer testing
	public Object getObject() {
		return object;
	}
	
}
