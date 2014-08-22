package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

/*
 * RULES FOR THE PROGRAMMER:
 *  - Never change the labels of Tasks in the old Contiguity Tree
 *  - A new group can never contain a Task with a PiecesLabel for hashing reasons
 *      - These tasks must be relabeled before they are added to a new group
 *  - Two Tasks are equal as long as they have equal Labels
 *      - Labels are equal as long as they have the same id
 *      - This is needed so that group.contains(Task T with a PieceLabel) will return true if T as if T has a normal Label
 */


/* 
 * I have separated out the steps of the dynamically incorporated process to make it more understandable to a human
 *
 * Reading the comments top down should give you a thorough understanding of the process of incorporating a OrderedGroup into the demo
 */

public class OrderedIncorporator extends Incorporator{
    
    private final boolean DEBUGGING = false;
    
    private ContiguousGroupFinder finder; //find contiguous groups - candidates to become nodes in the new tree
    private Validator validator; //validate these groups using the index
    private GroupBuilder builder; //build nodes in the new tree
    
    public OrderedIncorporator (OrderedGroup orderedGroupToIncorporateIntoDemo, List<Task> partiallyIncorporatedDemo) throws IncorporationError {
        demo = partiallyIncorporatedDemo;
        group = orderedGroupToIncorporateIntoDemo;
        int demoSize = demo.size();
        if (demoSize<group.getSize()) throw new IncorporationError("The demo can not have less tasks than the group does");
        
        finder = new ContiguousGroupFinder( (OrderedGroup)group, demo);
        int [] indexOfDemoTasksInGroup = finder.indexOfDemoTasksInGroup();
        validator = new Validator(demo, indexOfDemoTasksInGroup);
        if (group.isReversible()) { builder = new GroupBuilderFromReversible(demoSize); }
        else { builder = new GroupBuilderFromNonReversible(demoSize); }
    }
    
    
    public void incorporate () throws IncorporationError {
        int [] contiguousCandidate = finder.nextGroup();   // [start index in demo, end index in demo, number of tasks with PieceLabels in the group]
        
        while (contiguousCandidate!=null) { //while the finder continues to find contiguous groups   
            VerifiedGroupOfTasks verifiedGroup = validator.validate(contiguousCandidate[0], contiguousCandidate[1]);
            if (verifiedGroup != null) { //if the indices were validated
                Task task = builder.buildTask(verifiedGroup, contiguousCandidate[2]); //build the task from the indices in the demo
                if (DEBUGGING) {System.out.println("    Made Task:"); task.printMe(2); System.out.println(); System.out.println();}
                validator.update(contiguousCandidate[0], contiguousCandidate[1], task, verifiedGroup.direction); //update our books for the validator with the newly made task
            }
            contiguousCandidate = finder.nextGroup(); 
        }
        
        validator.updateDemo(demo, group.getLabel().getId() ); //change the demo to reflect all the changes we have decided to make  
    }
    
	

	
	
    
    
    
    
	
	//************************************************************************************************//
    //*************************************** FINDER CLASSES *****************************************//
    //************************************************************************************************//
	
	/*
     * These classes are used to dynamically find all the groups of tasks which are contiguous
     * in both the OrderedGroup's subTasks and in the demo
     */
    
