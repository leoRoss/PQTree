package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
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
    
    //Traversal Objects
    private Task traversalCopy;
    private Task traversalState;
    List <Primitive> nextPossibleTasks;
    
    private List <Object> incrementalDemo;
    
    public ContiguityTree() {}
    public ContiguityTree(Task myHead) {head=myHead;}

    /* 
    * Take in a list of unlabeled primitive actions (the demo) and incorporate into the existing tree
    * If incorporation fails, the demo is ignored and false is returned
    * This could occur if the RULES (above) are not respected
    */
    private boolean incorporate(List<Task> demo) {
        if (demo==null || demo.size()==0) return false;
        
        if (head == null) {
            head = new OrderedGroup(new Label(), null, demo, false); // null parent, false because sequence is not reversible
            return true;
        } 
        
        try {
            head.incorporate(demo);
        } 
        catch (IncorporationError e){
            return false;
        }
        
        if (demo.size() != 1) return false; //the demo should now be a single task... the new tree
        head = demo.get(0);
        return true;
    }
    
    
    //The following two methods should be used to send a new sequence of Objects (List or Array) to the Tree
    //They return false if unsuccessful
    public boolean observeDemo(List<Object> demo) {
        List<Task> wrappedDemo = new ArrayList<Task>(demo.size());
        for (Object obj : demo) {
            wrappedDemo.add(new Primitive(obj)); // null parents for now
        }
        return incorporate(wrappedDemo);
    }
    
    public boolean observeDemo(Object[] demo) {
        List<Task> wrappedDemo = new ArrayList<Task>(demo.length);
        for (int i = 0; i < demo.length; i++) {
            wrappedDemo.add(new Primitive(demo[i])); // null parents for now
        }
        return incorporate(wrappedDemo);
    }
    
    
    public void startNewDemo() {
        incrementalDemo= new LinkedList <Object> ();
    }
      
    public void sendNextDemoObject(Object obj){
        incrementalDemo.add(obj);
    }
      
    public void sendDemo() {
        observeDemo(incrementalDemo);
        incrementalDemo= new LinkedList <Object> ();
    }
    
    public boolean equals (ContiguityTree tree) {
        if (head==null) return false;
        if (tree.head==null) return false;
        return head.contentEquals(tree.head);
    }
    
    //Create a full copy of the tree. Only the Objects stored in the Primitives are the same 
    public ContiguityTree fullCopy () {
        return new ContiguityTree(head.fullCopy());
    }
    
    
    /*
     * TRAVERSAL METHODS
     * 
     * The clean/safe way to traverse a Tree is to
     * tree.startNewTraversal()
     * Object[] myOptions = tree.nextPossibleObjects()
     * while (myOptions.length>0) {
     *      Object myChoice = //some Object from the myOptions Array
     *      tree.choseObject(myChoice);
     *      myOptions = tree.nextPossibleObjects()
     * }
     *  
     * However, choseObject(Object obj) can be called without calling nextPossibleObjects()
     * That being said, choseObject will return false if the Object is not legal
     * 
     * Thus, confident people could simply...
     * 
     * tree.startNewTraversal();
     * Object[] whatIThinkIsALegalStart = //make an array of objects you think respects all the rules when starting a traversal
     * int index=0;
     * while (tree.choseObject(whatIThinkIsALegalStart[index++])) {}
     * Now, you could finish of the traversal in a safe way, knowing your first index actions were successfully executed
     * 
     * IMPORTANT: Always call startNewTraversal() when you want to start a fresh traversal of the tree
     */
    
    public void startNewTraversal() {
        traversalCopy = head.fullCopy();
        traversalState = traversalCopy;
    }
    
    public Object[] nextPossibleObjects() {
        nextPossibleTasks = getNextPossibleTasks();
        Object[] nextPossibleActions = new Object[nextPossibleTasks.size()];
        int index=0;
        for (Primitive possibleTask :  nextPossibleTasks) {
            nextPossibleActions[index++]= possibleTask.getObject();
        }
        return nextPossibleActions;
    }
    
    //choseObject 
    public boolean choseObject(Object obj) { 
        if (nextPossibleTasks==null) {nextPossibleTasks = getNextPossibleTasks();}
        for (Primitive possibleTask :  nextPossibleTasks) {
            if(possibleTask.objectIs(obj)) {
                traversalState = possibleTask.executeInTraversal(null);
                nextPossibleTasks=null;
                return true;
            }
        }
        
        //they chose an invalid options
        return false;   
    }
    
    private List<Primitive> getNextPossibleTasks(){
        List<Primitive> nextPossibleTasks = new LinkedList<Primitive>();
        if (traversalState!=null) traversalState.getNextPossibleTasks(nextPossibleTasks);
        return nextPossibleTasks;
    }
    
    
    //Experiment Related Methods
    public List<Object> randomlyTraverse () {
        List<Object> traversal = new LinkedList<Object> ();
        startNewTraversal();
        Object[] myOptions = nextPossibleObjects();
        while (myOptions.length>0) {
             Object myChoice = myOptions[(int) Math.floor(Math.random()*myOptions.length)];//some Object from the myOptions Array
             choseObject(myChoice);
             traversal.add(myChoice);
             myOptions = nextPossibleObjects();
        }
        return traversal;
    }
    
    public int recreateTreeUsingRandomTraversalsAsDemonstrations () {
        int numberOfDemos = 0;
        ContiguityTree copy = new ContiguityTree();
        while(!copy.equals(this)) {copy.observeDemo(randomlyTraverse()); numberOfDemos++;}
        return numberOfDemos;
    }
    
    public void print() { head.printMe(0);}

}
