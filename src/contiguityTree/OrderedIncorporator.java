package contiguityTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/*
 * RULES FOR THE PROGRAMMER:
 *  - Never change the labels of Tasks in the old Contiguity Tree
 *      - Only Tasks in the demo which have PieceLabels ever need to be relabeled (after they are resolved)
 *      - This is important for the HashSets used to dynamically track pieces
 *  - Two Tasks are equal as long as they have equal Labels
 *      - Labels are equal as long as they have the same id (and the same brotherUUID if PieceLabel)
 */



public class OrderedIncorporator {
	
    ContiguousGroupFinder finder;
    Validator validator;
    GroupBuilder builder;
    OrderedGroup orderedGroup;
    List<Task> demo;
    
    public OrderedIncorporator (OrderedGroup orderedGroupForEncorporation, List<Task> partiallyEncorporatedDemonstration) {
        orderedGroup = orderedGroupForEncorporation;
        demo = partiallyEncorporatedDemonstration;
        finder = new ContiguousGroupFinder( (List<Task>)orderedGroup.getSubTasks(), demo);
        validator = new Validator(demo);
        if (orderedGroup.isReversible()) { builder = new GroupBuilderFromReversible(demo.size()); }
        else { builder = new GroupBuilderFromNonReversible(demo.size()); }
    }
    
    
	

	
	
    
    
    
    
	
	//************************************************************************************************//
    //*************************************** FINDER CLASSES *****************************************//
    //************************************************************************************************//
	
	/*
     * These classes are used to dynamically find all the groups of tasks which are contiguous
     * in both the OrderedGroup's subTasks and in the demo
     */
	
	private class ContiguousGroupFinder {
	    private PieceIndexTracker[][] books;
	    List<Task> subTasks, demo;
	    
	    public ContiguousGroupFinder (List<Task> orderedGroupSubTasks, List<Task> partiallyEncorporatedDemonstration) {
	        int index=0;
            for (Task demoTask : demo){
                books[0][index] = new PieceIndexTracker(orderedGroupSubTasks, demoTask);
            }
	    }
	
    	//Imagine we are incorporating OrderedGroup oGroup into the List<Task> demo.
    	//oGroup also contains a List<Task>.
    	//Let PieceIndexTracker tracker be used to track the Tasks in the demo from index 4 to 7.
    	//minIndex is the smallest index of a Task in oGroup which also exists in demo[4,7].
    	//maxIndex is the largest index of a Task in oGroup which also exists in demo[4,7].
    	//We define exists to include tasks in the demo which are Pieces of a Task in oGroup.
    	//The pieces Map is used to keep track of these Pieces. We can only form a new Group if we have all the Pieces of a Task.
    	private class PieceIndexTracker {
    		protected int minIndex, maxIndex;
    		protected HashSet<Task> tasksOfMyPieces;
    		protected HashSet<Task> myPieces;
    		//When merging two PieceIndexTrackers, I just need to add the highest index task of one to the pieces of the other
    		protected Task taskOfMyHighestIndexOriginalTask; 
    		protected Task highestIndexOriginalTask;
    		protected int desiredNumberOfPieces;
    		protected int numberOfDemoTasksTracked; //my height in books[][]
    		
    		public PieceIndexTracker (List<Task> subTasks, Task task) {
    			int indexInGroup = lenientIndexOfSubTask(subTasks, task);
    			if (indexInGroup==-1) throw new Error ("Lists must contain the same set of unique elements when building a Contiguity Tree");
    			minIndex = indexInGroup;
    			maxIndex = indexInGroup;
    			
    			//A vague copy create a task with an equivalent label
    			//Why? b/c this task may become relabeled when it becomes part of a group (the new Contiguity Tree should not contain any Tasks with PieceLabels)
    			//Since we are tracking pieces using a label-based hash, we need persistent labels
    			highestIndexOriginalTask = task.vagueCopy(); 
    			taskOfMyHighestIndexOriginalTask = subTasks.get(indexInGroup); 
    			
    			myPieces = new HashSet<Task> ();
    			tasksOfMyPieces = new HashSet<Task> ();
    			
    			if (highestIndexOriginalTask.isPiece()) {
    				myPieces.add(highestIndexOriginalTask);
    				tasksOfMyPieces.add(taskOfMyHighestIndexOriginalTask);	
    				desiredNumberOfPieces = highestIndexOriginalTask.getNumberOfBrothers();
    			}
    			else {
    				desiredNumberOfPieces=0;
    			}
    			
    			numberOfDemoTasksTracked = 1;
    		}
    		