    /* HOW WE COMPUTE CONITGUOUS GROUPS...
     * 
     * We use an 2D Array of PieceIndexTrackers, books, to keep keep information about a subgroup of the demo
     * books[i][j] keeps information about the subgroup demo(j..i+j)
     * 
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * Information: Max and Min
     * 
     * Step1: 
     *      For all j, we initialize books[0][..].max and books[0][..].min to = group.indexOf( demo.get(j) )
     * 
     * Dynamic Step:
     *      books[i][j].max=Math.max(books[i-1][j].max,books[i-1][j+1].max)
     *      books[i][j].min=Math.min(books[i-1][j].min,books[i-1][j+1].min)
     *      
     * Why?
     *      Let range = max-min
     *      Iff books[i][i].range == i then demo.get(j..i+j) is contiguous
     *
     *      Example:
     *          group = A,B,C,D,E,F
     *          demo  = E,F,A,C,D,B 
     *                                                               
     *          max[0] = [4, 5, 0, 2, 3, 1]  min[0] = [4, 5, 0, 2, 3, 1]             [E, F, A, C, D, B]
     *          max[1] = [5, 5, 2, 3, 3]     min[1] = [4, 0, 0, 2, 1]    max-min-i = [0, 4, 1, 0, 1]    // the two 0 signify that groups EF and CD are both contiguous in the two lists
     *          max[2] = [5, 5, 3, 3]        min[2] = [0, 0, 0, 1]       max-min-i = [3, 3, 1, 0]       // 0 signifies that CDB are contiguous in the two lists
     *          max[3] = [5, 5, 3]           min[3] = [0, 0, 0]          max-min-i = [2, 2, 0]          // 0 signifies that ACDB are contiguous in the two lists
     *          max[4] = [5, 5]              min[4] = [0, 0]             max-min-i = [1, 1]             // neither EFACD or FACDB are contiguous in the two lists
     *          max[5] = [5]                 min[5] = [0]                max-min-i = [0]                // of course, EFACDB is a contiguous group in the two lists
     *
     * Why? This makes sense because we want the range of a group to be equal to its size. 
     * A group 7, 9, 6, 8 has a range of 4 and a size of 4. However, 7, 10, 6, 8 has a range of 5 and a size of 4.
     * 
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * Information: Pieces
     * 
     * The above logic and example does not involve pieces. It is therefore, incorrect, but its a good place to start...
     * 
     * A more realistic situation would look more like this:
     *      group = A,B,C,D,E,F
     *      demo  = E,F',A,F',C',D,B,C'
     * 
     * As you can see, F' and C' exist in multiple places in the demo. The ' indicates that they are pieces of F and C.
     * This implies that when C and F attempted to incorporate themselves into the demo, 
     * they were unable to find an entire contiguous group with all their subtasks.
     * Instead, F (and C) found all the chunks of its subtasks in the demo, and did its best to resolve each chunk.
     * 
     * What does this mean for us?
     * A group is not truly contiguous if it has one piece without having all the matching pieces (its brothers).
     * 
     * In the example above, we cannot match C',D together, even though our range requirement above is satisfied.
     * Why?
     * Imagine C = a non-reversible ordered group [3,4,5]
     *      and D = [6,7]
     * By nature of Contiguity Trees, we know that C and D have NOT always appeared sequentially
     *      Why? Because, otherwise, we would only have a single non-reversible ordered group [3,4,5,6,7]
     * 
     * Without loss of Gen, lets pretend in past demos have seen CD and DC.... [3,4,5],[6,7] and [6,7],[3,4,5]
     * Now, in the demo, we have .... C',D,B,C' = {4,3}, [6,7], B, 5   where the first C`={4,3} and the last C' = 5
     * Obviously, saying that C',D (4,3,6,7) is a contiguous group is incorrect!
     * 
     * Thus, we must wait until we have all the pieces of C before forming a new group.
     * Moreover, this group C',D,B,C' must be an UnorderedGroup.
     * 
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * OK! So now we understand the issue of Pieces. 
     * We could obviously keep track of all the pieces in books[i][j] and check if we have any incomplete brotherhoods.
     * 
     * Instead,
     * Each book[][] keeps a hashset called myPeices used to track the brotherhoods it contains. 
     * Each book also keeps track of the desiredNumberOfPieces (instead of how many there really are) and the numberOfBrotherhoods
     * 
     * Dynamic Step:
     *      books[i][j] copies the above hashset and counter variables from books[i-1][j].myPeices
     *      
     *      Clearly, this is not sufficient. books[i][j] is supposed to track demo[j..i+j] but is only tracking demo[j..i-1+j]
     *      Obviously, we are need to track the missing demo[i+j] 
     *      Fortunately, books[i-1][j+1].highestIndexOriginalTask = demo[i+j]
     * 
     *      If demo[i+j] is a piece, we check if books[i][j].myPeices contains demo[i+j]
     *          Remember that all brothers of the same brotherhood will be hashed the same and are all equal! :)
     *          So now, if its a piece from a new brotherhood:
     *              we increase desiredNumberOfPieces by demo[i+j].sizeOfBrotherhood() and numberOfBrotherhoods++
     *              and we add demo[i+j] to the hashset, so the above operation is never repeated for that brotherhood
     *      Otherwise, demo[i+j] is a full task, and we numberOfFullTasks++;
     *      
     *      
     * The final test:
     *     
     *     demo[j..i+j] is contiguous iff 2 conditions are satisfied:
     *          books[i][j].range + 1 + books[i][j].desiredNumberOfPieces - numberOfBrotherhoods == i
     *          where: i = numberOfTasksTracked
     *     
     *     Ex:
     *          assume AAABBBBCCDDDDE is contiguous (has all the pieces in each brotherhood)
     *               
     *          number of tasks tracked = 14
     *          range +1 = 4-0+1 = 5
     *          the desire number of pieces = 3As + 4Bs + 2Cs + 4Ds = 13
     *          number of brotherhoods = 4 ... A, B, C, and D brotherhoods
     *          
     *          indeed 5+13-4 = 14 == 14
     *          
     *          As you can see below, this test is sufficient, as any action which would break contiguity always relatively increases the left side of the equation 
     *          
     *          Add a full from out of range:                        + more than 1   == +1       left side relatively greater
     *          Remove a full:                                              +0       == -1       left side relatively greater
     *          Add a piece from an out of range brotherhood  +more than 1 +size -1  == +1       left side relatively greater
     *          Add a entire brotherhood from out of range    +more than 1 +size -1  == +size    left side relatively greater
     *          Remove a piece from brotherhood                              +0      == -1       left side relatively greater
     *          Remove an entire brotherhood                           -size +1      == -size    left side relatively greater
     *          
     */
	
