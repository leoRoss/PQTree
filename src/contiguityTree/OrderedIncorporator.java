package contiguityTree;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

/*
 * RULES FOR THE PROGRAMMER:
 *  - Never change the labels of Tasks in the old Contiguity Tree
 *      - Only Tasks in the demo which have PieceLabels ever need to be relabeled (after they are resolved)
 *      - This is important for the HashSets used to dynamically track pieces
 *  - Two Tasks are equal as long as they have equal Labels
 *      - Labels are equal as long as they have the same id (and the same brotherUUID if PieceLabel)
 */


//I have separated out the steps of the dynamically incorporated process to make it more understandable to a human



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


public class OrderedIncorporator extends Incorporator{
    ContiguousGroupFinder finder;
    Validator validator;
    GroupBuilder builder;
    
    public OrderedIncorporator (OrderedGroup orderedGroupToIncorporateIntoDemo, List<Task> partiallyIncorporatedDemo) {
        demo = partiallyIncorporatedDemo;
        group = orderedGroupToIncorporateIntoDemo;
        int demoSize = demo.size();
        if (demoSize<group.getSize()) throw new Error("The demo can not have less tasks than the group does");
        
        finder = new ContiguousGroupFinder( (List<Task>)group.getSubTasks(), demo);
        int [] indexOfDemoTasksInGroup = finder.indexOfDemoTasksInGroup();
        validator = new Validator(demo, indexOfDemoTasksInGroup);
        if (group.isReversible()) { builder = new GroupBuilderFromReversible(demoSize); }
        else { builder = new GroupBuilderFromNonReversible(demoSize); }
    }

