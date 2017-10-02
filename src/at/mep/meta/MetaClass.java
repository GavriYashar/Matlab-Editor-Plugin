package at.mep.meta;

import at.mep.Matlab;
import at.mep.editor.tree.EAttributes;
import at.mep.installer.Install;

import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2016-09-12. */
@Deprecated
public class MetaClass extends Meta {
    protected boolean isSealed = false;
    protected boolean isAbstract = false;
    protected boolean isEnumeration = false;
    protected boolean isConstructOnLoad = false;
    protected boolean isHandleCompatible = false;

    protected List<MetaProperty> properties = new ArrayList<>(10);
    protected List<MetaMethod> methods = new ArrayList<>(10);

    public MetaClass() {
    }

    public static MetaClass getMatlabClass(String name) throws Exception {
        String version = Install.getVersion().replace(".", "_");
        String metaVar = "MEP_" + version + "_meta";
        String classVar = "MEP_" + version + "_c";
        String propVar = "MEP_" + version + "_p";
        String propVarP = "MEP_" + version + "_pp";
        String methVar = "MEP_" + version + "_m";
        String methVarM = "MEP_" + version + "_mm";
        String fori = "MEP_" + version + "_i";
        String forj = "MEP_" + version + "_j";

        String command = metaVar + " = ?" + name + ";\n"
                + classVar + " = at.mep.meta.MetaClass();\n"
                + classVar + ".setName(" + metaVar + ".Name);\n"
                + classVar + ".setDescription(" + metaVar + ".Description);\n"
                + classVar + ".setDetailedDescription(" + metaVar + ".DetailedDescription);\n"
                + classVar + ".setHidden(" + metaVar + ".Hidden);\n"
                + classVar + ".setSealed(" + metaVar + ".Sealed);\n"
                + classVar + ".setAbstract(" + metaVar + ".Abstract);\n"
                + classVar + ".setEnumeration(" + metaVar + ".Enumeration);\n"
                + classVar + ".setConstructOnLoad(" + metaVar + ".ConstructOnLoad);\n"
                + classVar + ".setHandleCompatible(" + metaVar + ".HandleCompatible);\n\n"

                + " for " + fori + " = numel(" + metaVar + ".PropertyList):-1:1\n"
                + "     " + propVarP + " = " + metaVar + ".PropertyList(" + fori + ");\n"
                + "     " + propVar + " = at.mep.meta.MetaProperty();\n"
                + "     " + propVar + ".setName(" + propVarP + ".Name);\n"
                + "     " + propVar + ".setDescription(" + propVarP + ".Description);\n"
                + "     " + propVar + ".setDetailedDescription(" + propVarP + ".DetailedDescription);\n"
                + "     " + propVar + ".setHasSetter(~isempty(" + propVarP + ".SetMethod));\n"
                + "     " + propVar + ".setHasGetter(~isempty(" + propVarP + ".GetMethod));\n"

                // ACCESS
                // TODO: fill list with meta classes propVarP.GetAccess/SetAccess
                + "     " + "if iscell(" + propVarP + ".GetAccess)\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.META);\n"
                + "     " +  "elseif ischar(" + propVarP + ".GetAccess) && strcmpi(" + propVarP + ".GetAccess, 'private')\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.PRIVATE);\n"
                + "     " +  "elseif ischar(" + propVarP + ".GetAccess) && strcmpi(" + propVarP + ".GetAccess, 'public')\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.PUBLIC);\n"
                + "     " +  "elseif ischar(" + propVarP + ".GetAccess) && strcmpi(" + propVarP + ".GetAccess, 'protected')\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.PROTECTED);\n"
                + "     " +  "elseif ischar(" + propVarP + ".GetAccess) && strcmpi(" + propVarP + ".GetAccess, 'immutable')\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.IMMUTABLE);\n"
                + "     " +  "else\n"
                + "         " + propVar + ".setGetAccess(at.mep.meta.EAccess.INVALID);\n"
                + "     " +  "end\n"
                + "     " +  "if iscell(" + propVarP + ".SetAccess)\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.META);\n"
                + "     " +  "elseif ischar(" + propVarP + ".SetAccess) && strcmpi(" + propVarP + ".SetAccess, 'private')\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.PRIVATE);\n"
                + "     " +  "elseif ischar(" + propVarP + ".SetAccess) && strcmpi(" + propVarP + ".SetAccess, 'public')\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.PUBLIC);\n"
                + "     " +  "elseif ischar(" + propVarP + ".SetAccess) && strcmpi(" + propVarP + ".SetAccess, 'protected')\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.PROTECTED);\n"
                + "     " +  "elseif ischar(" + propVarP + ".SetAccess) && strcmpi(" + propVarP + ".SetAccess, 'immutable')\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.IMMUTABLE);\n"
                + "     " +  "else\n"
                + "         " + propVar + ".setSetAccess(at.mep.meta.EAccess.INVALID);\n"
                + "     " +  "end\n"

                + "     " + propVar + ".setDependent(" + propVarP + ".Dependent);\n"
                + "     " + propVar + ".setConstant(" + propVarP + ".Constant);\n"
                + "     " + propVar + ".setAbstract(" + propVarP + ".Abstract);\n"
                + "     " + propVar + ".setTransient(" + propVarP + ".Transient);\n"
                + "     " + propVar + ".setHidden(" + propVarP + ".Hidden);\n"
                + "     " + propVar + ".setHasDefaults(" + propVarP + ".HasDefault);\n"
                + "     " + propVar + ".setDefiningClass(" + propVarP + ".DefiningClass.Name);\n"
                + "     " + classVar + ".addProperty(" + propVar +");\n"
                + " end\n\n"

                + " for " + fori + " = numel(" + metaVar + ".MethodList):-1:1\n"
                + "     " + methVarM + " = " + metaVar + ".MethodList(" + fori + ");\n"
                + "     " + methVar + " = at.mep.meta.MetaMethod();\n"
                + "     " + methVar + ".setName(" + methVarM + ".Name);\n"
                + "     " + methVar + ".setDescription(" + methVarM + ".Description);\n"
                + "     " + methVar + ".setDetailedDescription(" + methVarM + ".DetailedDescription);\n"


                // ACCESS
                // TODO: fill list with meta classes methVarM.Access
                + "     " + "if iscell(" + methVarM + ".Access)\n"
                + "         " + methVar + ".setAccess(at.mep.meta.EAccess.META);\n"
                + "     " +  "elseif ischar(" + methVarM + ".Access) && strcmpi(" + methVarM + ".Access, 'private')\n"
                + "         " + methVar + ".setAccess(at.mep.meta.EAccess.PRIVATE);\n"
                + "     " +  "elseif ischar(" + methVarM + ".Access) && strcmpi(" + methVarM + ".Access, 'public')\n"
                + "         " + methVar + ".setAccess(at.mep.meta.EAccess.PUBLIC);\n"
                + "     " +  "elseif ischar(" + methVarM + ".Access) && strcmpi(" + methVarM + ".Access, 'protected')\n"
                + "         " + methVar + ".setAccess(at.mep.meta.EAccess.PROTECTED);\n"
                + "     " +  "else\n"
                + "         " + methVar + ".setAccess(at.mep.meta.EAccess.INVALID);\n"
                + "     " +  "end\n"

                + "     " + methVar + ".setStatic(" + methVarM + ".Static);\n"
                + "     " + methVar + ".setAbstract(" + methVarM + ".Abstract);\n"
                + "     " + methVar + ".setSealed(" + methVarM + ".Sealed);\n"
                + "     " + methVar + ".setHidden(" + methVarM + ".Hidden);\n"
                + "     for " + forj + " = 1:numel(" + methVarM + ".InputNames)\n"
                + "         " + methVar + ".addInputName(" + methVarM + ".InputNames{" + forj + "});\n"
                + "     end\n"
                + "     for " + forj + " = 1:numel(" + methVarM + ".OutputNames)\n"
                + "         " + methVar + ".addOutputName(" + methVarM + ".OutputNames{" + forj + "});\n"
                + "     end\n"
                + "     " + methVar + ".setDefiningClass(" + methVarM + ".DefiningClass.Name);\n"
                + "     " + classVar + ".addMethod(" + methVar + ");\n"
                + " end\n";

        String commandClear = "clear "
                + metaVar + " "
                + classVar + " "
                + propVar + " "
                + propVarP + " "
                + methVar + " "
                + methVarM + " "
                + fori + " "
                + forj;
        Matlab.getInstance().proxyHolder.get().eval(command);
        MetaClass c = (MetaClass) Matlab.getInstance().proxyHolder.get().getVariable(classVar);
        Matlab.getInstance().proxyHolder.get().eval(commandClear);
        return c;

    }

