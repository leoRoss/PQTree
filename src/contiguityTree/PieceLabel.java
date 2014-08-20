package contiguityTree;

//SubLabels are used to track pieces of split nodes
public class PieceLabel extends Label {
	private int brotherhoodSize; //number of brother pieces

	PieceLabel (int labelId, int bros) {
		super(labelId);
		brotherhoodSize=bros;
	}
	
	public String toString () {
		return id + "("+brotherhoodSize+")";
	}
	
	public boolean isPiece () {
		return true;
	}
	
	public int getBrotherhoodSize () {
		return brotherhoodSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * super.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PieceLabel)) return false;
        PieceLabel pieceLabel = (PieceLabel) obj;
		return id == pieceLabel.id;
	}
	
	public Label copyLabel() {
        PieceLabel copy = new PieceLabel(id, brotherhoodSize);
        return copy;
    }
}