    public void incorporate () {
        int [] contiguousCandidate = finder.nextGroup(); 
        int numberOfGroupsMade = 0;
        int numberOfPrimitivesInGroupsMade = 0;
        while (contiguousCandidate!=null) {
            System.out.println("Candidate: ["+contiguousCandidate[0] +" - "+ contiguousCandidate[1] + "](" + contiguousCandidate[2] +")");
            
            
            VerifiedGroupOfTasks verifiedGroup = validator.validate(contiguousCandidate[0], contiguousCandidate[1]);
            if (verifiedGroup != null) {
                System.out.println("    Verified");
                Task task = builder.buildTask(verifiedGroup, contiguousCandidate[2]);
                numberOfGroupsMade++;
                numberOfPrimitivesInGroupsMade+= task.absoluteSize();
                System.out.println("    Made Task:");
                task.printMe(2);
                System.out.println();
                System.out.println();
                validator.update(contiguousCandidate[0], contiguousCandidate[1], task);
            }
            contiguousCandidate = finder.nextGroup(); 
        }
        
        System.out.println("Made " + numberOfGroupsMade + " new tasks during incorporation" + " for a total Primitive count of " + numberOfPrimitivesInGroupsMade);
        validator.updateDemo(demo, group.getLabel().getId() );
        
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
	    int i,j;
	    
	    public ContiguousGroupFinder (List<Task> orderedGroupSubTasks, List<Task> demo) {
	        int demoSize = demo.size();
	        i=1; j=-1;
	        books = new PieceIndexTracker [demoSize][demoSize];
	        int index=0;
            for (Task demoTask : demo){
                books[0][index++] = new PieceIndexTracker(orderedGroupSubTasks, demoTask, demoSize);
                System.out.print("| " + books[0][index-1].minIndex + "," + books[0][index-1].maxIndex);
            }
            System.out.println();
	    }
	    
	    //Keep dynamically building books from where we left off until we find a new candidate
	    public int[] nextGroup () {
	        int size = books[0].length;
	        
	        //I must apologize for the ugly nested looping. Returning inside the nested loop must not affect the incrementing of i and j
	        //This allows us to return to where we left of :)
	        while (i<=size){
	            if (j+i==size-1) {i++; j=-1;}
	            while (j<size-i-1){
	                j++;
	                //System.out.println("Making books for["+i+"]["+j+"]");
	                books[i][j]=new PieceIndexTracker(books[i-1][j],books[i-1][j+1]);
	                //System.out.print("| " + books[i][j].minIndex + "," + books[i][j].maxIndex);
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
	    
	    
    	//Imagine we are incorporating OrderedGroup oGroup into the List<Task> demo.
    	//oGroup also contains a List<Task>.
    	//Let PieceIndexTracker tracker be used to track the Tasks in the demo from index 4 to 7.
    	//minIndex is the smallest index of a Task in oGroup which also exists in demo[4,7].
    	//maxIndex is the largest index of a Task in oGroup which also exists in demo[4,7].
    	//We define exists to include tasks in the demo which are Pieces of a Task in oGroup.
    	//The pieces Map is used to keep track of these Pieces. We can only form a new Group if we have all the Pieces of a Task.
    	private class PieceIndexTracker {
    		int minIndex, maxIndex;
    		HashSet<Task> tasksOfMyPieces;
    		HashSet<Task> myPieces;
    		//When merging two PieceIndexTrackers, I just need to add the highest index task of one to the pieces of the other
    		Task taskOfMyHighestIndexOriginalTask; 
    		Task highestIndexOriginalTask;
    		int desiredNumberOfPieces;
    		int numberOfDemoTasksTracked; //my height in books[][]
    		
    		public PieceIndexTracker (List<Task> subTasks, Task task, int demoSize) {
    			int indexInGroup = lenientIndexOfSubTask(subTasks, task);
    			
    			if (indexInGroup==-1) { //the task does not exist in this group
    			    minIndex = -1;
    			    maxIndex = demoSize+1; //this way no group containing this task will ever be a candidate
    			    
    			    highestIndexOriginalTask = null; 
                    taskOfMyHighestIndexOriginalTask = null;
    			}
    			else {
        			minIndex = indexInGroup;
        			maxIndex = indexInGroup;
        			
        			//A vague copy create a task with an equivalent label
                    //Why? b/c this task may become relabeled when it becomes part of a group (the new Contiguity Tree should not contain any Tasks with PieceLabels)
                    //Since we are tracking pieces using a label-based hash, we need persistent labels
                    highestIndexOriginalTask = task.vagueCopy(); 
                    taskOfMyHighestIndexOriginalTask = subTasks.get(indexInGroup); 
    			}
    			
    			
    			
    			myPieces = new HashSet<Task> ();
    			tasksOfMyPieces = new HashSet<Task> ();
    			
    			if (highestIndexOriginalTask!=null && highestIndexOriginalTask.isPiece()) {
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
    			maxIndex = Math.max(tracker1.maxIndex, tracker2.maxIndex);
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
    			
    			if (highestIndexOriginalTask!= null && highestIndexOriginalTask.isPiece()) {
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
    			if (rangeOfIndices + desiredNumberOfPieces + 1 == numberOfDemoTasksTracked) return true;
    			return false;
    		}
    		
    		public boolean allPiecesHaveAllTheirBrothers () {
    			return desiredNumberOfPieces == myPieces.size();
    		}
    		
    		public int numberOfPeices() {
                return desiredNumberOfPieces;
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
    //************************************************************************************************//
	
	/*
     * This class is used to ensure that the contiguous groups merit to be built into the Contiguity Tree
     */
	
	private class Validator {
	    private Task[] upToDateDemoTasks;
	    private int [] demoIndexedInGroup;
	    
	    public Validator (List<Task> demo, int [] indexedInGroup) {
	        int demoSize = demo.size();
	        upToDateDemoTasks = new Task[demoSize];
	        demoIndexedInGroup = new int [demoSize];
	        int index=0;
            for (Task demoTask : demo){
                upToDateDemoTasks[index] = demoTask;
                demoIndexedInGroup[index] = indexedInGroup[index];
                index++;
            }
	    }


        /*
	     * Not all contiguous groups merit to become a Task in the final Contiguity Tree
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
	    public VerifiedGroupOfTasks validate (int start, int end) {
	        //System.out.println("Validating group from: "+ start + " to " + end);
	        ArrayList<Task> tasksInL2Order = new ArrayList<Task>();
            ArrayList<Integer> L1IndexOfTasksInL2Order = new ArrayList<Integer>();
           
            /*
             * As described above, when building a Task, we must ensure that we are not breaking up any existing tasks
             * While we do this, we can go ahead and find out if the task is Ordered(reversible?) or Unordered
             * We accomplish this by tracking the tasks we would like to combine into a new task
             *   and by tracking the index of these tasks with respect to list1
             *   For example, if the tasks (when traversed w respect to list2) have decreasing indices with respect to list1,
             *       then we know the tasks are in the opposite order 
             */
           
            //check first edge to ensure we are not breaking apart a pre-existing task
            if (start>0) {
                if (upToDateDemoTasks[start].equals(upToDateDemoTasks[start-1])) return null;
            }
            
          //check last edge to ensure we are not breaking apart a pre-existing task
            if (end < upToDateDemoTasks.length-1) {
                if (upToDateDemoTasks[end].equals(upToDateDemoTasks[end+1])) return null;
            }
            
            //record each subtask and their index with respect to list 1
            Task currentTask = null;
            for (int k=start; k <= end; k++){
                if ( ! upToDateDemoTasks[k].equals(currentTask)) {
                    currentTask = upToDateDemoTasks[k];
                    tasksInL2Order.add(upToDateDemoTasks[k]);
                    L1IndexOfTasksInL2Order.add(demoIndexedInGroup[k]);
                }
            }
            
            //determine the GroupType based on L1IndexOfTasksInL2Order's numeric ordering
            int direction = determineDirection(L1IndexOfTasksInL2Order);
            
            return new VerifiedGroupOfTasks(tasksInL2Order, direction, start, end);
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
	    
	    public void update (int start, int end, Task task) {
            for (int i=start; i<=end; i++){
                upToDateDemoTasks[i] = task;
            }
	    }
	    
	    
	    //The Validator is aware of what Tasks are a piece of the OrderedGroup we are incorporating.
	    //If all pieces were combined into a single Task, relabel the Task with the same label as the OG
	    //If there are multiple pieces left, relabel them all as peices of OG
	    //Of course, remove the old Tasks from the demo, and insert the new ones in their place
	    public void updateDemo (List<Task> demo, int labelId) {
	        int numberOfResolvedPieces = numberOfResolvedPieces();
	        System.out.print("Updating demo with number of pieces = " + numberOfResolvedPieces + " from ");
	        if (numberOfResolvedPieces==1) updateDemoWithSingleTaskAsOG(demo, labelId);
	        else if (numberOfResolvedPieces>1) updateDemoWithMulipleTasksAsOGPieces(demo, labelId);
	        else throw new Error ("Not a single piece was found for this OG! Tisk tisk tisk. Trying to pull a fast one on me?");
	    }
	    
	    private void updateDemoWithSingleTaskAsOG (List<Task> demo, int labelId) {
	        Integer startOfPiece = null;
	        Integer endOfPiece = null;
	        Task singlePiece = null;
	        
	        for (int i=demoIndexedInGroup.length-1; i>=0; i--){
                if (demoIndexedInGroup[i] >= 0) { //this Task was a piece of the OG
                    if (endOfPiece == null) {
                        endOfPiece = i;
                        singlePiece = upToDateDemoTasks[i];
                    }
                    startOfPiece = i;
                }
            }
	        
	        if (startOfPiece == null || endOfPiece == null || singlePiece == null) throw new Error ("Unless demoIndexedInGroup changed out from under us, this is impossible");
	        System.out.println(startOfPiece+ " to " + endOfPiece);
	        //now relabel singlePiece and replace demo[startOfPiece,endOfPiece] with singlePiece
	        singlePiece.setLabel(new Label(labelId));
	        replaceTaskInDemo(demo, startOfPiece, endOfPiece, singlePiece);
	        
	        singlePiece.printMe(3);
	    }
	    
	    private void updateDemoWithMulipleTasksAsOGPieces (List<Task> demo, int labelId) {
	        Integer startOfPiece = null;
            Integer endOfPiece = null;
            Task singlePiece = null;
            
            for (int i=demoIndexedInGroup.length-1; i>=0; i--){
                if (demoIndexedInGroup[i] >= 0) { //this Task was a piece of the OG
                    startOfPiece = i;
                    if (endOfPiece == null) {
                        endOfPiece = i;
                        singlePiece = upToDateDemoTasks[i];
                    }
                    else if (singlePiece.equals(upToDateDemoTasks[i])) {
                        
                    }
                    
                }
            }
            
	    }
	    
	    private void replaceTaskInDemo (List<Task> demo, int start, int end, Task task) {
	        int demoSize = demo.size();
	        List<Task> startOfDemo = demo.subList(0, start);
	        List<Task> endOfDemo = demo.subList(end+1, demoSize);
	        List<Task> newDemo = new ArrayList<Task>(demoSize-end+start-1); 
	        newDemo.addAll(startOfDemo);
	        newDemo.add(task);
	        newDemo.addAll(endOfDemo);
	        
	        demo.clear();
	        demo.addAll(newDemo);
	    }
	    
	    //Into how many tasks have the pieces of the OG been reduced to?
	    //demoIndexedInGroup helps us know where these pieces are
	    //upToDateDemoTasks helps us know if they pieces were reduced together or stayed separate
        private int numberOfResolvedPieces () {
            int numberOfResolvedPieces = 0;
            boolean lastGuyWasAPiece = false;
            
            for (int i=demoIndexedInGroup.length-1; i>=0; i--){
                if (demoIndexedInGroup[i] >= 0) { //this Task was a piece of the OG
                    if (lastGuyWasAPiece) {
                        //we may be a new piece, or we may have been resolved into the same Task as the lastGuy. Lets check...
                        if ( ! upToDateDemoTasks[i].equals(upToDateDemoTasks[i+1])) {
                            numberOfResolvedPieces++; //we are our own Task/piece!
                        }
                    }
                    else {
                        numberOfResolvedPieces++; //we just found a new piece!
                    }
                    lastGuyWasAPiece = true;
                }
                else {
                    lastGuyWasAPiece = false;
                }
            }
            
            return numberOfResolvedPieces;
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
        protected int[] directionOfTasks;
        public GroupBuilder (int demoLength) {
            directionOfTasks = new int[demoLength]; //default values of 0 so original Tasks will never be absorbed
        }
        
        abstract public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices);
        
        protected void setDirection (int start, int end, int direction) {
            for (int i=start; i<=end; i++){
                directionOfTasks[i]=direction;
            }
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
		
		public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices) {
		    Task task;
		    List<Task> tasks = verifiedGroup.tasks;
		    int direction = verifiedGroup.direction;
		    int startIndex = verifiedGroup.start;
            int endIndex = verifiedGroup.end;
            
		    //if there are any pieces we have to make an UnOrderedGroup no matter what
		    if (numberOfPeices>0) {
		        task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
		    }
		    else {
    		    if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
    		        task = new OrderedGroup(new Label(), null, tasks, false, directionOfTasks, startIndex, endIndex, direction); //false bc task order is not reversible
    		    }
    		    else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
    		        task = new OrderedGroup(new Label(), null, verifiedGroup.tasks, true, directionOfTasks, startIndex, endIndex, direction); //true bc task order is reversible
    		    }
    		    else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
    		        task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
    		    }
    		    else {throw new Error("Unsupported direction passed into buildNode()");}
		    }
		    
		    setDirection(startIndex, endIndex, direction);
		    
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
	    
	    public Task buildTask (VerifiedGroupOfTasks verifiedGroup, int numberOfPeices) {
            Task task;
            List<Task> tasks = verifiedGroup.tasks;
            int direction = verifiedGroup.direction;
            int startIndex = verifiedGroup.start;
            int endIndex = verifiedGroup.end;
            
            //if there are any pieces we have to make an UnOrderedGroup no matter what
            if (numberOfPeices>0) {
                task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
            }
            else {
                if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
                    task = new OrderedGroup(new Label(), null, tasks, true, directionOfTasks, startIndex, endIndex, direction); //true bc task order is reversible
                }
                else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
                    task = new OrderedGroup(new Label(), null, verifiedGroup.tasks, true, directionOfTasks, startIndex, endIndex, direction); //true bc task order is reversible
                }
                else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
                    task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
                }
                else {throw new Error("Unsupported direction passed into buildNode()");}
            }
            
            setDirection(startIndex, endIndex, direction);
            
            return task;
        }
	    
	}
	
	
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
    //************************************** UPDATER CLASSES *****************************************//
    //************************************************************************************************//
    
    /*
     * These classes are used to update the Demo with the new Tasks
     */
	
	
	
	
	
	
	
	
	
	//************************************************************************************************//
    //**************************************** HELPER CLASS ******************************************//
    //************************************************************************************************//
    
    /*
     * An unfortunate result of separating a unified process into many classes
     * This class is simply used to pass info between the verifier and the builder
     */
	
	private class VerifiedGroupOfTasks {
	    List<Task> tasks;
	    int start, end, direction;
	    
	    public VerifiedGroupOfTasks ( List<Task> tasksToBeGrouped, int dir, int s, int e) {
	        tasks = tasksToBeGrouped;
	        direction = dir;
	        start = s; end = e;
	    }
	}	
	
	
	
}