	private class ContiguousGroupFinder {
	    private PieceIndexTracker[][] books;
	    private int i,j;
	    
	    public ContiguousGroupFinder (OrderedGroup group, List<Task> demo) {
	        int demoSize = demo.size();
	        i=1; j=-1;
	        books = new PieceIndexTracker [demoSize][demoSize];
	        int index=0;
            for (Task demoTask : demo){
                books[0][index++] = new PieceIndexTracker(group, demoTask, demoSize);
            }
	    }
	    
	    //This function dynamically builds books until it finds a candidate, which it returns
	    //When it is called again, it continues building from where it left off
	    public int[] nextGroup () {
	        int size = books[0].length;
	        
	        //I must apologize for the ugly nested looping. Returning inside the nested loop must not affect the incrementing of i and j
	        //This allows us to return to where we left of :)
	        while (i<=size){
	            if (j+i==size-1) {i++; j=-1;}
	            while (j<size-i-1){
	                j++;
	                books[i][j]=new PieceIndexTracker(books[i-1][j],books[i-1][j+1]);
	                if (books[i][j].candidateForGrouping()) return new int[]{j,j+i, books[i][j].numberOfPeices()};
	            }
	        }
	        return null;
	    }
	    
	    public int [] indexOfDemoTasksInGroup () {
	        int size = books[0].length;
	        int [] indexOfDemoTasksInGroup = new int [size];
	        for (int i=0; i<size; i++) {
	            indexOfDemoTasksInGroup[i] = books[0][i].minIndex;
	        }
	        return indexOfDemoTasksInGroup;
	    }
	    
	    
	    
