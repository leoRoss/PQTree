package contiguityTree;

//SubLabels are used to track pieces of split nodes
public class PieceLabel extends Label {
	private int brotherUUID; //unique id to differentiate amongst us brothers
	private int brothers; //number of brother pieces

	PieceLabel (int labelId, int uuid, int bros) {
		super(labelId);
		brotherUUID=uuid;
		brothers=bros;
	}
	
	public String toString () {
		return ""+id;
	}
	
	public boolean isPiece () {
		return true;
	}
	
	public int getNumberOfBrothers () {
		return brothers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * super.hashCode() + brotherUUID;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PieceLabel)) return false;
        PieceLabel pieceLabel = (PieceLabel) obj;
		return id == pieceLabel.id && brotherUUID == pieceLabel.brotherUUID;
	}
	
	public Label copyLabel() {
        PieceLabel copy = new PieceLabel(id, brotherUUID, brothers);
        return copy;
    }
}
