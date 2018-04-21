package contiguityTree;

import java.util.List;

public abstract class Node {
	protected Label label;
	int size; // number of subLabels
	
	public Node(Label lab, int s) {
		label = lab;
		size = s;
	}
	
	//Transfer my knowledge to a new demo
	public abstract void incorporate (List<Node> permutation) throws IncorporationError;
	
	//two Tasks are equal as long as they have equivalent labels
	public boolean equals (Object obj) {
	    if (obj == null) return false;
	    return obj instanceof Node && ((Node)obj).getLabel().equals(label);
	}
	
	//Two Tasks are strictEqual if their labels are exactly the same
	boolean strictEquals (Object obj) {
		if (obj instanceof Node) {
			return (label.strictEquals( ((Node)obj).getLabel() ));
		}
		return false;
	}
	
	//Two Tasks are contentEqual if they encode the same contiguity rules about the same set of objects
	//IE: They must have the same structure all the way down to the leaf of the node
	public abstract boolean contentEquals (Node t);
	
	public abstract Node fullCopy ();
	
	public abstract void getGroupSizes(List<Integer> list, boolean onlyCountPermutationsForOrderedGroups);
	
	//Number of leaf nodes below me    
    public abstract int absoluteSize ();
    
    //Am I a piece of an unresolved task? IE: Do I have a PieceLabel?
	boolean isPiece () { return label.isPiece(); }
    
    //Simply using the label as a hash gives us a perfect hash distribution since Labels and PieceLabels are never hashed together
    public int hashCode () { return label.hashCode(); }
    
	//Make a PlaceHolderNode with an equivalent copy - used for to represent a Node during hashing if that Node may later be relabeled
	Node vagueCopy () { return new PlaceHolderNode(label.copyLabel()); }
	
	//number of Brothers if Piece, 1 otherwise
	int getBrotherhoodSize() {return label.getBrotherhoodSize(); }
	
	//GETTERS AND SETTERS
    public Label getLabel () {return label;}
    public void setLabel (Label l) {label = l;}
    int getSize () {return size;}
    
    
	//PRINTING
	public abstract void printMe (int d);
	
    void printSpace (int depth) {
		for (int x=0; x<depth; x++) System.out.print("    "); 
	}
}
