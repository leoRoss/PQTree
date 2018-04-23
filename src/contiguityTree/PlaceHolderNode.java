package contiguityTree;

import java.util.List;

// Used for Hashing
// When a task needs to be hashed, but its label is likely to change, we use a placeholdertask with an equivalent label instead
public class PlaceHolderNode extends Node {

    PlaceHolderNode(Label copiedLabel) {
        super(copiedLabel, -1);
    }

    public void incorporate(List<Node> demo) throws IncorporationError {
        throw new IncorporationError("PlaceHolderNode methods should never be called!");
    }

    public int absoluteSize() {
        throw new Error("PlaceHolderTasks methods should never be called!");
    }

    public void printMe(int depth) {
        throw new Error("PlaceHolderTasks methods should never be called!");
    }

    public Node fullCopy() {
        throw new Error("PlaceHolderTasks methods should never be called!");
    }

    public boolean contentEquals(Node t) {
        throw new Error("PlaceHolderTasks methods should never be called!");
    }

    public void getPermutationCounts(List<Integer> list) {
        throw new Error("PlaceHolderTasks methods should never be called!");
    }
}
