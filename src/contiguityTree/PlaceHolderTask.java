package contiguityTree;

import java.util.List;

//Used for Hashing
public class PlaceHolderTask extends Task {

	public PlaceHolderTask (Label copiedLabel) {
		super(copiedLabel, null, -1);
	}

	public void encorporate(List<Task> demo) {
		throw new Error ("PlaceHolderTasks should never be called!");
	}
	
	public int absoluteSize(){
	    throw new Error ("PlaceHolderTasks should never be called!");
	}
	
	public void printMe(int depth) {
	    throw new Error ("PlaceHolderTasks should never be called!");
	}
	
}