    	private class PieceIndexTracker {
    		protected int minIndex, maxIndex;
    		protected HashSet<Task> myPieces;
    		protected Task highestIndexOriginalDemoTask; //the original task in demo[j+i], used facilitate merging two PieceIndexTrackers
    		protected int desiredNumberOfPieces;
    		protected int numberOfBrotherhoods;
    		protected int numberOfDemoTasksTracked; //= i+1 in books[i][j] ie: size of demo(j..j+i)
    		
    		public PieceIndexTracker (OrderedGroup group, Task task, int demoSize) {
    			myPieces = new HashSet<Task> ();
                desiredNumberOfPieces=0;
                numberOfBrotherhoods=0;
                numberOfDemoTasksTracked = 1;
                
                int indexInGroup;
                if (group.contains(task)) {indexInGroup = group.lenientIndexOfSubTask(task); }
                else {indexInGroup = -1;}
                
    			if (indexInGroup==-1) { //the task does not exist in this group
    			    minIndex = -demoSize*100;
    			    maxIndex = demoSize*100; //this way no group containing this task will ever be a candidate
    			    
    			    highestIndexOriginalDemoTask = null; 
    			}
    			else {
        			minIndex = indexInGroup;
        			maxIndex = indexInGroup;
        			
        			//A vague copy creates a placeholder task with an equivalent label
                    //Since we are tracking pieces using a label-based hashset, we need persistent labels
        			//the highestIndexOriginalDemoTask may become relabeled when it becomes part of a group (a new Group should never contain any Tasks with PieceLabels)
                    highestIndexOriginalDemoTask = task.vagueCopy(); 
    			}
    			
    			if (highestIndexOriginalDemoTask!=null && highestIndexOriginalDemoTask.isPiece()) {
    				myPieces.add(highestIndexOriginalDemoTask);
    				desiredNumberOfPieces+= highestIndexOriginalDemoTask.getBrotherhoodSize();
    				numberOfBrotherhoods++;
    			}
    		}

            public PieceIndexTracker (PieceIndexTracker tracker1, PieceIndexTracker tracker2) {
                minIndex = Math.min(tracker1.minIndex, tracker2.minIndex);
    			maxIndex = Math.max(tracker1.maxIndex, tracker2.maxIndex);
    			numberOfDemoTasksTracked = tracker1.numberOfDemoTasksTracked+1;
    			numberOfBrotherhoods = tracker1.numberOfBrotherhoods;
    			copyPieceTrackingFrom(tracker1);
    			addHighestIndexOriginalDemoTaskFrom(tracker2);
    		}
    		
    		//NOTE: We are stealing (not copying) and further editing the HashSet from the books[][] directly below us. 
            //This is OK as we no longer need the layer below us to be valid - as long as we dynamically computer j=0->n. 
    		private void copyPieceTrackingFrom (PieceIndexTracker tracker1) {
    			myPieces = tracker1.myPieces;
    			desiredNumberOfPieces = tracker1.desiredNumberOfPieces;
    		}
    		
    		private void addHighestIndexOriginalDemoTaskFrom (PieceIndexTracker tracker2) {
    			highestIndexOriginalDemoTask = tracker2.highestIndexOriginalDemoTask;
    			
    			if (highestIndexOriginalDemoTask!= null && highestIndexOriginalDemoTask.isPiece()) {
    				//If we are adding a new set of pieces to track, we must update our piece count
    				if (!myPieces.contains(highestIndexOriginalDemoTask)) {
    				    myPieces.add(highestIndexOriginalDemoTask);
    					desiredNumberOfPieces+= highestIndexOriginalDemoTask.getBrotherhoodSize();
    					numberOfBrotherhoods++;
    				}
    			}
    		}
    		
