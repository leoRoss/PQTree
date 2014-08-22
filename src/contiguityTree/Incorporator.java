package contiguityTree;

import java.util.List;

public abstract class Incorporator {
    protected List <Task> demo;
    protected Group group;
    public abstract void incorporate() throws IncorporationError;
}
