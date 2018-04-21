package contiguityTree;

// SubLabels are used to track pieces of split nodes
public class PieceLabel extends Label {
	private int brotherhoodSize; // number of brother pieces
	private int uuid;
	
	PieceLabel (int labelId, int bros, int id) {
		super(labelId);
		brotherhoodSize = bros;
		uuid = id;
	}
	
	// PieceLabels are strictEqual as long as they have the same id and uuid and are both PieceLabels
    public boolean strictEquals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PieceLabel)) return false;
        PieceLabel pLabel = (PieceLabel) obj;
        return id == pLabel.id && uuid==pLabel.uuid;
    }
	
    public Label copyLabel() {
        return new PieceLabel(id, brotherhoodSize, uuid);
    }
	
	public String toString () { return id + "("+uuid+"/"+brotherhoodSize+")"; }
    public boolean isPiece () { return true; }
    public int getBrotherhoodSize () { return brotherhoodSize; }
}
