package contiguityTree;

import java.util.ArrayList;
import java.util.List;

/* 
 * By: Leo Rossignac-Milon
 * 
 * This ContiguityTree Object is responsible for interfacing between requests and the actual Contiguity Tree
 * This object receives new demos and allows for the current Tree to be traversed
 * Moreover, the current tree can be traversed while new demos are being sent (it makes a copy of the tree for traversal)
 * 
 * RULES:
 *      All demos sent to a tree must contain the exact same set of unique actions
 *      In other words, there must be a 1-1 mapping of the objects in demo1 to the objects in demo2 with respect to the equals() method
 *      Example: demo1= List: A, B, C, D, E   and demo2 = E, B, C, D, A
 *      
 * GENERAL CONCEPT:
 *      A Contiguity Tree creates a task model from a series of demonstrations
 *      Please read www.leorossignacmilon.com/projects/ContiguityTree/main.php for a tutorial with examples
 *      
 *      Now that you have familiarized with the general concept, let's move onto implementation...
 *          
 *       
 * IMPLEMENTATION TO-KNOW:
 *      There are 4 types of Tasks in a ContiguityTree:
 *              Group:
 *                  UnOrdered:
 *                      The subTasks can be executed in any order
 *                  Ordered:
 *                      reversible=false: The subTasks can be executed sequentially
 *                      reversible=true: The subTasks can be executed either sequentially or in the exact opposite order
 *              Primitive:
 *                  A single low-level Action/Object from a demo
 *                  
 *      Tasks contain a Label.
 *      Tasks are always equal if their Labels are equal
 *      
 *      There are two types of Labels:
 *          Label: A Contiguity Trees only contains these types of labels. They simply have a int id as an identifier
 *          PieceLabel: These labels are a subclass of Label. They only exist during the incorporation phase.
 *                      This phase occurs when a Task T in an existing Tree is attempting to copy its information/structure into a new demo
 *                      If Task T does not find all of its subTasks in one contiguous/unseparated group, 
 *                          a pieceLabel is assigned to each of the resolved Tasks 
 *      Labels are always equal if their id are the same
 *      
 *      Incorporation is the process of changing a demo into the new Tree.
 *      For more information on how incorporation occurs, please refer to the simple UnorderedIncorporator and the much more complex OrderedIncorporator
 */

public class ContiguityTree {
    private Task head;

    public ContiguityTree() {

    }

    /* 
    * Take in a list of unlabeled primitive actions (the demo) and incorporate into the existing tree
    * If incorporation fails, the demo is ignored and an Error is raised.
    * This could occur if the RULES (above) are not respected
    */
    private void incorporate(List<Task> demo) {
        if (head == null) {
            head = new OrderedGroup(new Label(), null, demo, false); // null parent, false because sequence is not reversible
        } else {
            head.incorporate(demo);
            if (demo.size() != 1)
                throw new Error(
                        "The tree did not reduce the list to a single task");
            head = demo.get(0);
        }
    }
    
    
    //The following two methods should be used to send a new sequence of Objects (List or Array) to the Tree
    public void observeDemo(List<Object> demo) {
        List<Task> wrappedDemo = new ArrayList<Task>(demo.size());
        for (Object obj : demo) {
            wrappedDemo.add(new Primitive(obj)); // null parents for now
        }
        incorporate(wrappedDemo);
    }

    public void observeDemo(Object[] demo) {
        List<Task> wrappedDemo = new ArrayList<Task>(demo.length);
        for (int i = 0; i < demo.length; i++) {
            wrappedDemo.add(new Primitive(demo[i])); // null parents for now
        }
        incorporate(wrappedDemo);
    }
    
    
    
    public void print() { head.printMe(0);}

}