    		public boolean candidateForGrouping () {
    			int rangeOfIndices = maxIndex - minIndex;
    			int supplementalPieces = desiredNumberOfPieces - numberOfBrotherhoods; //AAABC has 2 supplemental As
    			return rangeOfIndices + 1 + supplementalPieces == numberOfDemoTasksTracked;
    		}
    		
    		public int numberOfPeices() {
                return desiredNumberOfPieces;
            }
    	}
	
	}
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
    //************************************** VALIDATION CLASS ****************************************//
    //************************************************************************************************//
	
	/*
     * This class is used to ensure that the contiguous groups merit to be built into the Contiguity Tree
     */
	
	private class Validator {
	    private Task[] upToDateDemoTasks;
	    private int[] directionOfDemoTasks;
	    private int [] demoIndexedInGroup;
	    
	    public Validator (List<Task> demo, int [] indexedInGroup) {
	        int demoSize = demo.size();
	        upToDateDemoTasks = new Task[demoSize];
	        demoIndexedInGroup = new int [demoSize];
	        directionOfDemoTasks = new int [demoSize];
	        int index=0;
            for (Task demoTask : demo){
                upToDateDemoTasks[index] = demoTask;
                demoIndexedInGroup[index] = indexedInGroup[index];
                directionOfDemoTasks[index] = 0; //unnecessary initialization for clarity
                index++;
            }
	    }


        /*
	     * Not all contiguous groups merit to become a Task in the final Contiguity Tree
         * For example, assume group = ABCD && demo = ABCD
         * Initially, upToDateDemoTasks = 0 1 2 3.
         * 
         * We first dynamically discover group AB. We turn that into a task OrderedGroup#5: <AB>
         * Now, upToDateDemoTasks = 5 5 2 3.
         * 
         * Next, we see that BC is also a contiguous group. 
         * However, since B has already been coupled with A via Task#5, we cannot "steal" B into a new task. 
         * 
         * Next, we see that CD is also a contiguous group. 
         * Since neither C or D are in still in their own tasks, we turn that into a task OrderedGroup#6 <CD>.
         * Now, upToDateDemoTasks = 5 5 6 6.
         * 
         * We find, but ignore, groups ABC and BCD since C is already coupled with D and B is coupled with A.
         * 
         * Finally, we find group ABCD. Since forming task OrderedGroup#7 <<AB><CD>> does not involve breaking apart Task#5 or #6, we proceed.
         * Now, upToDateDemoTasks = 7 7 7 7.
         * 
         * This ContiguityTree legitimately represents all of the contiguous groups. 
         * However, when we create Task #7, the OrderedGroup is smart enough to know it should remove unnecessary children (other groups created by this OrderIncorporator which have the same direction).
         * Thus, Task#7 = <ABCD>. Task#5 and Task #6 have been absorbed by #7, as the information they represented is encoded in Task#7.
         * 
         * The final Contiguity Tree is an Ordered Group <ABCD>.
         */
	    public VerifiedGroupOfTasks validate (int start, int end) {
	        LinkedList<Task> tasksInDemoOrder = new LinkedList<Task>();
            LinkedList<Integer> groupIndexOfTasksInDemoOrder = new LinkedList<Integer>();
            LinkedList<Integer> directionOfTasksInDemoOrder = new LinkedList<Integer>();
           
            /*
             * As described above, when building a new Task, we must ensure that we are not breaking up any existing tasks
             * While we do this, we can go ahead and find out if the task is Ordered(sequential or reversible) or Unordered
             * We accomplish this by tracking the tasks we would like to combine into a new task
             *   We track the index of these tasks with respect to the group we are incorporating
             *   For example, if the tasks (when traversed w respect to the demo) have decreasing indices with respect to group,
             *       then we know the tasks are in the opposite order 
             */
           
            //check first edge to ensure we are not breaking apart a pre-existing task
            if (start>0) {
                if (upToDateDemoTasks[start].strictEquals(upToDateDemoTasks[start-1])) return null;
            }
            
            //check last edge to ensure we are not breaking apart a pre-existing task
            if (end < upToDateDemoTasks.length-1) {
                if (upToDateDemoTasks[end].strictEquals(upToDateDemoTasks[end+1])) return null;
            }
            
            //record each subtask, their direction, and their index with respect to the group
            Task currentTask = null;
            for (int k=start; k <= end; k++){
                if ( ! upToDateDemoTasks[k].strictEquals(currentTask)) {
                    currentTask = upToDateDemoTasks[k];
                    tasksInDemoOrder.add(upToDateDemoTasks[k]);
                    groupIndexOfTasksInDemoOrder.add(demoIndexedInGroup[k]);
                    directionOfTasksInDemoOrder.add(directionOfDemoTasks[k]);
                }
            }
            
            //determine the GroupType based on L1IndexOfTasksInL2Order's numeric ordering
            int direction = determineDirection(groupIndexOfTasksInDemoOrder);
            
            return new VerifiedGroupOfTasks(tasksInDemoOrder, direction, directionOfTasksInDemoOrder);
	    }
	    
