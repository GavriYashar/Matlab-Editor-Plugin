package at.mep.meta;

/** Created by Gavri on 2017-03-21. */
public enum EMetaAccess {
    INVALID(-1),
    PRIVATE(0),
    PROTECTED(1),
    PUBLIC(2),
    META(3);

    private final int access;

    EMetaAccess(int access) {
        this.access = access;
    }

    public int getAccess() {
        return access;
    }
}
