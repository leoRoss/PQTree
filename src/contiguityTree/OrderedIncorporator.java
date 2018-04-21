package contiguityTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

/**
 * RULES FOR THE PROGRAMMER:
 *  - Never change the labels of Tasks in the old Contiguity Tree
 *  - A new node can never contain a Node with a PiecesLabel for hashing reasons
 *      - These nodes must be relabeled before they are added to a new node
 *  - Two Tasks are equal as long as they have equal Labels
 *      - Labels are equal as long as they have the same id
 *      - This is needed so that node.contains(Node T with a PieceLabel) will return true if T as if T has a normal Label
 *
 * I have separated out the steps of the incorporation process to make it more understandable to a human
 * Reading the comments top down should give you a thorough understanding of the process of incorporating a OrderedNode into the demo
 */

public class OrderedIncorporator extends Incorporator{

    private ContiguousGroupFinder finder; // find contiguous groups - candidates to become nodes in the new tree
    private Validator validator; // validate these groups using the index
    private GroupBuilder builder; // build nodes in the new tree

    OrderedIncorporator (OrderedNode orderedGroupToIncorporateIntoDemo, List<Node> partiallyIncorporatedDemo) throws IncorporationError {
        permutation = partiallyIncorporatedDemo;
        node = orderedGroupToIncorporateIntoDemo;
        int permutationSize = permutation.size();
        if (permutationSize< node.getSize()) throw new IncorporationError("The demo can not have less nodes than the node does");

        finder = new ContiguousGroupFinder( (OrderedNode) node, permutation);
        int [] indexOfDemoTasksInGroup = finder.indexOfDemoTasksInGroup();
        validator = new Validator(permutation, indexOfDemoTasksInGroup);
        if (node.isReversible()) { builder = new GroupBuilderFromReversible(permutationSize); }
        else { builder = new GroupBuilderFromNonReversible(permutationSize); }
    }

    public void incorporate () throws IncorporationError {
        int [] contiguousCandidate = finder.nextGroup();   // [start index in demo, end index in demo]

        while (contiguousCandidate!=null) { //while the finder continues to find contiguous groups
            VerifiedGroupOfTasks verifiedGroup = validator.validate(contiguousCandidate[0], contiguousCandidate[1]);
            if (verifiedGroup != null) { //if the indices were validated
                Node node = builder.buildTask(verifiedGroup); //build the node from the indices in the demo
                // System.out.println("    Made Node:"); node.printMe(2); System.out.println(); System.out.println();
                validator.update(contiguousCandidate[0], contiguousCandidate[1], node, verifiedGroup.direction); //update our books for the validator with the newly made node
            }
            contiguousCandidate = finder.nextGroup();
        }

        validator.updateDemo(permutation, node.getLabel().getId() ); //change the demo to reflect all the changes we have decided to make
    }


    //************************************************************************************************//
    //*************************************** FINDER CLASSES *****************************************//
    //************************************************************************************************//

