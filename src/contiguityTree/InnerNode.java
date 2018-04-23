package contiguityTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An inner node within the PQ Tree.
 * Can either be Ordered or UnOrdered.
 */
public abstract class InnerNode extends Node {

    Set<Node> subNodes;

    InnerNode(Label lab, int numSubTasks) {
        super(lab, numSubTasks);
        subNodes = new HashSet<>((int) Math.ceil(numSubTasks / 0.75)); // initialize so will not need be resized
    }

    /**
     * Incorporates this node into permutation
     *
     * @param permutation - the previous permutation (which is modified in place to become a new tree)
     */
    public void incorporate(List<Node> permutation) throws IncorporationError {
        // Let my subNodes to incorporate themselves in the demo.
        incorporateChildren(permutation);
        // At this point, each of my subNodes either:
        //      has a single equivalent node with the same Label in the demo
        //      or has several equivalent nodes with PieceLabels in the demo
        // System.out.println(); System.out.println(); System.out.println("Incorporating:"); printMe(4); System.out.println("Into Demo:"); printTaskList(demo); System.out.println();
        createNewIncorporator(permutation).incorporate();
    }

    public abstract void incorporateChildren(List<Node> permutation) throws IncorporationError;

    public abstract Incorporator createNewIncorporator(List<Node> permutation) throws IncorporationError;

    void addTaskSafe(Node node) {
        if (node.isPiece()) {
            node.setLabel(new Label());
        }
        addTask(node);
    }

    protected abstract void addTask(Node node);

    public abstract boolean contentEquals(Node t); // Checks if the Node encode same the rules about the same set of elements

    public abstract Node fullCopy();

    /**
     * @return true even if Node is a Piece
     */
    boolean contains(Node node) {
        return subNodes.contains(node);
    }

    Collection<Node> getSetSubTasks() {
        return subNodes;
    }

    /**
     * @return The number of leaf nodes are below this node?
     */
    public int absoluteSize() {
        int sum = 0;
        for (Node subNode : subNodes) {
            sum += subNode.absoluteSize();
        }
        return sum;
    }

    /**
     * @return if node is the same type (ordered, sequential, reversible)
     */
    protected abstract boolean sameType(Node node);

    public abstract boolean isReversible();

    public void getPermutationCounts(List<Integer> list) {
        if (isReversible()) {
            list.add(2); // Two ways to permute
        } else {
            list.add(1);
        }
        // Recurse
        for (Node node : getSubTasksForEfficientTraversal()) {
            node.getPermutationCounts(list);
        }
    }

    public void printMe(int depth) {
        printSpace(depth);
        System.out.println(name() + ": " + label + " {");
        for (Node subNode : getSubTasksForEfficientTraversal()) {
            subNode.printMe(depth + 1);
        }
        printSpace(depth);
        System.out.println("}");
    }

    protected abstract String name();

    /**
     * @return the most traversal-efficient representation of the subNodes
     */
    protected abstract Collection<Node> getSubTasksForEfficientTraversal();
}
