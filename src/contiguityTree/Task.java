package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Task {
	Label label;
	Task parent;
	Collection <Label> preconditions;
	int size;
	
	public Task (Label lab, Task par, int s) {
		label = lab;
		parent = par;
		preconditions = new ArrayList<Label> ();
		size = s;
	}
	//Transfer my knowledge to a new demo
	public abstract void encorporate (List<Task> demo);
	
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
	
	//Two Tasks are equal if their labels are exactly the same
	public boolean lenientEquals (Object obj) {
		if (obj instanceof Task) {
			if (label.lenientEquals( ((Task)obj).getLabel() )) return true;
		}
		return false;
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
	
	public int getNumberOfBrothers() {
		return label.getNumberOfBrothers();
	}
}
