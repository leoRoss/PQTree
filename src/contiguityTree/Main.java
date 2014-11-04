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
    
    
    //SUPER TEST DEMOS BROKEN UP... for Leo's sanity :P
    static int [] a1 = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,33,34};
    static int [] a2 = new int[] {13,14,15,11,12,17,18,16,21,28,23,26,24,27,25,29,30,31,19,20,33,34,32,5,6,4,9,7,10,8,2,3,1};
    static int [] a3 = new int[] {18,16,17,30,31,19,20,21,24,25,26,27,28,23,29,34,32,33,7,8,9,10,3,1,2,6,4,5,11,12,13,14,15};
    
    static int [] c1 = new int[] {101,102};
    static int [] c2 = c1;
    static int [] c3 = c1;
    
    static int [] d1 = new int[] {301,302,303,304};
    static int [] d2 = new int[] {302,304,301,303};
    static int [] d3 = d1;
    
    static int [] e1 = new int[] {401,402,403};
    static int [] e2 = new int[] {403,402,401};
    static int [] e3 = e1;
    
    static int [] f1 = new int[] {501,502,503,504,505};
    static int [] f2 = new int[] {502,503,501,505,504};
    static int [] f3 = new int[] {503,501,502,504,505};
    
    //b group is more complex!
    static int [] ba1 = new int[] {201,202,203,204,205,206,207,208,209,210};
    static int [] ba2 = new int[] {209,210,204,207,206,205,208,202,203,201};
    static int [] ba3 = new int[] {203,201,202,208,205,206,207,204,209,210};
    
    static int [] bb1 = new int[] {211,212,213,214,215,216,217,218,219};
    static int [] bb2 = new int[] {219,212,213,211,218,217,215,216,214};
    static int [] bb3 = new int[] {216,214,215,217,218,219,213,211,212};
    
    static int [] bc1 = new int[] {220,221};
    static int [] bc2 = bc1;
    static int [] bc3 = bc1;
    
    static int [] be1 = new int[] {270,271,272};
    static int [] be2 = new int[] {272,271,270};
    static int [] be3 = be1;
    
    static int [] bf1 = new int[] {273,274,275,276,277,278};
    static int [] bf2 = new int[] {275,276,277,278,273,274};
    static int [] bf3 = new int[] {277,278,273,274,275,276};
    
    static int [] bd1 = new int[] {700,701,702};
    static int [] bd2 = bd1;
    static int [] bd3 = bd1;
    
    
    
    public static void superTest () {
    	System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Starting Super Test");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        
        
    	ContiguityTree giant = new ContiguityTree();
    	
    	accumulateAndSendDemosToTree(giant,a3,bf3,be3,bd3,bc3,bb3,ba3,c3,d3,e3,f3);
    	accumulateAndSendDemosToTree(giant,d2,ba2,bb2,bc2,bd2,be2,bf2,f2,a2,e2,c2);
    	accumulateAndSendDemosToTree(giant,a1,ba1,bb1,bc1,bd1,be1,bf1,c1,d1,e1,f1);
    	
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
