package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * If tasks are in an UnOrdered Group, then we know by definition that no subset of these tasks have always been contiguous
 * Therefore, we can only resolve and UnOrderedGroup if all the subtasks are once again contiguous.
 * For Example, let say we have UnOrderedGroup(ABCD) and demo <???AB????CD????>
 *      It would not make sense to place AB together in a Group
 *      Why? Since A and B are in an UnOrderedGroup with other tasks, we know that in some past demo, the two tasks were separated by C or D.
 *
 * So, all we need to to Incorporate an UnOrderedGroup into a demo is...
 *      1. find all the children and their pieces. 
 *      2. if they are all contiguous, make them into one UnOrderedGroup with the same label as the group we are incorporating
 *      3. otherwise, relabel them all as pieces of this unordered group
 *
 */

public class UnOrderedIncorporator extends Incorporator {

    public UnOrderedIncorporator(
            UnOrderedGroup unOrderedGroupToIncorporateIntoDemo,
            List<Task> partiallyIncorporatedDemo) {
        demo = partiallyIncorporatedDemo;
        group = unOrderedGroupToIncorporateIntoDemo;

    }

    public void incorporate() {

        // instead of finding subTasks in the demo, lets try and find the demo
        // tasks into the group
        // this lets us leverage the demo.contains method with O(1)
        // O(demo.length) or O(3*demo.length).... who cares lets just make this
        // code readable!

        if (allGroupSubTasksAreContiguous())
            replaceSingleChunkOfGroupSubTasks();
        else
            relabelAllGroupSubTasksAsPieces();

    }

    private boolean allGroupSubTasksAreContiguous() {
        boolean started = false;
        boolean ended = false;
        for (Task task : demo) {
            if (group.contains(task)) {
                started = true;
                if (ended)
                    return false; // hit a second hunk of subTasks
            } else {
                if (started)
                    ended = true;
            }
        }
        return started;
    }

    private void replaceSingleChunkOfGroupSubTasks() {
        List<Task> mySubTasks = new LinkedList<Task>();
        Integer start = null;
        Integer end = null;
        int index = 0;

        for (Task task : demo) {
            if (group.contains(task)) {
                if (start == null) {
                    start = index;
                }
                end = index;
                mySubTasks.add(task);
            } else {
                if (start != null)
                    break;
            }
            index++;
        }

        UnOrderedGroup replacement = new UnOrderedGroup(group.getLabel()
                .copyLabel(), null, mySubTasks);
        replaceTasksInDemo(start, end, replacement);
    }

    private void replaceTasksInDemo(Integer start, Integer end,
            Task replacementTask) {
        int demoSize = demo.size();
        List<Task> startOfDemo = demo.subList(0, start);
        List<Task> endOfDemo = demo.subList(end + 1, demoSize);
        List<Task> newDemo = new ArrayList<Task>(demoSize - end + start - 1);
        newDemo.addAll(startOfDemo);
        newDemo.add(replacementTask);
        newDemo.addAll(endOfDemo);

        demo.clear();
        demo.addAll(newDemo);
    }

    private void relabelAllGroupSubTasksAsPieces() {
        List<Task> allMyPieces = findAllMyPieces();
        int brotherhoodSize = allMyPieces.size();
        int index = 0;
        for (Task task : allMyPieces) {
            if (group.contains(task)) {
                task.setLabel(new PieceLabel(group.getLabel().getId(),
                        brotherhoodSize, index++));
            }
        }

    }

    private List<Task> findAllMyPieces() {
        List<Task> allMyPieces = new LinkedList<Task>();
        for (Task task : allMyPieces) {
            if (group.contains(task)) {
                allMyPieces.add(task);
            }
        }
        return allMyPieces;
    }

}