    /**
     * These classes are used to find all the groups of nodes which are contiguous
     * in both the OrderedNode's subNodes and in the demo
     * HOW WE COMPUTE CONTIGUOUS GROUPS...
     *
     * We use an 2D Array of PieceIndexTrackers, books, to keep keep information about a subgroup of the demo
     * books[i][j] keeps information about the subgroup demo(j..i+j)
     *
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     *
     * Information: Max and Min
     *
     * Step1:
     *      For all j, we initialize books[0][..].max and books[0][..].min to = node.indexOf( demo.get(j) )
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
     *          node = A,B,C,D,E,F
     *          demo  = E,F,A,C,D,B
     *
     *          max[0] = [4, 5, 0, 2, 3, 1]  min[0] = [4, 5, 0, 2, 3, 1]             [E, F, A, C, D, B]
     *          max[1] = [5, 5, 2, 3, 3]     min[1] = [4, 0, 0, 2, 1]    max-min-i = [0, 4, 1, 0, 1]    // the two 0 signify that groups EF and CD are both contiguous in the two lists
     *          max[2] = [5, 5, 3, 3]        min[2] = [0, 0, 0, 1]       max-min-i = [3, 3, 1, 0]       // 0 signifies that CDB are contiguous in the two lists
     *          max[3] = [5, 5, 3]           min[3] = [0, 0, 0]          max-min-i = [2, 2, 0]          // 0 signifies that ACDB are contiguous in the two lists
     *          max[4] = [5, 5]              min[4] = [0, 0]             max-min-i = [1, 1]             // neither EFACD or FACDB are contiguous in the two lists
     *          max[5] = [5]                 min[5] = [0]                max-min-i = [0]                // of course, EFACDB is a contiguous node in the two lists
     *
     * Why? This makes sense because we want the range of a node to be equal to its size.
     * A node 7, 9, 6, 8 has a range of 4 and a size of 4. However, 7, 10, 6, 8 has a range of 5 and a size of 4.
     *
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     *
     * Information: Pieces
     *
     * The above logic and example does not involve pieces. It is therefore, incorrect, but its a good place to start...
     *
     * A more realistic situation would look more like this:
     *      node = A,B,C,D,E,F
     *      demo  = E,F',A,F',C',D,B,C'
     *
     * As you can see, F' and C' exist in multiple places in the demo. The ' indicates that they are pieces of F and C.
     * This implies that when C and F attempted to incorporate themselves into the demo,
     * they were unable to find an entire contiguous node with all their subtasks.
     * Instead, F (and C) found all the chunks of its subtasks in the demo, and did its best to resolve each chunk.
     *
     * What does this mean for us?
     * A node is not truly contiguous if it has one piece without having all the matching pieces (its brothers).
     *
     * In the example above, we cannot match C',D together, even though our range requirement above is satisfied.
     * Why?
     * Imagine C = a non-reversible ordered node [3,4,5]
     *      and D = [6,7]
     * By nature of Contiguity Trees, we know that C and D have NOT always appeared sequentially
     *      Why? Because, otherwise, we would only have a single non-reversible ordered node [3,4,5,6,7]
     *
     * Without loss of Gen, lets pretend in past demos have seen CD and DC.... [3,4,5],[6,7] and [6,7],[3,4,5]
     * Now, in the demo, we have .... C',D,B,C' = {4,3}, [6,7], B, 5   where the first C`={4,3} and the last C' = 5
     * Obviously, saying that C',D (4,3,6,7) is a contiguous node is incorrect!
     *
     * Thus, we must wait until we have all the pieces of C before forming a new node.
     * Moreover, this node C',D,B,C' must be an UnorderedGroup.
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
     *          number of nodes tracked = 14
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

        ContiguousGroupFinder (OrderedNode group, List<Node> demo) {
            int demoSize = demo.size();
            i=1; j=-1;
            books = new PieceIndexTracker [demoSize][demoSize];
            int index=0;
            for (Node demoNode : demo){
                books[0][index++] = new PieceIndexTracker(group, demoNode, demoSize);
            }
        }

        //This function dynamically builds books until it finds a candidate, which it returns
        //When it is called again, it continues building from where it left off
        int[] nextGroup () {
            int size = books[0].length;

            //I must apologize for the ugly nested looping. Returning inside the nested loop must not affect the incrementing of i and j
            //This allows us to return to where we left of :)
            while (i<=size){
                if (j+i==size-1) {i++; j=-1;}
                while (j<size-i-1){
                    j++;
                    books[i][j]=new PieceIndexTracker(books[i-1][j],books[i-1][j+1]);
                    if (books[i][j].candidateForGrouping()) return new int[]{j,j+i};
                }
            }
            return null;
        }

        int [] indexOfDemoTasksInGroup () {
            int size = books[0].length;
            int [] indexOfDemoTasksInGroup = new int [size];
            for (int i=0; i<size; i++) {
                indexOfDemoTasksInGroup[i] = books[0][i].minIndex;
            }
            return indexOfDemoTasksInGroup;
        }



        private class PieceIndexTracker {
            int minIndex, maxIndex;
            HashSet<Node> myPieces;
            Node highestIndexOriginalDemoNode; //the original task in demo[j+i], used facilitate merging two PieceIndexTrackers
            int desiredNumberOfPieces;
            int numberOfBrotherhoods;
            int numberOfDemoTasksTracked; //= i+1 in books[i][j] ie: size of demo(j..j+i)

            PieceIndexTracker (OrderedNode group, Node node, int demoSize) {
                myPieces = new HashSet<> ();
                desiredNumberOfPieces=0;
                numberOfBrotherhoods=0;
                numberOfDemoTasksTracked = 1;

                int indexInGroup;
                if (group.contains(node)) {indexInGroup = group.lenientIndexOfSubTask(node); }
                else {indexInGroup = -1;}

                if (indexInGroup==-1) { //the node does not exist in this node
                    minIndex = -demoSize*100;
                    maxIndex = demoSize*100; //this way no node containing this node will ever be a candidate

                    highestIndexOriginalDemoNode = null;
                }
                else {
                    minIndex = indexInGroup;
                    maxIndex = indexInGroup;

                    //A vague copy creates a placeholder node with an equivalent label
                    //Since we are tracking pieces using a label-based hashset, we need persistent labels
                    //the highestIndexOriginalDemoNode may become relabeled when it becomes part of a node (a new InnerNode should never contain any Tasks with PieceLabels)
                    highestIndexOriginalDemoNode = node.vagueCopy();
                }

                if (highestIndexOriginalDemoNode !=null && highestIndexOriginalDemoNode.isPiece()) {
                    myPieces.add(highestIndexOriginalDemoNode);
                    desiredNumberOfPieces+= highestIndexOriginalDemoNode.getBrotherhoodSize();
                    numberOfBrotherhoods++;
                }
            }

            PieceIndexTracker (PieceIndexTracker tracker1, PieceIndexTracker tracker2) {
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
                highestIndexOriginalDemoNode = tracker2.highestIndexOriginalDemoNode;

                if (highestIndexOriginalDemoNode != null && highestIndexOriginalDemoNode.isPiece()) {
                    //If we are adding a new set of pieces to track, we must update our piece count
                    if (!myPieces.contains(highestIndexOriginalDemoNode)) {
                        myPieces.add(highestIndexOriginalDemoNode);
                        desiredNumberOfPieces+= highestIndexOriginalDemoNode.getBrotherhoodSize();
                        numberOfBrotherhoods++;
                    }
                }
            }

            boolean candidateForGrouping () {
                int rangeOfIndices = maxIndex - minIndex;
                int supplementalPieces = desiredNumberOfPieces - numberOfBrotherhoods; //AAABC has 2 supplemental As
                return rangeOfIndices + 1 + supplementalPieces == numberOfDemoTasksTracked;
            }
        }
    }


    //************************************************************************************************//
    //************************************** VALIDATION CLASS ****************************************//
    //************************************************************************************************//

