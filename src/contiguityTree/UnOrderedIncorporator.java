package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UnOrderedIncorporator extends Incorporator {

    public UnOrderedIncorporator (UnOrderedGroup unOrderedGroupToIncorporateIntoDemo, List<Task> partiallyIncorporatedDemo) {
        demo = partiallyIncorporatedDemo;
        group = unOrderedGroupToIncorporateIntoDemo;
        
    }
    
    //all we need to do is find all the children and their pieces. 
    //if they are all contiguous, make them into one UnOrderedGroup with the same label as the group we are incorporating
    //otherwise, make them all pieces of this unordered group
    
    public void incorporate() {
        //instead of finding group children in the demo, lets find the demo into the group
        //this lets us leverage the demo.contains method with O(1)
        //thus this entire process is O(demo.length) or O(3*demo.length).... who cares lets just make this code readable!
        
        
        if (allGroupSubTasksAreContiguous()) replaceSingleChunkOfGroupSubTasks();
        else relabelAllGroupSubTasksAsPieces();

    }
    
    private boolean allGroupSubTasksAreContiguous () {
        boolean started=false;
        boolean ended=false;
        for (Task task : demo){
            if (group.contains(task)) {
                started=true;
                if (ended) return false; //hit a second hunk of subTasks
            }
            else {
                if (started) ended=true;
            }
        }
        return false;
    }
    
    private void replaceSingleChunkOfGroupSubTasks() {
        List<Task> mySubTasks = new LinkedList<Task>();
        Integer start=null;
        Integer end=null;
        int index=0;
        
        for (Task task : demo){
            if (group.contains(task)) {
                if (start==null) {start=index;}
                end = index;
                mySubTasks.add(task);
            }
            else {
                if(start!=null) break;
            }
            index++;
        }
        
        UnOrderedGroup replacement = new UnOrderedGroup(group.getLabel().copyLabel(), null, mySubTasks);
        replaceTasksInDemo(start, end, replacement);
    }
    
    private void replaceTasksInDemo (Integer start, Integer end, Task replacementTask) {
        int demoSize = demo.size();
        List<Task> startOfDemo = demo.subList(0, start);
        List<Task> endOfDemo = demo.subList(end+1, demoSize);
        List<Task> newDemo = new ArrayList<Task>(demoSize-end+start-1); 
        newDemo.addAll(startOfDemo);
        newDemo.add(replacementTask);
        newDemo.addAll(endOfDemo);
        
        demo.clear();
        demo.addAll(newDemo);
    }
    
    private void relabelAllGroupSubTasksAsPieces(){
        List<Task> allMyPieces = findAllMyPieces();
        int brotherhoodSize = allMyPieces.size();
        for (Task task : allMyPieces){
            if (group.contains(task)){
                task.setLabel( new PieceLabel(group.getLabel().getId(), brotherhoodSize) );
            }
        }
        
    }
    
    private List<Task> findAllMyPieces() {
        List<Task> allMyPieces = new LinkedList<Task>();
        for (Task task : allMyPieces){
            if (group.contains(task)){
                allMyPieces.add(task);
            }
        }
        return allMyPieces;
    }
    

}
