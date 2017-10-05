package at.mep.editor.tree;

import at.mep.meta.EAccess;

/** Created by Andreas Justin on 2017-09-29. */
public enum EAttributes {
    INVALID("Invalid", EAccess.INVALID),
    
    TEST("Test", EAccess.FALSE),

    ABORTSET("AbortSet", EAccess.FALSE),
    ABSTRACT("Abstract", EAccess.FALSE),
    ACCESS("Access", EAccess.PUBLIC),
    CONSTANT("Constant", EAccess.FALSE),
    DEPENDENT("Dependent", EAccess.FALSE),
    GETACCESS("GetAccess", EAccess.PUBLIC),
    GETOBSERVABLE("GetObservable", EAccess.FALSE),
    HIDDEN("Hidden", EAccess.FALSE),
    NONCOPYABLE("NonCopyAble", EAccess.FALSE),
    SETACCESS("SetAccess", EAccess.PUBLIC),
    SETOBSERVABLE("SetObservable", EAccess.FALSE),
    TRANSIENT("Transient", EAccess.FALSE),

    SEALED("Sealed", EAccess.FALSE),
    STATIC("Static", EAccess.FALSE),

    ALLOWEDSUBCLASSES("AllowedSubClasses", EAccess.META),
    CONSTRUCTONLOAD("ConstructOnLoad", EAccess.FALSE),
    HANDLECOMPATIBLE("HandleCompatible", EAccess.FALSE),
    INFERIORCLASSES("InferiorClasses", EAccess.META);

    private final String stringMatlab;
    private final EAccess defaultAccess;

    EAttributes(String stringMatlab, EAccess defaultValue) {
        this.stringMatlab = stringMatlab;
        this.defaultAccess = defaultValue;
    }

    public String getStringMatlab() {
        return stringMatlab;
    }

    public EAccess getDefaultAccess() {
        return defaultAccess;
    }
}
