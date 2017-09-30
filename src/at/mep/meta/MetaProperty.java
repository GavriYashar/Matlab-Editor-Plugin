package at.mep.meta;

import at.mep.editor.tree.EAttributePropertyMethod;
import at.mep.util.TreeUtilsV2;

/** Created by Andreas Justin on 2016-09-12. */
public class MetaProperty extends Meta {
    private String type = "";
    private String validators = "";
    private EMetaAccess getAccess = EMetaAccess.PUBLIC;
    private EMetaAccess setAccess = EMetaAccess.PUBLIC;
    protected String definingClass = "";
    private boolean isDependent = false;
    private boolean isConstant = false;
    private boolean isAbstract = false;
    private boolean isTransient = false;
    private boolean hasDefaults = false;
    private boolean hasSetter = false;
    private boolean hasGetter = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValidators() {
        return validators;
    }

    public void setValidators(String validators) {
        this.validators = validators;
    }

    public void setGetAccess(EMetaAccess getAccess) {
        this.getAccess = getAccess;
    }

    public void setGetAccess(String getAccess) {
        setGetAccess(EMetaAccess.valueOf(getAccess.toUpperCase()));
    }

    public void setSetAccess(EMetaAccess setAccess) {
        this.setAccess = setAccess;
    }

    public void setSetAccess(String setAccess) {
        setSetAccess(EMetaAccess.valueOf(setAccess.toUpperCase()));
    }

    public void setDefiningClass(String definingClass) {
        this.definingClass = definingClass;
    }

    public void setDependent(boolean dependent) {
        isDependent = dependent;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public void setHasDefaults(boolean hasDefaults) {
        this.hasDefaults = hasDefaults;
    }

    public void setHasSetter(boolean hasSetter) {
        this.hasSetter = hasSetter;
    }

    public void setHasGetter(boolean hasGetter) {
        this.hasGetter = hasGetter;
    }

    public EMetaAccess getGetAccess() {
        return getAccess;
    }

    public EMetaAccess getSetAccess() {
        return setAccess;
    }

    public String getDefiningClass() {
        return definingClass;
    }

    public boolean isDependent() {
        return isDependent;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public boolean isHasDefaults() {
        return hasDefaults;
    }

    public boolean isHasSetter() {
        return hasSetter;
    }

    public boolean isHasGetter() {
        return hasGetter;
    }

    public void populate(String name, String type, String validators) {
        this.name = name;
        this.type = type;
        this.validators = validators;
    }

    public void populate(TreeUtilsV2.PropertyHolder propertyHolder) {
        populate(propertyHolder.getName(), propertyHolder.getType(), propertyHolder.getValidator());
    }

    @Override
    public void populate(EAttributePropertyMethod attribute, EMetaAccess access) {
        switch (attribute) {
            case ABORTSET:
                break;
            case ABSTRACT:
                isAbstract = access.convertBoolean();
                break;
            case ACCESS:
                getAccess = access;
                setAccess = access;
                break;
            case CONSTANT:
                isConstant = access.convertBoolean();
                break;
            case DEPENDENT:
                isDependent = access.convertBoolean();
                break;
            case GETACCESS:
                getAccess = access;
                break;
            case GETOBSERVABLE:
                break;
            case HIDDEN:
                isHidden = access.convertBoolean();
                break;
            case NONCOPYABLE:
                break;
            case SETACCESS:
                setAccess = access;
                break;
            case SETOBSERVABLE:
                break;
            case TRANSIENT:
                isTransient = access.convertBoolean();
                break;
        }
    }
}
