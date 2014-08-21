package contiguityTree;

//Labels are used to track Nodes

//A PieceLabel will only ever be equal to another PieceLabel (same id)
//A normal Label will only ever be equal to another Label (same id)
//PieceLabels are only ever hashed with other PieceLabels, same for normal Labels
//Thus hash is simply the id (perfect distribution)
public class Label {
	private static int next_available_id = 0;
	protected int id;
	
	public Label () {
		id = next_available_id++;
	}
	
	public Label (int i){
		id = i;
	}
	
	//GETTERS AND SETTERS
	public int getId () {return id;}
	public void setId (int id) {this.id = id;}
	
	public String toString () {
		return ""+id;
	}
	
	public boolean isPiece () {
		return false;
	}

	public boolean lenientEquals (Label lab) {
		return lab.id==id;
	}

	
	//Labels are only ever hashed against other Labels, not including PieceLabels. Thus we have a perfect hash distribution
	@Override
	public int hashCode () {
		return id;
	}
	
	
	
	public int getBrotherhoodSize () {
		return 1;
	}

	//Labels are equal as long as they have the same id and are not a PieceLabel
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (obj == null) return false;
        if (obj instanceof PieceLabel) return false;
        if (!(obj instanceof Label)) return false;
        Label label = (Label) obj;
		return id == label.id;
	}
	
	public Label copyLabel() {
	    Label copy = new Label(id);
	    return copy;
	}
}
