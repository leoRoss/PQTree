package contiguityTree;

//Labels are used to track Nodes

//A Label will be equal (and will hash the same) as any other Lable or Piecelabel with the same id
//StrictEquals ensures two labels are the same type and have the same attributes (id and optionally brotherhood & uuid)
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

	//Labels are equal as long as they have the same id
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Label)) return false;
        Label label = (Label) obj;
		return id == label.id;
	}
	
	
	//Labels are strictequal as long as they have the same id and are both full labels, not PieceLabels
    public boolean strictEquals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Label)) return false;
        if ((obj instanceof PieceLabel)) return false;
        Label label = (Label) obj;
        return id == label.id;
    }
    
	public Label copyLabel() {
	    Label copy = new Label(id);
	    return copy;
	}
}
