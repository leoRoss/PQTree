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

	//Piece Labels are only ever hashed against other piece labels, thus we have a perfect hash distribution
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public Label copyLabel() {
        PieceLabel copy = new PieceLabel(id, brotherhoodSize);
        return copy;
    }
}
