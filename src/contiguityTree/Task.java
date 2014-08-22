package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Task {
	protected Label label;
	protected Task parent;
	protected Collection <Label> preconditions;
	protected int size;
	
	public Task (Label lab, Task par, int s) {
		label = lab;
		parent = par;
		preconditions = new ArrayList<Label> ();
		size = s;
	}
	//Transfer my knowledge to a new demo
	public abstract void incorporate (List<Task> demo);
	
	//Number of leaf nodes below me	
	public abstract int absoluteSize ();
	
	//Am I a peice of an unresolved task?
	public boolean isPiece () {
		return label.isPiece();
	}
	
	//GETTERS AND SETTERS
	public Label getLabel () {return label;}
	public void setLabel (Label l) {label = l;}
	public Task getParent () {return parent;}
	public void setParent (Task par) {parent = par;}
	public int getSize () {return size;}
	
	
	public int hashCode () {
		return label.hashCode();
	}
	
	//two Tasks are equal as long as they have equivalent labels
	public boolean equals (Object obj) {
	    if (obj == null) return false;
	    return obj instanceof Task && ((Task)obj).getLabel().equals(label);
	}
	
	//Two Tasks are lenientEqual if their labels are exactly the same
	public boolean strictEquals (Object obj) {
		if (obj instanceof Task) {
			if (label.strictEquals( ((Task)obj).getLabel() )) return true;
		}
		return false;
	}
	
	public Task vagueCopy () {
	    return new PlaceHolderTask(label.copyLabel());
	}
	
	//PRINTING
	public abstract void printMe (int d);
	
	public void printPreconditions () {
		if (preconditions.size()>0) {
	    	System.out.print (" --> ");
	    	for (Label precond : preconditions){
	    	  	System.out.print(precond); System.out.print(", ");
	      	}
	    }
	}
	
	public void printSpace (int depth) {
		for (int x=0; x<depth; x++) System.out.print("    "); 
	}
	
	public int getBrotherhoodSize() {
		return label.getBrotherhoodSize();
	}
}
