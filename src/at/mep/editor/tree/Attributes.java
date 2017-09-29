package at.mep.editor.tree;

import at.mep.meta.EMetaAccess;

/** Created by Andreas Justin on 2017-09-29. */
public enum Attributes {
    ABORTSET("AbortSet", EMetaAccess.FALSE),
    ABSTRACT("Abstract", EMetaAccess.FALSE),
    ACCESS("Access", EMetaAccess.PUBLIC),
    CONSTANT("Constant", EMetaAccess.FALSE),
    DEPENDENT("Dependent", EMetaAccess.FALSE),
    GETACCESS("GetAccess", EMetaAccess.PUBLIC),
    GETOBSERVABLE("GetObservable", EMetaAccess.FALSE),
    HIDDEN("Hidden", EMetaAccess.FALSE),
    NONCOPYABLE("NonCopyAble", EMetaAccess.FALSE),
    SETACCESS("SetAccess", EMetaAccess.PUBLIC),
    SETOBSERVABLE("SetObservable", EMetaAccess.FALSE),
    TRANSIENT("Transient", EMetaAccess.FALSE),
    SEALED("Sealed", EMetaAccess.FALSE),
    STATIC("Static", EMetaAccess.FALSE);

    private final String stringMatlab;
    private final EMetaAccess defaultValue;

    Attributes(String stringMatlab, EMetaAccess defaultValue) {
        this.stringMatlab = stringMatlab;
        this.defaultValue = defaultValue;
    }
}
