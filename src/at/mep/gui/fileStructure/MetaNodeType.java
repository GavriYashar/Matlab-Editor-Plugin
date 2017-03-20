package at.mep.gui.fileStructure;

public enum MetaNodeType {
    INVALID(-1),
    STRING(0),
    MATLAB(1),
    META_CLASS(2),
    META_METHOD(3),
    META_PROPERTY(4);

    private final int nodeType;

    MetaNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public int getNodeType() {
        return this.nodeType;
    }

}
