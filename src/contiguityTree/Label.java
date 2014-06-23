package contiguityTree;

//Labels are used to track Nodes
public class Label {
	private static int next_available_id = 0;
	int id;
	
	Label () {
		id = next_available_id++;
	}
	
	Label (int i) {
		id=i;
	}
	
	//return 1 if both are Label (not subLabel) with same id
	public int myEquals (Label l) {
		if (l instanceof Label && l.id==id) return 1;
		return -1;
	}
	
	//GETTERS AND SETTERS
	public int getId () {return id;}
	public void setId (int id) {this.id = id;}
	
	public String toString () {
		return ""+id;
	}

}
