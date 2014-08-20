package contiguityTree;

public class Main {

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
		ct.observeDemo(demo1);
		ct.observeDemo(demo2);
		ct.observeDemo(demo1);
        ct.observeDemo(demo2);
		System.out.println();
		System.out.println();
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
        System.out.println();
        
		Object[] demo3 = new Object [] {"E", "D", "C", "B", "A"};
		ct.observeDemo(demo2);
        ct.observeDemo(demo3);
        ct.observeDemo(demo1);
        ct.observeDemo(demo2);
        ct.observeDemo(demo3);
        System.out.println();
        System.out.println();
        System.out.println("Final Result:");
        ct.print();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        
        
        
        
        
        
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
        
	}

}