	    //return 1 if the tasks are in the same order
        //return -1 if in opposite order (reversed)
        //return 0 if scrambled
        private int determineDirection(List<Integer> order){
           int initialDirection = order.get(1)-order.get(0);
           int lastIndex=order.get(0)-initialDirection; //cheat so that the comparing order.get(0) to the "lastIndex" always passes :)
           for (int index : order){
               int tempDirection = index-lastIndex;
               if (initialDirection*tempDirection<0) return 0;
               lastIndex=index;
           }
           return (int) Math.signum(initialDirection);
        }
	    
        
        //When a the builder successfully makes a task, we must update or records
	    public void update (int start, int end, Task task, int direction) {
            for (int i=start; i<=end; i++){
                upToDateDemoTasks[i] = task;
                directionOfDemoTasks[i]=direction;
            }
	    }
	    
	    
	    
	    
	    //THESE METHODS DEAL WITH REPLACING THE DEMO WITH THE NEW TASK OR PIECES ONCE AN ENTIRE INCORPORATION IS FINISHED
	    
	    //The Validator is aware of what Tasks are a piece of the OrderedGroup we are incorporating.
	    //If all pieces were combined into a single Task, relabel the Task with the same label as the OG
	    //If there are multiple pieces left, relabel them all as pieces of OG
	    //Of course, remove the old Tasks from the demo, and insert the new ones in their place
	    public void updateDemo (List<Task> demo, int labelId) throws IncorporationError {
	        List<Task> tasksFromOG = new LinkedList<Task>();
	        Integer startOfPiece = null;
            Integer endOfPiece = null;
            
            int length = demoIndexedInGroup.length;
            
            //go from end to start, so that replacing part of the demo with a single task does not affect the rest of the loop!
            for (int i=length-1; i>=0; i--){
                if (demoIndexedInGroup[i] >= 0) { //this Task was a piece of the OG...
                    if (i!=length-1 && upToDateDemoTasks[i].strictEquals(upToDateDemoTasks[i+1])) { 
                        //we are part of the same task as the last index we checked
                        startOfPiece=i;
                        if (i==0) {replaceTasksInDemo(demo, startOfPiece, endOfPiece);}
                    }
                    
                    else { 
                        //we are a newly found task 
                        tasksFromOG.add(upToDateDemoTasks[i]);
                        replaceTasksInDemo(demo, startOfPiece, endOfPiece); //commit the last piece found (nothing done if indices are null)
                        endOfPiece=i;
                        startOfPiece=i; 
                    }
                }
                else {
                    replaceTasksInDemo(demo, startOfPiece, endOfPiece); //commit the last piece found (nothing done if indices are null)
                    startOfPiece=null;
                    endOfPiece=null;
                }
            }
            
            //now, we have removed all the task that are a part of the OG from the demo
            //we have replaced them with their new tasks, which are not correctly labeled, so let get to it...       
            int numberOfPieces = tasksFromOG.size();
            if (numberOfPieces==1) {
                for (Task piece : tasksFromOG) {
                    piece.setLabel(new Label(labelId));
                }
            }
            else if (numberOfPieces>1 ) {
                int index=0;
                for (Task piece : tasksFromOG) {
                    piece.setLabel(new PieceLabel(labelId, numberOfPieces,index++));
                }
            }  //numberOfPieces is the size of the brotherhood
            else {throw new IncorporationError ("No parts of the Ordered Group were found while incorporating!");}
            
            
	    }
	    
