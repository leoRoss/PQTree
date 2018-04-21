package contiguityTree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A P-NODE in a PQ-Tree
 * Group of items that always appear contiguously, in no specific ordering
 */
public class UnOrderedNode extends InnerNode {

    UnOrderedNode(Label label, Collection<Node> col) {
        super(label, col.size());
        copyCollection(col);
    }

    /**
     * Add subNodes from a collection into my subNodes
     * Assign myself as each subTask's parent
     */
    private void copyCollection(Collection<Node> col) {
        for (Node node : col) {
            addTaskSafe(node);
        }
    }

    /**
     * Should only called from the InnerNode parent class
     */
    protected void addTask(Node node) {
        subNodes.add(node);
    }

    //******* INCORPORATION METHODS *******//
    public void incorporateChildren(List<Node> demo) throws IncorporationError {
        for (Node subNode : subNodes) {
            subNode.incorporate(demo);
        }
    }

    public Incorporator createNewIncorporator(List<Node> demo) {
        return new UnOrderedIncorporator(this, demo);
    }

    //******* TASK TO TASK METHODS *******//
    public boolean contentEquals(Node node) {
        if (node == null) return false;
        if (size != node.getSize()) return false;
        if (absoluteSize() != node.absoluteSize()) return false;
        if (!sameType(node)) return false;

        UnOrderedNode ug = (UnOrderedNode) node;

        List<Node> ugSubNodes = new LinkedList<Node>(); //his subNodes
        ugSubNodes.addAll(ug.getSetSubTasks());

        //for each of my tasks, try to find myself in his subNodes
        //if found, remove the node from his subNodes and continue
        //if not found, return false
        for (Node subNode : subNodes) {
            boolean found = false;
            int index = 0;
            for (Node ugSubNode : ugSubNodes) {
                if (subNode.contentEquals(ugSubNode)) {
                    ugSubNodes.remove(index);
                    found = true;
                    break;
                }
                index++;
            }
            if (!found) return false;
        }

        return true;
    }

    public Node fullCopy() {
        List<Node> subNodeCopies = new LinkedList<Node>();
        for (Node subNode : subNodes) {
            subNodeCopies.add(subNode.fullCopy());
        }
        return new UnOrderedNode(label.copyLabel(), subNodeCopies);
    }


    //GENERAL GROUP METHODS - DOCUMENTED IN GROUP CLASS
    protected boolean sameType(Node node) {
        return node instanceof UnOrderedNode;
    }

    public boolean isOrdered() {
        return false;
    }

    public boolean isReversible() {
        return false;
    }

    protected String name() {
        return "Unordered InnerNode";
    }

    protected Collection<Node> getSubTasksForEfficientTraversal() {
        return subNodes;
    }
}
