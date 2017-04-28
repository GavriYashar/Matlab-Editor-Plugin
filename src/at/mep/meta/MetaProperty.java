package at.mep.meta;

/** Created by Andreas Justin on 2016-09-12. */
public class MetaProperty extends Meta {
    private EMetaAccess getAccess = EMetaAccess.PUBLIC;
    private EMetaAccess setAccess = EMetaAccess.PUBLIC;
    protected String definingClass = "";
    private boolean isDependent = false;
    private boolean isConstant = false;
    private boolean isAbstract = false;
    private boolean isTransient = false;
    private boolean hasDefaults = false;

    public void setGetAccess(EMetaAccess getAccess) {
        this.getAccess = getAccess;
    }

    public void setSetAccess(EMetaAccess setAccess) {
        this.setAccess = setAccess;
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
}