    		public PieceIndexTracker (PieceIndexTracker tracker1, PieceIndexTracker tracker2) {
    			minIndex = Math.min(tracker1.minIndex, tracker2.minIndex);
    			maxIndex = Math.min(tracker1.maxIndex, tracker2.maxIndex);
    			numberOfDemoTasksTracked = tracker1.numberOfDemoTasksTracked+1;
    			copyPieceTrackingFrom(tracker1);
    			addHighestIndexTaskFrom(tracker2);
    		}
    		
    		//NOTE: We are making stealing and editing the Sets from the layer below us. This is OK as we no longer need them. 
    		//However, we must take note that tracker1 is no longer valid for dynamic computing
    		private void copyPieceTrackingFrom (PieceIndexTracker tracker1) {
    			myPieces = tracker1.myPieces;
    			tasksOfMyPieces = tracker1.tasksOfMyPieces;
    			desiredNumberOfPieces = tracker1.desiredNumberOfPieces;
    		}
    		
    		private void addHighestIndexTaskFrom (PieceIndexTracker tracker2) {
    			highestIndexOriginalTask = tracker2.highestIndexOriginalTask;
    			taskOfMyHighestIndexOriginalTask = tracker2.taskOfMyHighestIndexOriginalTask;
    			
    			if (highestIndexOriginalTask.isPiece()) {
    				myPieces.add(highestIndexOriginalTask);
    				
    				//If we are adding a new set of pieces to track, we must update our piece count
    				if (!tasksOfMyPieces.contains(taskOfMyHighestIndexOriginalTask)) {
    					tasksOfMyPieces.add(taskOfMyHighestIndexOriginalTask);
    					desiredNumberOfPieces += highestIndexOriginalTask.getNumberOfBrothers();
    				}
    			}
    		}
    		
    		public boolean candidateForGrouping () {
    			if (!allPiecesHaveAllTheirBrothers()) return false;
    			int rangeOfIndices = maxIndex - minIndex;
    			if (rangeOfIndices + desiredNumberOfPieces == numberOfDemoTasksTracked) return true;
    			return false;
    		}
    		
    		public boolean allPiecesHaveAllTheirBrothers () {
    			return desiredNumberOfPieces == myPieces.size();
    		}
    		
    		public boolean containsPeices () {
    			return desiredNumberOfPieces > 0;
    		}
    		
    		private int lenientIndexOfSubTask (List<Task> subTasks, Task task) {
                int index=0;
                for (Task subTask : subTasks) {
                    if (subTask.lenientEquals(task)) return index;
                    index++;
                }
                return -1;
            }
    	}
	
	}
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
    //************************************** VALIDATION CLASS ****************************************//
    //******************************************************************//
	
	/*
     * This class is used to ensure that the contiguous groups merit to be built into the Contiguity Tree
     */
	
	private class Validator {
	    private Task[] upToDateDemoTasks;
	    
	    public Validator (List<Task> demo) {
	        upToDateDemoTasks = new Task[demo.size()];
	        int index=0;
            for (Task demoTask : demo){
                upToDateDemoTasks[index++] = demoTask;
            }
	    }
	    
	    //validate
	    
	    //call the builder class, and recieve new Task
	    
	    //use Task to update upToDateDemoTasks
	}
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
	//************************************** BUILDER CLASSES *****************************************//
	//************************************************************************************************//
	
	/*
	 * These classes are used to build the new Contiguity Tree
	 */
	
	/*
	 * --DEFINITIONS--
     * Absorb: When a Group, parent, deletes one of its subTasks, child, and adds all of child's subTasks  
     *    A                                    A
     *   / \         --------------->        / | \
     *  B   C          A.absorb(C)          B  D  E 
     *     / \       --------------->
     *    D   E
     */
	
	/* 
	 * General Rule:
     *  - If we are making an OG containing another recently made subTask in the same direction, absorb it
     *  
     * EX:
     *   rOG = reversible OrderedGroup
     *   We want to avoid  non-reversible OrderedGroup(ABCDE)  +  demo=EDCBA  from becoming
     *   
     *                  rOG
     *                 /   \                                       rOG
     *               rOG    A              instead we want:      / /|\ \   
     *              /   \                                       E D C B A 
     *           rOG    rOG
     *           / \    / \
     *          E   D  C   B  
     *          
     *  
     * Implementation:
     *   Since we only want to absorb "recently created OGs", we initialize the original Task direction to 0 (default)
     *   Why? We do not want to accidentally absorb a pre-existing rOG into a new rOG.
     *   It is totally legitimate to have a reversible within a reversible
     */
	