    /**
     * This class is used to ensure that the contiguous groups merit to be built into the Contiguity Tree
     */
    private class Validator {
        private Node[] upToDateDemoNodes;
        private int[] directionOfDemoTasks;
        private int [] demoIndexedInGroup;

        Validator (List<Node> demo, int [] indexedInGroup) {
            int demoSize = demo.size();
            upToDateDemoNodes = new Node[demoSize];
            demoIndexedInGroup = new int [demoSize];
            directionOfDemoTasks = new int [demoSize];
            int index=0;
            for (Node demoNode : demo){
                upToDateDemoNodes[index] = demoNode;
                demoIndexedInGroup[index] = indexedInGroup[index];
                directionOfDemoTasks[index] = 0; //unnecessary initialization for clarity
                index++;
            }
        }

        /**
         * Not all contiguous groups merit to become a Node in the final Contiguity Tree
         * For example, assume node = ABCD && demo = ABCD
         * Initially, upToDateDemoNodes = 0 1 2 3.
         *
         * We first dynamically discover node AB. We turn that into a task OrderedNode#5: <AB>
         * Now, upToDateDemoNodes = 5 5 2 3.
         *
         * Next, we see that BC is also a contiguous node.
         * However, since B has already been coupled with A via Node#5, we cannot "steal" B into a new task.
         *
         * Next, we see that CD is also a contiguous node.
         * Since neither C or D are in still in their own nodes, we turn that into a task OrderedNode#6 <CD>.
         * Now, upToDateDemoNodes = 5 5 6 6.
         *
         * We find, but ignore, groups ABC and BCD since C is already coupled with D and B is coupled with A.
         *
         * Finally, we find node ABCD. Since forming task OrderedNode#7 <<AB><CD>> does not involve breaking apart Node#5 or #6, we proceed.
         * Now, upToDateDemoNodes = 7 7 7 7.
         *
         * This PQTree legitimately represents all of the contiguous groups.
         * However, when we create Node #7, the OrderedNode is smart enough to know it should remove unnecessary children (other groups created by this OrderIncorporator which have the same direction).
         * Thus, Node#7 = <ABCD>. Node#5 and Node #6 have been absorbed by #7, as the information they represented is encoded in Node#7.
         *
         * The final Contiguity Tree is an Ordered InnerNode <ABCD>.
         */
        VerifiedGroupOfTasks validate (int start, int end) {
            LinkedList<Node> tasksInDemoOrder = new LinkedList<Node>();
            LinkedList<Integer> groupIndexOfTasksInDemoOrder = new LinkedList<Integer>();
            LinkedList<Integer> directionOfTasksInDemoOrder = new LinkedList<Integer>();
            int numberOfUnResolvedPieces = 0;
            /*
             * As described above, when building a new Node, we must ensure that we are not breaking up any existing nodes
             * While we do this, we can go ahead and find out if the task is Ordered(sequential or reversible) or Unordered
             * We accomplish this by tracking the nodes we would like to combine into a new task
             *   We track the index of these nodes with respect to the node we are incorporating
             *   For example, if the nodes (when traversed w respect to the demo) have decreasing indices with respect to node,
             *       then we know the nodes are in the opposite order
             */

            //check first edge to ensure we are not breaking apart a pre-existing task
            if (start>0) {
                if (upToDateDemoNodes[start].strictEquals(upToDateDemoNodes[start-1])) return null;
            }

            //check last edge to ensure we are not breaking apart a pre-existing task
            if (end < upToDateDemoNodes.length-1) {
                if (upToDateDemoNodes[end].strictEquals(upToDateDemoNodes[end+1])) return null;
            }

            //record each subtask, their direction, and their index with respect to the node
            Node currentNode = null;
            for (int k=start; k <= end; k++){
                if (upToDateDemoNodes[k].isPiece()) numberOfUnResolvedPieces++;
                if ( ! upToDateDemoNodes[k].strictEquals(currentNode)) {
                    currentNode = upToDateDemoNodes[k];
                    tasksInDemoOrder.add(upToDateDemoNodes[k]);
                    groupIndexOfTasksInDemoOrder.add(demoIndexedInGroup[k]);
                    directionOfTasksInDemoOrder.add(directionOfDemoTasks[k]);
                }
            }

            //determine the GroupType based on L1IndexOfTasksInL2Order's numeric ordering
            int direction = determineDirection(groupIndexOfTasksInDemoOrder);

            return new VerifiedGroupOfTasks(tasksInDemoOrder, direction, directionOfTasksInDemoOrder, numberOfUnResolvedPieces);
        }

