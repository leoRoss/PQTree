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
	//GETTERS AND SETTERS
	public Label getLabel () {return label;}
	public void setLabel (Label l) {label = l;}
	public Task getParent () {return parent;}
	public void setParent (Task par) {parent = par;}
	public int getSize () {return size;}
	
	//TOHASH OVERRIDE - both toString and Hash must match in Java :P
	public int hashCode () {
		return label.getId();
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
}