    private abstract class GroupBuilder {
        private int[] directionOfTasks;
        public GroupBuilder (int demoLength) {
            directionOfTasks = new int[demoLength]; //default values of 0 so original Tasks will never be absorbed
        }
    }
   
	
	
    /*
     * This class is used when incorporating a reversible oGroup
     * 
     * We follow these rules:
     * - If a group of tasks contained pieces we make an unordered group
     * - If a group of tasks is in the same direction in the oGroup and in the demo:
     *       - make a non-reversible OrderedGroup
     *       - note that the direction was the same (1)
     * - If a group of tasks is in the opposite direction:
     *       - make a reversible OrderedGroup
     *       - note that the direction was opposite (-1)
     * - Otherwise make an unordered group and note that the direction was scrambled (0)
     */
	private class GroupBuilderFromNonReversible extends GroupBuilder {
		public GroupBuilderFromNonReversible (int demoLength){
		    super(demoLength);
		}
	}
	
	/*
	 * This class is used when incorporating a reversible oGroup
     * 
     * We follow the same rules as a GroupBuilderFromNonReversible except we ALWAYS make reversible OGs INSTEAD of non-reversible OGs
     * Why? Obviously, if the sequence of Tasks already existed in a reversible, it must have previously been seen in both direction
     */
	private class GroupBuilderFromReversible extends GroupBuilder {
	    public GroupBuilderFromReversible (int demoLength){
            super(demoLength);
        }
	}
}









































//This is needed when dealing with Reverses

/*
* This function takes in two lists of Tasks and returns a Contiguity Tree
* The lists must be composed of the same set of unique Tasks 
* We dynamically compute all contiguous groups
*/
public void buildContiguityTree(OrderedGroup group, List<Task> demo) {
   
   int size = demo.size();
   if (size<group.getSize()) throw new Error("The demo can not be have less tasks than the current group does");
   
   //the following arrays will be used for book-keeping
   upToDateDemoTasks = new Task[size]; 
   books = new PieceIndexTracker[size][size];       
   
   /*We initialize max[0][..] and min[0][..] s.t. [0][i] = group.indexOf( demo.get(i) )
   * We dynamically compute the remaining max s.t. max[i][j]=Math.max(max[i-1][j],max[i-1][j+1])
   * We dynamically compute the remaining min s.t. max[i][j]=Math.min(min[i-1][j],min[i-1][j+1])
   * Let range = max-min
   * Iff range[i][i] == i then l2.get(i..i+j) is contiguous. (not true for i=0)
   *
   * Example:
   * list1 = A,B,C,D,E,F
   * list2 = E,F,A,C,D,B 
   *                                                               
   * max[0] = [4, 5, 0, 2, 3, 1]  min[0] = [4, 5, 0, 2, 3, 1]             [E, F, A, C, D, B]
   * max[1] = [5, 5, 2, 3, 3]     min[1] = [4, 0, 0, 2, 1]    max-min-i = [0, 4, 1, 0, 1]    // the two 0 signify that groups EF and CD are both contiguous in the two lists
   * max[2] = [5, 5, 3, 3]        min[2] = [0, 0, 0, 1]       max-min-i = [3, 3, 1, 0]       // 0 signifies that CDB are contiguous in the two lists
   * max[3] = [5, 5, 3]           min[3] = [0, 0, 0]          max-min-i = [2, 2, 0]          // 0 signifies that ACDB are contiguous in the two lists
   * max[4] = [5, 5]              min[4] = [0, 0]             max-min-i = [1, 1]             // neither EFACD or FACDB are contiguous in the two lists
   * max[5] = [5]                 min[5] = [0]                max-min-i = [0]                // of course, EFACDB is a contiguous group in the two lists
   *
   * Why? This makes sense because we want the range of a group to be equal to its size. 
   * A group 7, 9, 6, 8 has a range of 4 and a size of 4. However, 7, 10, 6, 8 has a range of 5 and a size of 4.
   */

   setUpBooks(group,demo);
   
   dynamicallyFindContiguousGroups();
}

//Copy list2 into an list2tasks
//Initialize min[0][..] and max[0][..]


