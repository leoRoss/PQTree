package contiguityTree;

import java.util.List;

public abstract class Incorporator {
    protected List <Node> permutation;
    protected InnerNode node;
    public abstract void incorporate() throws IncorporationError;
}
