package leo.ContiguityTree;

import java.util.Collection;
import java.util.List;

public abstract class Task {
	Label label;
	Encorporator encorporator;
	Collection <Label> preconditions;
	
	//Transfer my knowledge to a new demo
	public abstract List<Task> encorporate(List<Task> demo);
	
	//GETTERS AND SETTERS
	public Label getLabel() {return label;}
	public void setLabel(Label l) {this.label = l;}
	
	
	//PRINTING
	public abstract void printMe(int d);
	
	public void printPreconditions() {
		if (preconditions.size()>0) {
	    	System.out.print (" --> ");
	    	for (Label precond : preconditions){
	    	  	System.out.print(precond); System.out.print(", ");
	      	}
	    }
	}
	
	public void printSpace(int depth) {
		for (int x=0; x<depth; x++) System.out.print("    "); 
	}
}
