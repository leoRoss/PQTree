package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Task {
	protected Label label;
	protected Task parent; //Parent in tree, root will have a null parent
	protected Collection <Label> preconditions; //TO BE IMPLEMENTED
	protected int size; //number of subLabels
	
	public Task (Label lab, Task par, int s) {
		label = lab;
		parent = par;
		preconditions = new ArrayList<Label> ();
		size = s;
	}
	
	//Transfer my knowledge to a new demo
	public abstract void incorporate (List<Task> demo) throws IncorporationError;
	
	//two Tasks are equal as long as they have equivalent labels
	public boolean equals (Object obj) {
	    if (obj == null) return false;
	    return obj instanceof Task && ((Task)obj).getLabel().equals(label);
	}
	
	//Two Tasks are strictEqual if their labels are exactly the same
	public boolean strictEquals (Object obj) {
		if (obj instanceof Task) {
			if (label.strictEquals( ((Task)obj).getLabel() )) return true;
		}
		return false;
	}
	
	//Two Tasks are contentEqual if they encode the same contiguity rules about the same set of objects
	//IE: They must have the same structure all the way down to the leaf of the node
	public abstract boolean contentEquals (Task t);
	
	public abstract Task fullCopy ();
	
	//Number of leaf nodes below me    
    public abstract int absoluteSize ();
    
    //Am I a piece of an unresolved task? IE: Do I have a PieceLabel?
    public boolean isPiece () { return label.isPiece(); }   
    
    //Simply using the label as a hash gives us a perfect hash distribution since Labels and PieceLabels are never hashed together
    public int hashCode () { return label.hashCode(); }
    
	//Make a PlaceHolderTask with an equivalent copy - used for to represent a Task during hashing if that Task may later be relabeled
	public Task vagueCopy () { return new PlaceHolderTask(label.copyLabel()); }
	
	//number of Brothers if Piece, 1 otherwise
	public int getBrotherhoodSize() {return label.getBrotherhoodSize(); }
	
	
	//GETTERS AND SETTERS
    public Label getLabel () {return label;}
    public void setLabel (Label l) {label = l;}
    public Task getParent () {return parent;}
    public void setParent (Task par) {parent = par;}
    public int getSize () {return size;}
    
    
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