	    private void replaceTasksInDemo (List<Task> demo, Integer start, Integer end) {
	        if ( start==null || end ==null) return;
	        
	        int demoSize = demo.size();
	        Task replacementTask = upToDateDemoTasks[start];
	        
	        List<Task> startOfDemo = demo.subList(0, start);
	        List<Task> endOfDemo = demo.subList(end+1, demoSize);
	        List<Task> newDemo = new ArrayList<Task>(demoSize-end+start-1); 
	        newDemo.addAll(startOfDemo);
	        newDemo.add(replacementTask);
	        newDemo.addAll(endOfDemo);
	        
	        demo.clear();
	        demo.addAll(newDemo);
	        
	    }
	    
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
        
        public GroupBuilder (int demoLength) {}
        
        abstract public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices) throws IncorporationError;
        
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
		
		public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices) throws IncorporationError {
		    Task task;
		    List<Task> tasks = verifiedGroup.tasks;
		    int direction = verifiedGroup.direction;
            List<Integer> subTaskDirections = verifiedGroup.subTaskDirections;
            
		    //if there are any pieces we have to make an UnOrderedGroup no matter what
		    if (numberOfPeices>0) {
		        task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
		    }
		    
		    else {
    		    if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
    		        task = new OrderedGroup(new Label(), null, tasks, false, subTaskDirections, direction); //false bc task order is not reversible
    		    }
    		    else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
    		        task = new OrderedGroup(new Label(), null, tasks, true, subTaskDirections, direction); //true bc task order is reversible
    		    }
    		    else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
    		        task = new UnOrderedGroup(new Label(), null, tasks);
    		    }
    		    else {throw new IncorporationError("Unsupported direction passed into buildNode()");}
		    }
		    
		    return task;
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
	    
	    public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices) throws IncorporationError {
            Task task;
            List<Task> tasks = verifiedGroup.tasks;
            int direction = verifiedGroup.direction;
            List<Integer> subTaskDirections = verifiedGroup.subTaskDirections;
            
            //if there are any pieces we have to make an UnOrderedGroup no matter what
            if (numberOfPeices>0) {
                task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
            }
            else {
                if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
                    task = new OrderedGroup(new Label(), null, tasks, true, subTaskDirections, direction); //true bc task order is reversible
                }
                else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
                    task = new OrderedGroup(new Label(), null, verifiedGroup.tasks, true, subTaskDirections, direction); //true bc task order is reversible
                }
                else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
                    task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
                }
                else {throw new IncorporationError("Unsupported direction passed into buildNode()");}
            }
            
            return task;
        }
	    
	}
	
	
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
    //**************************************** HELPER CLASS ******************************************//
    //************************************************************************************************//
    
    /*
     * An unfortunate result of separating a unified process into many classes
     * This class is simply used to pass info between the verifier and the builder
     */
	
	private class VerifiedGroupOfTasks {
	    private List<Task> tasks;
	    private List<Integer> subTaskDirections;
	    private int direction;
	    
	    public VerifiedGroupOfTasks ( List<Task> tasksToBeGrouped, int dir, List<Integer> subTaskDirs) {
	        tasks = tasksToBeGrouped;
	        direction = dir;
	        subTaskDirections = subTaskDirs;
	    }
	}	
	
	
	
}
