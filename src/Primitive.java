import java.util.List;

public class Primitive extends Task {
	Object object;

	public Primitive (Object obj) {
		object = obj;
	}
	
	//All we have to do is correctly label versions of ourselves in the demo
	public List<Task> encorporate(List<Task> demo) {
		for (Task subTask : demo) {
			if (this.equals(subTask)) subTask.setLabel(label);
		}
		return demo;
	}
	
	public boolean equals (Object obj) {
		return obj instanceof Primitive && ((Primitive)obj).object.equals(object);
	}
	
	public void printMe(int depth) {
		printSpace(depth);
		System.out.print(object);
		printPreconditions();
		System.out.println();
	}
	
}
