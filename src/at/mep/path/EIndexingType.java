package at.mep.path;


/**
 * dynamic: index gets updated as needed.
 * classes: does a scan for classes in matlab's search path, index also gets updated as needed
 * full: does a full scan for .m files in matlabs search path, index also gets updated as needed
 */
public enum EIndexingType{
    NONE (-1),
    DYNAMIC (0),
    CLASSES (1),
    FULL (2);

    private int indexingType;

    EIndexingType(int indexingType) {
        this.indexingType = indexingType;
    }

    public int getIndexingType() {
        return indexingType;
    }
}
