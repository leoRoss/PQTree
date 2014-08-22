package contiguityTree;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ContiguityTree ct = new ContiguityTree();
        System.out.println("Successfully made tree\n");

        Object[] demo1 = new Object[] { "A", "B", "C", "D", "E" };
        Object[] demo2 = new Object[] { "D", "A", "C", "B", "E" };
        Object[] demo3 = new Object[] { "E", "D", "C", "B", "A" };
        Object[] demo4 = new Object[] { "B", "E", "D", "A", "C" };
        Object[] demo5 = new Object[] { "C", "A", "E", "B", "D" };
        
        observeDemo(ct,demo1);
        observeDemo(ct,demo2);
        observeDemo(ct,demo3);
        observeDemo(ct,demo4);
        observeDemo(ct,demo5);
    }
    
    private static void observeDemo(ContiguityTree ct, Object[] demo){
        ct.observeDemo(demo);
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
    }
}
