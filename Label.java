package leo.ContiguityTree;

//Labels are used to track Nodes
public class Label {
	int id;
	
	Label (int i) {
		id=i;
	}
	
	Label (Label l){
		id=l.getId();
	}
	
	//return 1 if both are Label (not subLabel) with same id
	public int myEquals (Label l) {
		if (l instanceof Label && l.id==id) return 1;
		return -1;
	}
	
	//GETTERS AND SETTERS
	public int getId () {return id;}
	public void setId (int id) {this.id = id;}
	
	public void print () {
		System.out.print(id);
	}
}
