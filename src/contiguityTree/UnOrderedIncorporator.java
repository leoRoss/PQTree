package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * If tasks are in an UnOrdered InnerNode, then we know by definition that no subset of these tasks have always been contiguous
 * Therefore, we can only resolve and UnOrderedNode if all the subtasks are once again contiguous.
 * For Example, let say we have UnOrderedNode(ABCD) and demo <???AB????CD????>
 *      It would not make sense to place AB together in a InnerNode
 *      Why? Since A and B are in an UnOrderedNode with other tasks, we know that in some past demo, the two tasks were separated by C or D.
 *
 * So, all we need to to Incorporate an UnOrderedNode into a demo is...
 *      1. find all the children and their pieces.
 *      2. if they are all contiguous, make them into one UnOrderedNode with the same label as the node we are incorporating
 *      3. otherwise, relabel them all as pieces of this unordered node
 *
 */
public class UnOrderedIncorporator extends Incorporator {

    UnOrderedIncorporator(UnOrderedNode unOrderedGroupToIncorporateIntoDemo, List<Node> partiallyIncorporatedDemo) {
        permutation = partiallyIncorporatedDemo;
        node = unOrderedGroupToIncorporateIntoDemo;
    }

    public void incorporate() throws IncorporationError {
        /*
         * Instead of finding subNodes in the demo, lets try and find the demo tasks into the node.
         * This lets us leverage the demo.contains method with O(1)
         * O(demo.length) or O(3*demo.length).... who cares lets just make this code readable!
         */
        if (permutation.size() < node.getSize())
            throw new IncorporationError("The demo can not have less tasks than the node does");

        if (allGroupSubTasksAreContiguous()) replaceSingleChunkOfGroupSubTasks();
        else relabelAllGroupSubTasksAsPieces();
    }

    private boolean allGroupSubTasksAreContiguous() {
        boolean started = false;
        boolean ended = false;
        for (Node node : permutation) {
            if (this.node.contains(node)) {
                started = true;
                if (ended) return false; // hit a second hunk of subNodes
            } else {
                if (started) ended = true;
            }
        }
        return started;
    }

    private void replaceSingleChunkOfGroupSubTasks() {
        List<Node> mySubNodes = new LinkedList<Node>();
        Integer start = null;
        Integer end = null;
        int index = 0;

        for (Node node : permutation) {
            if (this.node.contains(node)) {
                if (start == null) {
                    start = index;
                }
                end = index;
                mySubNodes.add(node);
            } else {
                if (start != null)
                    break;
            }
            index++;
        }

        UnOrderedNode replacement = new UnOrderedNode(node.getLabel().copyLabel(), mySubNodes);
        replaceTasksInDemo(start, end, replacement);
    }

    private void replaceTasksInDemo(Integer start, Integer end, Node replacementNode) {
        int demoSize = permutation.size();
        List<Node> startOfDemo = permutation.subList(0, start);
        List<Node> endOfDemo = permutation.subList(end + 1, demoSize);
        List<Node> newDemo = new ArrayList<Node>(demoSize - end + start - 1);
        newDemo.addAll(startOfDemo);
        newDemo.add(replacementNode);
        newDemo.addAll(endOfDemo);

        permutation.clear();
        permutation.addAll(newDemo);
    }

    private void relabelAllGroupSubTasksAsPieces() {
        List<Node> allMyPieces = findAllMyPieces();
        int brotherhoodSize = allMyPieces.size();
        int index = 0;
        for (Node node : allMyPieces) {
            node.setLabel(new PieceLabel(this.node.getLabel().getId(), brotherhoodSize, index++));
        }
    }

    private List<Node> findAllMyPieces() {
        List<Node> allMyPieces = new LinkedList<Node>();
        for (Node node : permutation) {
            if (this.node.contains(node)) {
                allMyPieces.add(node);
            }
        }
        return allMyPieces;
    }
}
