package at.mep.editor.tree;

import at.mep.meta.EMetaAccess;

/** Created by Andreas Justin on 2017-09-29. */
public enum EAttributes {
    INVALID("Invalid", EMetaAccess.INVALID),
    
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
    STATIC("Static", EMetaAccess.FALSE),

    ALLOWEDSUBCLASSES("AllowedSubClasses", EMetaAccess.META),
    CONSTRUCTONLOAD("ConstructOnLoad", EMetaAccess.FALSE),
    HANDLECOMPATIBLE("HandleCompatible", EMetaAccess.FALSE),
    INFERIORCLASSES("InferiorClasses", EMetaAccess.META);

    private final String stringMatlab;
    private final EMetaAccess defaultAccess;

    EAttributes(String stringMatlab, EMetaAccess defaultValue) {
        this.stringMatlab = stringMatlab;
        this.defaultAccess = defaultValue;
    }

    public String getStringMatlab() {
        return stringMatlab;
    }

    public EMetaAccess getDefaultAccess() {
        return defaultAccess;
    }
}