//dynamically compute range
//when range == i, we have found a contiguous group
private void dynamicallyFindContiguousGroups( ){
   int size = books[0].length;
   
   //build pyramid
   for (int i=1; i<size; i++){
       for (int j=0; j<=size-i-1; j++){
           books[i][j]=new PieceIndexTracker(books[i-1][j],books[i-1][j+1]);
           if (books[i][j].candidateForGrouping()) processContiguousGroup(j,j+i);
       }
   }
}

/*Not all contiguous groups merit to become a Task in the final Contiguity Tree
* For example, assume list1 = ABCD && list2 = ABCD
* Initially, list2tasks = 0 1 2 3.
* 
* We first dynamically discover group AB. We turn that into a task OrderedGroup#5: <AB>
* Now, list2tasks = 5 5 2 3.
* 
* Next, we see that BC is also a contiguous group. 
* However, since B has already been coupled with A via Task#5, we cannot "steal" B into a new task. 
* 
* Next, we see that CD is also a contiguous group. 
* Since neither C or D are in still in their own tasks, we turn that into a task OrderedGroup#6 <CD>.
* Now, list2tasks = 5 5 6 6.
* 
* We find, but ignore, groups ABC and BCD since C is already coupled with D and B is coupled with A.
* 
* Finally, we find group ABCD. Since forming task OrderedGroup#7 <<AB><CD>> does not involve breaking apart Task#5 or #6, we proceed.
* Now, list2tasks = 7 7 7 7.
* This ContiguityTree legitimately represents all of the contiguous groups. 
* However, when we create Task #7, the OrderedGroup is smart enough to know it should remove unnecessary children (other OrderedGroups).
* Thus, Task#7 = <ABCD>. Task#5 and Task #6 have been absorbed by #7, as the information they represented is encoded in Task#7.
* 
* The final Contiguity Tree is an Ordered Group <ABCD>.
*/
private void processContiguousGroup(int start, int end) {
   
   ArrayList<Task> tasksInL2Order = new ArrayList<Task>();
   ArrayList<Integer> L1IndexOfTasksInL2Order = new ArrayList<Integer>();
   
   /*As described above, when building a Task, we must ensure that we are not breaking up any existing tasks
   * While we do this, we can go ahead and find out if the task is Ordered(reversible?) or Unordered
   * We accomplish this by tracking the tasks we would like to combine into a new task
   *   and by tracking the index of these tasks with respect to list1
   *   For example, if the tasks (when traversed w respect to list2) have decreasing indices with respect to list1,
   *       then we know the tasks are in the opposite order 
   */
   
   //check first edge to ensure we are not breaking apart a pre-existing task
   if (!list2tasks[start].equals(list2tasks[start+list2tasks[start].absoluteSize()-1])) return;
   
   //record each subtask and their index with respect to list 1
   int k =start;
   while(k<=end){
       tasksInL2Order.add(list2tasks[k]);
       L1IndexOfTasksInL2Order.add(min[0][k]);
       k+=list2tasks[k].absoluteSize();
   }
   
   //check last edge to ensure we are not breaking apart a pre-existing task
   if (k!=end+1) return;
   
   //determine the GroupType based on L1IndexOfTasksInL2Order's numeric ordering
   int direction = determineDirection(L1IndexOfTasksInL2Order);
   
   buildNode(start, end, tasksInL2Order, direction);
}

//return 1 if the tasks are in the same order
//return -1 if in opposite order (reversed)
//return 0 if scrambled
private int determineDirection(ArrayList<Integer> order){
   int initialDirection = order.get(1)-order.get(0);
   for (int i=order.size()-1; i>=2; i--){
       int tempDirection = order.get(i)-order.get(i-1);
       if (initialDirection*tempDirection<0) return 0; 
   }
   return (int) Math.signum(initialDirection);
}

private void buildNode(int start, int end, ArrayList<Task> tasksInL2Order, int direction){
   Task node;
   if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
       node = new OrderedGroup(new Label(), null, tasksInL2Order, false); //false bc task order is not reversible
   }
   else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
       node = new OrderedGroup(new Label(), null, tasksInL2Order, true); //true bc task order is reversible
   }
   else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
       node = new UnOrderedGroup(new Label(), null, tasksInL2Order);
   }
   else {throw new Error("Unsupported direction passed into buildNode()");}
   
   for (int i=start; i<=end; i++) {
       list2tasks[i]= node;
   }
}