    public void setSealed(boolean sealed) {
        isSealed = sealed;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public void setEnumeration(boolean enumeration) {
        isEnumeration = enumeration;
    }

    public void setConstructOnLoad(boolean constructOnLoad) {
        isConstructOnLoad = constructOnLoad;
    }

    public void setHandleCompatible(boolean handleCompatible) {
        isHandleCompatible = handleCompatible;
    }

    public void setProperties(List<MetaProperty> properties) {
        this.properties = properties;
    }

    public void setMethods(List<MetaMethod> methods) {
        this.methods = methods;
    }

    public void addMethod(MetaMethod method) {
        methods.add(method);
    }

    public void addProperty(MetaProperty property) {
        properties.add(property);
    }

    public boolean isSealed() {
        return isSealed;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isEnumeration() {
        return isEnumeration;
    }

    public boolean isConstructOnLoad() {
        return isConstructOnLoad;
    }

    public boolean isHandleCompatible() {
        return isHandleCompatible;
    }

    public List<MetaProperty> getProperties() {
        return properties;
    }

    public List<MetaMethod> getMethods() {
        return methods;
    }

    @Override
    public void populate(EAttributes attribute, EAccess access) {
        switch (attribute) {
            case ABSTRACT:
                isAbstract = access.convertBoolean();
                break;
            case ALLOWEDSUBCLASSES:
                break;
            case CONSTRUCTONLOAD:
                break;
            case HANDLECOMPATIBLE:
                break;
            case HIDDEN:
                isHidden = access.convertBoolean();
                break;
            case INFERIORCLASSES:
                break;
            case SEALED:
                isSealed = access.convertBoolean();
                break;
        }
    }
}