        /**
         * @return  1 if the nodes are in the same order
         *          -1 if in opposite order (reversed)
         *          0 if scrambled
         */
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


        //When a the builder successfully makes a node, we must update or records
        void update (int start, int end, Node node, int direction) {
            for (int i=start; i<=end; i++){
                upToDateDemoNodes[i] = node;
                directionOfDemoTasks[i]=direction;
            }
        }




        //THESE METHODS DEAL WITH REPLACING THE DEMO WITH THE NEW TASK OR PIECES ONCE AN ENTIRE INCORPORATION IS FINISHED

        //The Validator is aware of what Tasks are a piece of the OrderedNode we are incorporating.
        //If all pieces were combined into a single Node, relabel the Node with the same label as the OG
        //If there are multiple pieces left, relabel them all as pieces of OG
        //Of course, remove the old Tasks from the demo, and insert the new ones in their place
        void updateDemo (List<Node> demo, int labelId) throws IncorporationError {
            List<Node> tasksFromOG = new LinkedList<Node>();
            Integer startOfPiece = null;
            Integer endOfPiece = null;

            int length = demoIndexedInGroup.length;

            //go from end to start, so that replacing part of the demo with a single task does not affect the rest of the loop!
            for (int i=length-1; i>=0; i--){
                if (demoIndexedInGroup[i] >= 0) { //this Node was a piece of the OG...
                    if (i!=length-1 && upToDateDemoNodes[i].strictEquals(upToDateDemoNodes[i+1])) {
                        //we are part of the same task as the last index we checked
                        startOfPiece=i;
                        if (i==0) {replaceTasksInDemo(demo, startOfPiece, endOfPiece);}
                    }

                    else {
                        //we are a newly found task 
                        tasksFromOG.add(upToDateDemoNodes[i]);
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
            //we have replaced them with their new nodes, which are not correctly labeled, so let get to it...
            int numberOfPieces = tasksFromOG.size();
            if (numberOfPieces==1) {
                for (Node piece : tasksFromOG) {
                    piece.setLabel(new Label(labelId));
                }
            }
            else if (numberOfPieces>1 ) {
                int index=0;
                for (Node piece : tasksFromOG) {
                    piece.setLabel(new PieceLabel(labelId, numberOfPieces,index++));
                }
            }  //numberOfPieces is the size of the brotherhood
            else {throw new IncorporationError ("No parts of the Ordered InnerNode were found while incorporating!");}


        }

        private void replaceTasksInDemo (List<Node> demo, Integer start, Integer end) {
            if ( start==null || end ==null) return;

            int demoSize = demo.size();
            Node replacementNode = upToDateDemoNodes[start];

            List<Node> startOfDemo = demo.subList(0, start);
            List<Node> endOfDemo = demo.subList(end+1, demoSize);
            List<Node> newDemo = new ArrayList<Node>(demoSize-end+start-1);
            newDemo.addAll(startOfDemo);
            newDemo.add(replacementNode);
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
     * Absorb: When a InnerNode, parent, deletes one of its subNodes, child, and adds all of child's subNodes
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
     *   rOG = reversible OrderedNode
     *   We want to avoid  non-reversible OrderedNode(ABCDE)  +  demo=EDCBA  from becoming
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
     *   Since we only want to absorb "recently created OGs", we initialize the original Node direction to 0 (default)
     *   Why? We do not want to accidentally absorb a pre-existing rOG into a new rOG.
     *   It is totally legitimate to have a reversible within a reversible
     */

    private abstract class GroupBuilder {

        public GroupBuilder (int demoLength) {}

        abstract public Node buildTask (VerifiedGroupOfTasks verifiedGroup) throws IncorporationError;

    }


    /*
     * This class is used when incorporating a reversible oGroup
     *
     * We follow these rules:
     * - If a node of nodes contains unresolved pieces we make an unordered node
     * - If a node of nodes is in the same direction in the oGroup and in the demo:
     *       - make a non-reversible OrderedNode
     *       - note that the direction was the same (1)
     * - If a node of nodes is in the opposite direction:
     *       - make a reversible OrderedNode
     *       - note that the direction was opposite (-1)
     * - Otherwise make an unordered node and note that the direction was scrambled (0)
     */
    private class GroupBuilderFromNonReversible extends GroupBuilder {
        GroupBuilderFromNonReversible (int demoLength){
            super(demoLength);
        }

        public Node buildTask (VerifiedGroupOfTasks verifiedGroup) throws IncorporationError {
            Node node;
            List<Node> nodes = verifiedGroup.nodes;
            int direction = verifiedGroup.direction;
            List<Integer> subTaskDirections = verifiedGroup.subTaskDirections;

            //if there are any unresolved pieces we have to make an UnOrderedNode no matter what
            if (verifiedGroup.numberOfUnResolvedPieces>0) {
                node = new UnOrderedNode(new Label(), verifiedGroup.nodes);
            }

            else {
                if (direction==1){ //increasing node indices. ex: 3, 4, 7, 11
                    node = new OrderedNode(new Label(), nodes, false, subTaskDirections, direction); //false bc node order is not reversible
                }
                else if (direction==-1){  //decreasing node indices. ex: 11, 7, 4, 3
                    node = new OrderedNode(new Label(), nodes, true, subTaskDirections, direction); //true bc node order is reversible
                }
                else if (direction==0){  //fluctuating node indices. ex: 11, 4, 7, 3
                    node = new UnOrderedNode(new Label(), nodes);
                }
                else {throw new IncorporationError("Unsupported direction passed into buildNode()");}
            }

            return node;
        }

    }

    /*
     * This class is used when incorporating a reversible oGroup
     *
     * We follow the same rules as a GroupBuilderFromNonReversible except we ALWAYS make reversible OGs INSTEAD of non-reversible OGs
     * Why? Obviously, if the sequence of Tasks already existed in a reversible, it must have previously been seen in both direction
     */
    private class GroupBuilderFromReversible extends GroupBuilder {
        GroupBuilderFromReversible (int demoLength){
            super(demoLength);
        }

        public Node buildTask (VerifiedGroupOfTasks verifiedGroup) throws IncorporationError {
            Node node;
            List<Node> nodes = verifiedGroup.nodes;
            int direction = verifiedGroup.direction;
            List<Integer> subTaskDirections = verifiedGroup.subTaskDirections;

            //if there are any unresolved pieces we have to make an UnOrderedNode no matter what
            if (verifiedGroup.numberOfUnResolvedPieces>0) {
                node = new UnOrderedNode(new Label(), verifiedGroup.nodes);
            }
            else {
                if (direction==1){ //increasing node indices. ex: 3, 4, 7, 11
                    node = new OrderedNode(new Label(), nodes, true, subTaskDirections, direction); //true bc node order is reversible
                }
                else if (direction==-1){  //decreasing node indices. ex: 11, 7, 4, 3
                    node = new OrderedNode(new Label(), verifiedGroup.nodes, true, subTaskDirections, direction); //true bc node order is reversible
                }
                else if (direction==0){  //fluctuating node indices. ex: 11, 4, 7, 3
                    node = new UnOrderedNode(new Label(), verifiedGroup.nodes);
                }
                else {throw new IncorporationError("Unsupported direction passed into buildNode()");}
            }

            return node;
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
        private List<Node> nodes;
        private List<Integer> subTaskDirections;
        private int direction;
        private int numberOfUnResolvedPieces;

        VerifiedGroupOfTasks (List<Node> tasksToBeGrouped, int dir, List<Integer> subTaskDirs, int numOfUnResolvedPieces) {
            nodes = tasksToBeGrouped;
            direction = dir;
            subTaskDirections = subTaskDirs;
            numberOfUnResolvedPieces = numOfUnResolvedPieces;
        }
    }
}
