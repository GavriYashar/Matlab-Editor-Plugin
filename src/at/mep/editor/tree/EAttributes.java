package at.mep.editor.tree;

import at.mep.meta.EAccess;

/**
 * Created by Andreas Justin on 2017-09-29.
 *
 * Enumeration of Attributes for Properties, Methods and Classdef
 */
public enum EAttributes {
    INVALID("Invalid", EAccess.INVALID),

    // matlab.unittest.TestCase class
    TEST("Test", EAccess.FALSE),
    TESTMETHODSETUP("TestMethodSetup", EAccess.FALSE),
    TESTMETHODTEARDOWN("TestMethodTeardown", EAccess.FALSE),
    TESTCLASSSETUP("TestClassSetup", EAccess.FALSE),
    TESTCLASSTEARDOWN("TestClassTeardown", EAccess.FALSE),

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
    INFERIORCLASSES("InferiorClasses", EAccess.META),
    LEARNABLE("Learnable", EAccess.FALSE); // nnet.layer.Layer

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
