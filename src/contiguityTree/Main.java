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
	}

}
