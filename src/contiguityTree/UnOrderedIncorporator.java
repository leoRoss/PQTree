package contiguityTree;

import java.util.List;

public class UnOrderedIncorporator extends Incorporator {

    public UnOrderedIncorporator (UnOrderedGroup unOrderedGroupToIncorporateIntoDemo, List<Task> partiallyIncorporatedDemo) {
        demo = partiallyIncorporatedDemo;
        group = unOrderedGroupToIncorporateIntoDemo;
        
    }
    @Override
    //all we need to do is find all the children and their pieces. 
    //if they are all contiguous, make them into one UnOrderedGroup with the same label as the group we are incorporating
    //otherwise, make them all pieces of this unordered group
    public void incorporate() {
        //instead of finding group children in the demo, lets find the demo into the group
        //this lets us leverage the demo.contains method with O(1)
        //thus this entire process is O(demo.length)
        //TODO
        throw new Error ("Unimplemented!");

    }

}
