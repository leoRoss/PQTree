package contiguityTree;

import java.util.ArrayList;
import java.util.List;

public class PQTree {
    private Node head;

    public PQTree() {
    }

    /**
     * Take in a list of unlabeled primitive actions (the demo) and incorporate into the existing tree
     * If incorporation fails, the demo is ignored and false is returned
     * This could occur if the RULES (above) are not respected
     */
    private boolean incorporate(List<Node> permutation) {
        if (permutation == null || permutation.size() == 0) return false;

        if (head == null) {
            head = new OrderedNode(new Label(), permutation, false); // null parent, false because sequence is not reversible
            return true;
        } else {
            try {
                head.incorporate(permutation);
            } catch (IncorporationError e) {
                // System.out.println("DEMO FAILED TO INCORPORATE");
                return false;
            }

            if (permutation.size() != 1) {
                // System.out.println("DEMO FAILED TO INCORPORATE");
                return false;
            }
            head = permutation.get(0); // the demo should now be a single task... the new tree
            return true;
        }
    }

    // The following two methods should be used to send a new sequence of Objects (List or Array) to the Tree
    // They return false if unsuccessful
    public boolean absorbPermutation(List<Object> permutation) {
        List<Node> wrappedDemo = new ArrayList<Node>(permutation.size());
        for (Object obj : permutation) {
            wrappedDemo.add(new LeafNode(obj)); // null parents for now
        }
        return incorporate(wrappedDemo);
    }

    public boolean absorbPermutation(Object[] permutation) {
        List<Node> wrappedDemo = new ArrayList<Node>(permutation.length);
        for (int i = 0; i < permutation.length; i++) {
            wrappedDemo.add(new LeafNode(permutation[i])); // null parents for now
        }
        return incorporate(wrappedDemo);
    }

    public boolean equals(PQTree tree) {
        if (head == null) return false;
        if (tree.head == null) return false;
        return head.contentEquals(tree.head);
    }

    public void print() {
        head.printMe(0);
    }
}
