package contiguityTree;

public class Main {

    //TODO: We have some .conatins issues for the incorporator? We want .conatins to return to even with piece labels,
    //that means hashing and equals must be the same for both. Tricky!
    
    //on the other hand, we need to right a custom strict equals function, and add back uuid to the mix
    
	/**
	 * @param args
	 */
	public static void main (String[] args) {
		ContiguityTree ct = new ContiguityTree();
		System.out.println("Successfully made tree\n");
		
		Object[] demo1 = new Object [] {"A", "B", "C", "D", "E"};
		ct.observeDemo(demo1);
		System.out.println();
		System.out.println();
        System.out.println("Final Result:");
		ct.print();
		System.out.println();
		System.out.println();
		System.out.println();
		
		Object[] demo2 = new Object [] {"D", "A", "C", "B", "E"};
		ct.observeDemo(demo2);
		System.out.println();
		System.out.println();
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
		Object[] demo3 = new Object [] {"E", "D", "C", "B", "A"};
		ct.observeDemo(demo3);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
        System.out.println();

        Object[] demo4 = new Object [] {"B", "E", "D", "A", "C"};
        ct.observeDemo(demo4);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
        
        
        
        /*
        ContiguityTree ct2 = new ContiguityTree();
        System.out.println("Successfully made tree\n");
        
        Object[] demo12 = new Object [] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
        ct2.observeDemo(demo12);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct2.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
        Object[] demo22 = new Object [] {"B", "C", "D", "J", "G", "I", "H", "F", "K", "E", "L", "M", "A"};
        ct2.observeDemo(demo22);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct2.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
        Object[] demo32 = new Object [] {"B", "E", "C", "M", "D", "K", "L", "J", "G", "H", "F", "I", "A"};
        ct2.observeDemo(demo32);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct2.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
        Object[] demo42 = new Object [] {"B", "I", "K", "E", "C", "M", "F", "D", "L", "J", "G", "A", "H"};
        ct2.observeDemo(demo42);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct2.print();
        System.out.println();
        System.out.println();
        System.out.println();*/
        
	}

}
