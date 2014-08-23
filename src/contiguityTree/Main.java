package contiguityTree;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
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
        System.out.println("Making a copy of the tree:");
        treeCopy.print();
        if (ct.equals(treeCopy)) {System.out.println("The two trees are indeed equal");}
        else {throw new Error ("The copy was not equal to the other tree!");}
        System.out.println();
    }
    
    private static void printDemo (Object[] demo){
        System.out.print("Observing Demo: ");
        for (int i=0; i<demo.length; i++) System.out.print(demo[i]+ ", ");
        System.out.println();
    }
    
    private static void runTraversalExperiment (ContiguityTree ct) {
        System.out.println("It took "+ct.recreateTreeUsingRandomTraversalsAsDemonstrations()+" random traversal(s) to recreate an equivalent tree");
        System.out.println();
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
    }
    
    
}
