package contiguityTree;

import java.util.Arrays;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        //basicTest();
        superTest();
    }
    
    public static void basicTest () {
    	System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Starting Basic Test");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        
        ContiguityTree tree = new ContiguityTree();
        System.out.println("Successfully made tree\n");
        
        Object[] demo1 = new Object[] { "A", "B", "C", "D", "E" };
        Object[] demo2 = new Object[] { "D", "A", "C", "B", "E" };
        Object[] demo3 = new Object[] { "E", "D", "C", "B", "A" };
        Object[] demo4 = new Object[] { "B", "E", "D", "A", "C" };
        Object[] demo5 = new Object[] { "C", "A", "E", "B", "D" };
        
        observeDemo(tree,demo1);
        copyPrintAndAssertEqual(tree);
        runTraversalExperiment(tree);
        
        observeDemo(tree,demo2);
        copyPrintAndAssertEqual(tree);
        runTraversalExperiment(tree);
        
        observeDemo(tree,demo3);
        copyPrintAndAssertEqual(tree);
        runTraversalExperiment(tree);
        
        observeDemo(tree,demo4);
        copyPrintAndAssertEqual(tree);
        runTraversalExperiment(tree);
        
        observeDemo(tree,demo5);
        copyPrintAndAssertEqual(tree);
        runTraversalExperiment(tree);
        
        //Incremental Demo Example
        tree.startNewDemo();
        tree.sendNextDemoObject("B");
        tree.sendNextDemoObject("A");
        tree.sendNextDemoObject("D");
        tree.sendNextDemoObject("E");
        tree.sendNextDemoObject("C");
        tree.sendDemo();
        
        tree.print();
        System.out.println();
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Basic Test Done");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        System.out.println();
    }
    
    private static void observeDemo(ContiguityTree ct, Object[] demo){
        printDemo(demo);
        System.out.println();
        ct.observeDemo(demo);
        System.out.println("Resulting Tree:");
        ct.print();     
    }
    
    private static void copyPrintAndAssertEqual(ContiguityTree ct){
        ContiguityTree treeCopy = ct.fullCopy();
        System.out.println("Making a full copy of the tree:");
        treeCopy.print();
        if (ct.equals(treeCopy)) {System.out.println("The copy and the original trees are equal");}
        else {throw new Error ("The copy was not equal to the other tree!");}
        System.out.println();
    }
    
    private static void printDemo (Object[] demo){
        System.out.print("Observing Demo: ");
        for (int i=0; i<demo.length; i++) System.out.print(demo[i]+ ", ");
        System.out.println();
    }
    
    private static void runTraversalExperiment (ContiguityTree ct) {
    	System.out.println("Starting Experiment...");
        System.out.println("It took "+ct.recreateTreeUsingRandomTraversalsAsDemonstrations()+" random traversal(s) to recreate an equivalent tree");
        System.out.println();
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
    }
    
    
    
    static int [] a1 = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,33,34};
    static int [] a2 = new int[] {13,14,15,11,12,17,18,16,21,28,23,26,24,27,25,29,30,31,19,20,33,34,32,5,6,4,9,7,10,8,2,3,1};
    static int [] a3 = new int[] {18,16,17,30,31,19,20,21,24,25,26,27,28,23,29,34,32,33,7,8,9,10,3,1,2,6,4,5,11,12,13,14,15};
    
    public static void superTest () {
    	System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Starting Super Test");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        
        
    	ContiguityTree giant = new ContiguityTree();
    	
    	accumulateAndSendDemosToTree(giant,a3);
    	accumulateAndSendDemosToTree(giant,a2);
    	accumulateAndSendDemosToTree(giant,a1);
    	
    	System.out.println();
    	giant.print();
    	
    	System.out.println();
    	runTraversalExperiment(giant);
    	
    	System.out.println();
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Super Test Done");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        System.out.println();
    }
    
    public static void accumulateAndSendDemosToTree (ContiguityTree ct, int []... demoParts){
    	int totalLength =0;
    	for (int [] d : demoParts) {
    		totalLength+= d.length;
    	}
    	Integer [] accumulatedDemo = new Integer [totalLength];
    	int index=0;
    	for (int [] d : demoParts) {
    		for (int j=0; j<d.length; j++){
    			accumulatedDemo[index++]=d[j];
    		}
    	}
    	System.out.println("Observing Demo: "+ Arrays.toString(accumulatedDemo));
    	ct.observeDemo(accumulatedDemo);
    }
}
