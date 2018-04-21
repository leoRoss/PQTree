package contiguityTree;

import java.util.List;

public class LeafNode extends Node {
	private Object object;
	
	private LeafNode(Label lab, Object obj) {
        super(lab,1);
        object=obj;
    }
	
    LeafNode(Object obj) {
		this(new Label(), obj);
	}

	public void incorporate(List<Node> permutation) throws IncorporationError {
		// All we have to do is find and label the our matching element in the permutation
	    boolean alreadyFound = false;
		for (Node subNode : permutation) {
			if (this.contentEquals(subNode)) {
			    if (alreadyFound) throw new IncorporationError ("An object should only appear once in each demo!");
			    subNode.setLabel(label);
			    alreadyFound = true;
			}
		}
	}

	public boolean contentEquals (Node node) {
        return node instanceof LeafNode && ((LeafNode) node).object.equals(object);
    }

	public Node fullCopy (){ return new LeafNode(label.copyLabel(), object);}
	
	public int absoluteSize(){ return 1; }
	
	public void printMe(int depth) {
		printSpace(depth);
		System.out.print(object + ": " + label);
		System.out.println();
	}

	public void getGroupSizes(List<Integer> list, boolean onlyCountPermutationsForOrderedGroups) {
    	list.add(1);
    }
}
