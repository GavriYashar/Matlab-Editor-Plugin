package at.mep.meta;

/** Created by Gavri on 2017-03-21. */
public enum MetaAccessE {
    INVALID(-1),
    PRIVATE(0),
    PROTECTED(1),
    PUBLIC(2),
    META(3);

    private final int access;

    MetaAccessE(int access) {
        this.access = access;
    }

    public int getAccess() {
        return access;
    }
}
