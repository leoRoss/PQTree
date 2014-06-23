package contiguityTree;

//SubLabels are used to track pieces of split nodes
public class SubLabel extends Label {

	SubLabel (int i) {
		super(i);
	}
	
	//return 0 if same id, -1 otherwise
	//1 is reserved for normal Labels
	public int myEquals (Label l) {
		if (l.id==id) return 0;
		return -1;
	}
	
	public String toString () {
		return ""+id;
	}
	
}
