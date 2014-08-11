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
		ct.print();
		Object[] demo2 = new Object [] {"D", "A", "C", "B", "E"};
		ct.observeDemo(demo2);
		ct.print();
	}

}
