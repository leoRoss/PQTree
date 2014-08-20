package contiguityTree;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

/*
 * RULES FOR THE PROGRAMMER:
 *  - Never change the labels of Tasks in the old Contiguity Tree
 *      - Only Tasks in the demo which have PieceLabels ever need to be relabeled (right BEFORE they are resolved)
 *          -A new group can never contain piecesLabels for hashing reasons
 *  - Two Tasks are equal as long as they have equal Labels
 *      - Labels are equal as long as they have the same id (and the same brotherUUID if PieceLabel)
 */


/* 
 * I have separated out the steps of the dynamically incorporated process to make it more understandable to a human
 *
 * Reading the comments top down should give you a thorough understanding of the process of incorporating a OrderedGroup into the demo
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
     * Each book also keeps track of the numberOfFullTasks, the desiredNumberOfPieces (we aren't ever sure), and the numberOfBrotherhoods
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
     *          books[i][j].numberOfFullTasks + books[i][j].desiredNumberOfPieces = i
     *          
     *          Note: i = numberOfTasksTracked
     *     
     *     Ex:
     *          assume AAABBBBCCDDDDE is contiguous (has all the pieces in each brotherhood)
     *          range +1 = 4-0+1 = 5
     *          
     *          number of tasks tracked = 14
     *          number of full tasks = 1 ... just E
     *          the desire number of pieces = 3As + 4Bs + 2Cs + 4Ds = 13
     *          number of brotherhoods = 4 ... A, B, C, and D brotherhoods
     *          
     *          indeed 5+13-4 == 14
     *          indeed 1+13 == 14
     *          
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
    		//When merging two PieceIndexTrackers, I need to add the highest index task of one to the pieces of the other
    		protected Task highestIndexOriginalDemoTask;
    		protected int desiredNumberOfPieces;
    		protected int numberOfBrotherhoods;
    		protected int numberOfFullTasks; //as opposed to Pieces
    		protected int numberOfDemoTasksTracked; //= i+1 in books[i][j] ie: size of demo(j..j+i)
    		
    		public PieceIndexTracker (List<Task> subTasks, Task task, int demoSize) {
    			myPieces = new HashSet<Task> ();
                desiredNumberOfPieces=0;
                numberOfBrotherhoods=0;
                numberOfFullTasks=0;
                numberOfDemoTasksTracked = 1;
                
                
                int indexInGroup = lenientIndexOfSubTask(subTasks, task);
                
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
    			else {
    			    numberOfFullTasks++;
    			}
    		}

            public PieceIndexTracker (PieceIndexTracker tracker1, PieceIndexTracker tracker2) {
                minIndex = Math.min(tracker1.minIndex, tracker2.minIndex);
    			maxIndex = Math.max(tracker1.maxIndex, tracker2.maxIndex);
    			numberOfFullTasks = tracker1.numberOfFullTasks;
    			numberOfDemoTasksTracked = tracker1.numberOfDemoTasksTracked+1;
    			numberOfBrotherhoods = tracker1.numberOfBrotherhoods;
    			copyPieceTrackingFrom(tracker1);
    			addHighestIndexOriginalDemoTaskFrom(tracker2);
    		}
    		
    		//NOTE: We are making stealing and editing the Sets from the layer below us. This is OK as we no longer need them. 
    		//However, we must take note that tracker1 is no longer valid for dynamic computing
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
    			else {
    			    numberOfFullTasks++;
    			}
    		}
    		
    		public boolean candidateForGrouping () {
    			int rangeOfIndices = maxIndex - minIndex;
    			if (numberOfFullTasks+desiredNumberOfPieces != numberOfDemoTasksTracked) return false;
    			int supplementalPieces = desiredNumberOfPieces - numberOfBrotherhoods; //AAABC has 2 supplemental As
    			if (rangeOfIndices + 1 + supplementalPieces != numberOfDemoTasksTracked) return false;
    			return true;
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
    		        task = new OrderedGroup(new Label(), null, tasks, false, directionOfTasks, startIndex, direction); //false bc task order is not reversible
    		    }
    		    else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
    		        task = new OrderedGroup(new Label(), null, verifiedGroup.tasks, true, directionOfTasks, startIndex, direction); //true bc task order is reversible
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
            
            if (tasks.size()!=verifiedGroup.end-verifiedGroup.start+1) throw new Error ("We fucked up!");
            int endIndex = verifiedGroup.end;
            
            //if there are any pieces we have to make an UnOrderedGroup no matter what
            if (numberOfPeices>0) {
                task = new UnOrderedGroup(new Label(), null, verifiedGroup.tasks);
            }
            else {
                if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
                    task = new OrderedGroup(new Label(), null, tasks, true, directionOfTasks, startIndex, direction); //true bc task order is reversible
                }
                else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
                    task = new OrderedGroup(new Label(), null, verifiedGroup.tasks, true, directionOfTasks, startIndex, direction); //true bc task order is reversible
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
