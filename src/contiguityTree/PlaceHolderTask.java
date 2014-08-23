package contiguityTree;

import java.util.List;

//Used for Hashing
//When a task needs to be hashed, but its label is likely to change, we use a placeholdertask with an equivalent label instead
public class PlaceHolderTask extends Task {

	public PlaceHolderTask (Label copiedLabel) {
		super(copiedLabel, null, -1);
	}

	public void incorporate(List<Task> demo) throws IncorporationError {
		throw new IncorporationError ("PlaceHolderTask methods should never be called!");
	}
	
	public int absoluteSize(){
	    throw new Error ("PlaceHolderTasks methods should never be called!");
	}
	
	public void printMe(int depth) {
	    throw new Error ("PlaceHolderTasks methods should never be called!");
	}
	
	public Task fullCopy() {
        throw new Error ("PlaceHolderTasks methods should never be called!");
    }

    public boolean contentEquals(Task t) {
        throw new Error ("PlaceHolderTasks methods should never be called!");
    }
    
    public void getNextPossibleTasks(List<Primitive> list){
        throw new Error ("PlaceHolderTasks methods should never be called!");
    }
    
    public Task executeInTraversal(Task t){
        throw new Error ("PlaceHolderTasks methods should never be called!");
    }
	
}
