package contiguityTree;

public class Main {

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		ContiguityTree ct = new ContiguityTree();
		System.out.println("Successfully made tree\n");
		Object[] demo = new Object [] {"A", "B", "C"};
		ct.observeDemo(demo);
		ct.print();
	}

}
