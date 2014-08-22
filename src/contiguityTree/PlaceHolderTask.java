package contiguityTree;

import java.util.List;

//Used for Hashing
//When a task needs to be hashed, but its label is likely to change, we use a placeholdertask with an equivalent label instead
public class PlaceHolderTask extends Task {

	public PlaceHolderTask (Label copiedLabel) {
		super(copiedLabel, null, -1);
	}

	public void incorporate(List<Task> demo) {
		throw new Error ("PlaceHolderTasks should never be called!");
	}
	
	public int absoluteSize(){
	    throw new Error ("PlaceHolderTasks should never be called!");
	}
	
	public void printMe(int depth) {
	    throw new Error ("PlaceHolderTasks should never be called!");
	}
	
}
