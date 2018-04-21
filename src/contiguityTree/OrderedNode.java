package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
 * A Q-NODE in a PQ Tree
 */
public class OrderedNode extends InnerNode {
    private ArrayList<Node> orderedSubNodes;
    private boolean reversible;

    //Various Constructors
    private OrderedNode(Label label, int size, boolean rev) {
        super(label, size);
        orderedSubNodes = new ArrayList<Node>(size);
        reversible = rev;
    }

    OrderedNode(Label label, List<Node> subTasksToAdd, boolean rev) {
        this(label, subTasksToAdd.size(), rev);
        addListToSubTasks(subTasksToAdd);
    }


    // This constructor will absorb any subNodes which share my direction
    OrderedNode(Label label, List<Node> subTasksToAdd, boolean rev, List<Integer> subTaskDirections, int myDirection) {
        this(label, subTasksToAdd.size(), rev);
        addListToSubTasks(subTasksToAdd, subTaskDirections, myDirection);
    }

    private void addListToSubTasks(List<Node> entireListToAdd, List<Integer> subTaskDirections, int myDirection) {
        Iterator<Node> taskIt = entireListToAdd.iterator();
        Iterator<Integer> dirIt = subTaskDirections.iterator();
        while (taskIt.hasNext()) {
            Node subNode = taskIt.next();
            int subTaskDir = dirIt.next(); //we want this to fail if directions run out before subtask do
            if (subTaskDir == myDirection && subNode instanceof OrderedNode) { //time to absorb, that means remove all ties to the subtask and steal his kids :)
                addListToSubTasks(((OrderedNode) subNode).getOrderedSubNodes());
            } else {
                addTaskSafe(subNode);
            }
        }
    }

    private void addListToSubTasks(List<Node> entireListToAdd) {
        for (Node subNode : entireListToAdd) addTaskSafe(subNode);
    }

    protected void addTask(Node node) {
        if (node.isPiece()) throw new Error("Please only add Tasks using addTaskSafe()!");
        subNodes.add(node);
        orderedSubNodes.add(node);
    }


    //INCORPORATION METHODS
    public void incorporateChildren(List<Node> permutation) throws IncorporationError {
        for (Node subNode : orderedSubNodes) {
            subNode.incorporate(permutation);
        }
    }

    public Incorporator createNewIncorporator(List<Node> permutation) throws IncorporationError {
        return new OrderedIncorporator(this, permutation);
    }


    //TASK TO TASK METHODS
    public boolean contentEquals(Node node) {
        //early abort
        if (node == null) return false;
        if (size != node.getSize()) return false;
        if (absoluteSize() != node.absoluteSize()) return false;
        if (!sameType(node)) return false;

        OrderedNode og = (OrderedNode) node;

        //no matter what if they are in the same order, they are content equals
        for (int i = 0; i < size; i++) {
            if (!getSubTask(i).contentEquals(og.getSubTask(i))) break;
            if (i == size - 1) return true;
        }

        //if its reversible, it has another chance at being equal
        if (reversible) {
            for (int i = 0; i < size; i++) {
                if (!getSubTask(i).contentEquals(og.getSubTask(size - 1 - i))) return false;
                if (i == size - 1) return true;
            }
        }

        return false;
    }

    public Node fullCopy() {
        List<Node> subNodeCopies = new LinkedList<Node>();
        for (Node subNode : orderedSubNodes) {
            subNodeCopies.add(subNode.fullCopy());
        }
        return new OrderedNode(label.copyLabel(), subNodeCopies, reversible);
    }


    //INTERFACE WITH ORDERED SUBTASKS
    int lenientIndexOfSubTask(Node node) { //return the index of the equal subTask even if node has a PieceLabel
        int index = 0;
        for (Node subNode : orderedSubNodes) {
            if (subNode.equals(node)) return index;
            index++;
        }
        return -1;
    }

    private Node getSubTask(int index) {
        return orderedSubNodes.get(index);
    }

    private List<Node> getOrderedSubNodes() {
        return orderedSubNodes;
    }

    //GENERAL GROUP METHODS - DOCUMENTED IN GROUP CLASS
    protected boolean sameType(Node node) {
        if (node instanceof OrderedNode) {
            return (((OrderedNode) node).isReversible() == reversible);
        }
        return false;
    }

    public boolean isOrdered() {
        return true;
    }

    public boolean isReversible() {
        return reversible;
    }

    protected Collection<Node> getSubTasksForEfficientTraversal() {
        return getOrderedSubNodes();
    }

    protected String name() {
        if (reversible) return "Reversible";
        return "Sequential";
    }
}
