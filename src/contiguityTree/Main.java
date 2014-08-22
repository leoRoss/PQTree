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
        observeDemo(tree,demo2);
        copyPrintAndAssertEqual(tree);
        observeDemo(tree,demo3);
        copyPrintAndAssertEqual(tree);
        ContiguityTree treeCopyAfterDemo3 = tree.fullCopy();
        observeDemo(tree,demo4);
        copyPrintAndAssertEqual(tree);
        ContiguityTree treeCopyAfterDemo4 = tree.fullCopy();
        observeDemo(tree,demo5);
        copyPrintAndAssertEqual(tree);
        
        if(treeCopyAfterDemo3.equals(tree)) throw new Error("treeCopyAfterDemo3 should NOT be the same as after demo5");
        if(!treeCopyAfterDemo4.equals(tree)) throw new Error("treeCopyAfterDemo4 should be the same as after demo5");
    }
    
    private static void observeDemo(ContiguityTree ct, Object[] demo){
        printDemo(demo);
        ct.observeDemo(demo);
        System.out.println("Final Result:");
        ct.print();     
    }
    
    private static void copyPrintAndAssertEqual(ContiguityTree ct){
        ContiguityTree treeCopy = ct.fullCopy();
        System.out.println("Copy:");
        treeCopy.print();
        if (ct.equals(treeCopy)) {System.out.println("The two trees are equal");}
        else {throw new Error ("The copy was not equal to the other tree!");}
        System.out.println();
        System.out.println();
    }
    
    private static void printDemo (Object[] demo){
        System.out.print("Demo: ");
        for (int i=0; i<demo.length; i++) System.out.print(demo[i]+ ", ");
        System.out.println();
    }
}
