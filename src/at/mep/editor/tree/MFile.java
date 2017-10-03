package at.mep.editor.tree;

import at.mep.editor.EditorWrapper;
import at.mep.meta.EAccess;
import at.mep.util.StringUtils;
import at.mep.util.TreeUtilsV2;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

public class MFile {
    /** name of file (or full qualified name if class) */
    private String name = "NAME NOT SET";

    /** list of class definition in file (matlab currently supports only one classdef)*/
    private List<ClassDef> classDefs = new ArrayList<>(0);

    /** list of cell title in mfile */
    private List<CellTitle> cellTitles = new ArrayList<>(0);

    /** list of all functions in mfile */
    private List<ClassDef.Method.Function> functions = new ArrayList<>(0);

    private MFile() {
    }

    public String getName() {
        return name;
    }

    public List<ClassDef> getClassDefs() {
        return classDefs;
    }

    public List<CellTitle> getCellTitles() {
        return cellTitles;
    }

    public List<ClassDef.Method.Function> getFunctions() {
        return functions;
    }

    public boolean hasClassDef() {
        return classDefs.size() > 0;
    }

    public boolean hasCellTitles() {
        return cellTitles.size() > 0;
    }

    public boolean hasFunctions() {
        return functions.size() > 0;
    }

    public static MFile construct(Editor editor) {
        MTree mTree = EditorWrapper.getMTreeFast(editor);
        MFile mFile = construct(mTree);

        if (mFile.name.equals("NAME NOT SET")) {
            mFile.name = EditorWrapper.getFullQualifiedClass(editor);
        }
        return mFile;
    }

    public static MFile construct(MTree mTree) {
        switch (mTree.getFileType()) {
            case ScriptFile:
                return constructForCellTitles(mTree);
            case FunctionFile:
                return constructFunctions(mTree);
            case ClassDefinitionFile:
                return constructForClassDef(mTree);
            case Unknown:
        }
        return new MFile();
    }

    private static MFile constructForCellTitles(MTree mTree) {
        MFile mFile = new MFile();
        mFile.cellTitles = CellTitle.construct(mTree.findAsList(CELL_TITLE));
        return mFile;
    }

    private static MFile constructFunctions(MTree mTree) {
        MFile mFile = new MFile();
        mFile.cellTitles = CellTitle.construct(mTree.findAsList(CELL_TITLE));
        mFile.functions = ClassDef.Method.Function.construct(mTree.findAsList(FUNCTION));
        return mFile;
    }

    private static MFile constructForClassDef(MTree mTree) {
        MFile mFile = new MFile();
        mFile.classDefs = ClassDef.construct(mTree.findAsList(CLASSDEF));
        mFile.cellTitles = CellTitle.construct(mTree.findAsList(CELL_TITLE));
        mFile.functions = ClassDef.Method.Function.construct(mTree.findAsList(FUNCTION));
        return mFile;
    }

    /** actual representation of MTree.NodeType.CELL_TITLE */
    public static class CellTitle{
        /** trimmed string of cell title w/o %% */
        private String titleString = "";

        /** Actual node to cell title (CELL_TITLE) */
        private MTree.Node node = MTree.NULL_NODE;

        private CellTitle() {
        }

        public MTree.Node getNode() {
            return node;
        }

        public static List<CellTitle> construct(List<MTree.Node> mtnCellTitle) {
            List<CellTitle> cellTitles = new ArrayList<>(mtnCellTitle.size());

            for (MTree.Node node : mtnCellTitle) {
                CellTitle cellTitle = new CellTitle();
                cellTitle.node = node;
                cellTitle.titleString = node.getText();
                cellTitles.add(cellTitle);
            }

            return cellTitles;
        }

        public String getTitleString() {
            if (titleString != null) {
                titleString = node.getText().replaceFirst("%%", "");
                titleString = StringUtils.trimStart(titleString);
                titleString = StringUtils.trimEnd(titleString);
            }
            return titleString;
        }
    }

    /** an actual representation of MTree.NodeType.ATTRIBUTES, different to ATTR*/
    public static class Attributes {

        /** list of ATTRIBUTES (afaik everythin only has one attributeS */
        private List<Attribute> attributeList = new ArrayList<>(0);

        private Attributes() {
        }

        public List<Attribute> getAttributeList() {
            return attributeList;
        }

        public boolean hasAttribute() {
            return attributeList.size() > 0;
        }

        public static List<Attributes> construct(List<MTree.Node> mtnAttributes) {
            List<Attributes> attributes = new ArrayList<>(mtnAttributes.size());

            for (MTree.Node node : mtnAttributes) {
                Attributes attributes1 = new Attributes();

                attributes1.attributeList = Attribute.construct(node.getLeft().getListOfNextNodes());

                attributes.add(attributes1);
            }
            return attributes;
        }

        /** an actual representation of MTRee.NodeType.ATTR, actual attributes you're interested in */
        public static class Attribute {
            /** actual node of attribute (ATTR) */
            private MTree.Node node = MTree.NULL_NODE;

            /** list of actual nodes of attribute values like public, true, private etc... */
            private List<MTree.Node> value = Arrays.asList(MTree.NULL_NODE);

            /** representation of attribute as enumeration */
            private EAttributes attributeAsEAttribute = EAttributes.INVALID;

            /** representation of access as enumeration */
            private EAccess accessAsEAccess = EAccess.INVALID;

            private Attribute() {
            }

            public EAttributes getAttributeAsEAttribute() {
                if (attributeAsEAttribute == EAttributes.INVALID) {
                    attributeAsEAttribute = EAttributes.valueOf(node.getText().toUpperCase());
                }
                return attributeAsEAttribute;
            }

            public EAccess getAccessAsEAccess() {
                if (accessAsEAccess == EAccess.INVALID) {
                    if (value.size() == 1 && value.get(0).getType() == JAVA_NULL_NODE) {
                        accessAsEAccess = EAccess.INVALID;
                    } else if (value.size() == 1 && value.get(0).getType() != JAVA_NULL_NODE) {
                        if (EAccess.getNames().contains(value.get(0).getText().toUpperCase())) {
                            accessAsEAccess = EAccess.valueOf(value.get(0).getText().toUpperCase());
                        } else {
                            accessAsEAccess = EAccess.META;
                        }

                    }
                }
                return accessAsEAccess;
            }

            public MTree.Node getNode() {
                return node;
            }

            public List<MTree.Node> getValue() {
                return value;
            }

            public static List<Attribute> construct(List<MTree.Node> mtnAttribute) {
                List<Attribute> attributeList = new ArrayList<>(mtnAttribute.size());
                for (MTree.Node node : mtnAttribute) {
                    Attribute attribute = new Attribute();

                    attribute.node = node.getLeft();

                    switch (node.getRight().getType()) {
                        case ID:
                            attribute.value = Arrays.asList(node.getRight());
                            break;
                        case LEFT_CURLY_BRACE:
                            attribute.value = TreeUtilsV2.findNode(node.getRight().getSubtree(), ID);
                            break;
                    }

                    attributeList.add(attribute);
                }
                return attributeList;
            }
        }
    }

    /** actual representation of MTree.NodeType.CLASSDEF */
    public static class ClassDef {
        /** actual node of class definition (CLASSDEF) */
        private MTree.Node node = MTree.NULL_NODE;

        /** list of actual nodes of all inherited classes (< bla & bla & ...) */
        private List<MTree.Node> superclasses = Arrays.asList(MTree.NULL_NODE);

        /** list of attributes for classdef (size = 1) */
        private List<Attributes> attributes = new ArrayList<>(0);

        /** list of properties for class */
        private List<Properties> properties = new ArrayList<>(0);

        /** lsit of methods for class */
        private List<Method> method = new ArrayList<>(0);

        private ClassDef() {
        }

        public boolean hasSuperClasses() {
            return superclasses.size() > 0 && superclasses.get(0).getType() != JAVA_NULL_NODE;
        }

        public boolean hasAttributes() {
            return attributes.size() > 0;
        }

        public boolean hasProperties() {
            return properties.size() > 0;
        }

        public boolean hasMethods() {
            return method.size() > 0;
        }

        public MTree.Node getNode() {
            return node;
        }

        public List<Attributes> getAttributes() {
            return attributes;
        }

        public List<Properties> getProperties() {
            return properties;
        }

        public List<Method> getMethod() {
            return method;
        }

        public List<MTree.Node> getSuperclasses() {
            return superclasses;
        }

        public static List<ClassDef> construct(List<MTree.Node> mtnClassDef) {
            List<ClassDef> classDefs = new ArrayList<>(mtnClassDef.size());

            for (MTree.Node node : mtnClassDef) {
                ClassDef classDef = new ClassDef();

                classDef.node = TreeUtilsV2.mTreeNodeGetClassName(node);

                List<MTree.Node> superclasses = TreeUtilsV2.findNode(node.getLeft().getRight().getSubtree(), ID);
                if (superclasses.size() > 1) {
                    classDef.superclasses = superclasses.subList(1, superclasses.size());
                }

                classDef.attributes = Attributes.construct(node.getLeft().getLeft().getListOfNextNodes());

                List<MTree.Node> pn = TreeUtilsV2.findNode(mtnClassDef.get(0).getRight().getListOfNextNodes(), MTree.NodeType.PROPERTIES);
                classDef.properties = Properties.construct(pn);

                List<MTree.Node> mn = TreeUtilsV2.findNode(mtnClassDef.get(0).getRight().getListOfNextNodes(), MTree.NodeType.METHODS);
                classDef.method = Method.construct(mn);

                List<Method.Function> functions = new ArrayList<>(30);
                List<Properties.Property> propertyList = new ArrayList<>(30);

                for (Method method : classDef.getMethod()) {
                    functions.addAll(method.getFunctionList());
                }
                for (Properties properties : classDef.getProperties()) {
                    propertyList.addAll(properties.getPropertyList());
                }
                Properties.Property.populateWithSetterGetterFunctions(propertyList, functions);

                classDefs.add(classDef);
            }
            return classDefs;
        }

        /** actual representation of MTree.NodeType.PROPERTIES, different to EQUALS (Property) */
        public static class Properties {
            /** list of attributes for properties (size = 1) */
            private List<Attributes> attributes = new ArrayList<>(0);

            /** list of properties you're actually interested in */
            private List<Property> propertyList = new ArrayList<>(0);

            private Properties() {
            }

            public static List<Properties> construct(List<MTree.Node> mtnProperties) {
                List<Properties> properties = new ArrayList<>(mtnProperties.size());

                for (MTree.Node node : mtnProperties) {
                    Properties properties1 = new Properties();

                    properties1.attributes = Attributes.construct(node.getLeft().getListOfNextNodes());

                    List<MTree.Node> pn = TreeUtilsV2.findNode(node.getRight().getListOfNextNodes(), EQUALS);
                    properties1.propertyList = Property.construct(pn);

                    properties.add(properties1);
                }
                return properties;
            }

            public boolean hasAttributes() {
                return attributes.size() > 0;
            }

            public boolean hasProperty() {
                return propertyList.size() > 0;
            }

            public List<Attributes> getAttributes() {
                return attributes;
            }

            public List<Property> getPropertyList() {
                return propertyList;
            }

            /** actual representation of MTree.NodeType.EQUALS, the kind of property you're actually interested in */
            public static class Property {
                /** returns property string with type declaration and validators incl ATBASE (@)
                 * e.g.: var1 double {mustBeReal, mustBeFinite}
                 *       var1@double
                 */
                String propertyString = null;

                /** actual definition node of property (EQUALS) */
                MTree.Node node = MTree.NULL_NODE;

                /** actual definition node of property (child/attribute? of @ (ATBASE) or e.g. var double (PROPTYPEDECL) */
                MTree.Node definition = MTree.NULL_NODE;

                /** list of validator nodes, (at least R2017a?) e.g. var double {mustBeReal, mustBeFinite} */
                List<MTree.Node> validators = Arrays.asList(MTree.NULL_NODE);

                /** function for setter of property e.g. set.var */
                Method.Function setter = new Method.Function();

                /** function for getter of property e.g. get.var */
                Method.Function getter = new Method.Function();

                boolean isAtBase = false;

                private Property() {
                }

                public boolean hasDefinition() {
                    return definition.getType() != JAVA_NULL_NODE;
                }

                public boolean hasValidators() {
                    return validators.size() > 0 && validators.get(0).getType() != JAVA_NULL_NODE;
                }

                public String getPropertyString() {
                    if (propertyString == null) {
                        StringBuilder string = new StringBuilder(30);
                        string.append(node.getText());
                        if (hasDefinition()) {
                            if (isAtBase) {
                                string.append("@");
                            } else {
                                string.append(" ");
                            }
                            string.append(definition.getText());
                        }
                        if (hasValidators()) {
                            string.append(" {");
                            for (MTree.Node node : validators){
                                string.append(node.getText()).append(", ");
                            }
                            string.delete(string.length()-2, string.length());
                            string.append("}");
                        }
                        propertyString = string.toString();
                    }
                    return propertyString;
                }

                public static List<Property> construct(List<MTree.Node> mtnProperty) {
                    List<Property> propertyList = new ArrayList<>(mtnProperty.size());

                    for (MTree.Node node : mtnProperty) {
                        Property property = new Property();

                        property.node = TreeUtilsV2.mTreeNodeGetPropertyName(node);

                        switch (node.getLeft().getType()) {
                            case ATBASE:
                                property.isAtBase = true;
                                property.definition = node.getLeft().getRight();
                                break;
                            case PROPTYPEDECL:
                                property.definition = node.getLeft().getRight().getLeft();
                                MTree.Node someNode = node.getLeft().getRight().getRight();
                                if (someNode != null && someNode.getType() != JAVA_NULL_NODE) {
                                    // TODO: need to check if this also exists in MATLAB R2017a (b) or only in R2016b or earlier versions
                                    // Matlab.verLessThan(9.1)?
                                    property.validators = TreeUtilsV2.findNode(someNode.getSubtree(), ID);
                                }
                                break;
                        }

                        propertyList.add(property);
                    }
                    return propertyList;
                }

                public static void populateWithSetterGetterFunctions(List<Property> propertyList, List<Method.Function> functions) {
                    List<Method.Function> setterList = new ArrayList<>(functions.size());
                    List<Method.Function> getterList = new ArrayList<>(functions.size());

                    // filter functions that are actually getter or setters
                    for (Method.Function function : functions) {
                        if (function.isSetter()) {
                            setterList.add(function);
                            continue;
                        }
                        if (function.isGetter()) {
                            getterList.add(function);
                        }
                    }

                    // set setters
                    for (Method.Function function : setterList) {
                        String functionName = function.getNode().getText();
                        for (Property property : propertyList) {
                            if (functionName.endsWith(property.getNode().getText())) {
                                property.setter = function;
                            }
                        }
                    }

                    // set getters
                    for (Method.Function function : getterList) {
                        String functionName = function.getNode().getText();
                        for (Property property : propertyList) {
                            if (functionName.endsWith(property.getNode().getText())) {
                                property.getter = function;
                            }
                        }
                    }
                }

                public MTree.Node getNode() {
                    return node;
                }

                public MTree.Node getDefinition() {
                    return definition;
                }

                public List<MTree.Node> getValidators() {
                    return validators;
                }

                public Method.Function getSetter() {
                    return setter;
                }

                public Method.Function getGetter() {
                    return getter;
                }

                public boolean hasGetter() {
                    return getter.getNode().getType() != JAVA_NULL_NODE;
                }

                public boolean hasSetter() {
                    return setter.getNode().getType() != JAVA_NULL_NODE;
                }
            }
        }

        /** actual representation of MTree.NodeType.METHODS, different to FUNCTION */
        public static class Method {
            /** list of attributes (size = 1) */
            private List<Attributes> attributes = new ArrayList<>(0);

            /** list of functions */
            private List<Function> functionList = new ArrayList<>(0);

            private Method() {
            }

            public boolean hasAttributes() {
                return attributes.size() > 0;
            }

            public List<Attributes> getAttributes() {
                return attributes;
            }

            public List<Function> getFunctionList() {
                return functionList;
            }

            public static List<Method> construct(List<MTree.Node> mtnMethod) {
                List<Method> methods = new ArrayList<>(mtnMethod.size());

                for (MTree.Node node : mtnMethod) {
                    Method method = new Method();

                    method.attributes = Attributes.construct(node.getLeft().getListOfNextNodes());
                    method.functionList = Function.construct(TreeUtilsV2.findNode(node.getRight().getListOfNextNodes(), FUNCTION));

                    methods.add(method);
                }

                return methods;
            }

            /** actual representation of MTree.NodeType.FUNCTION */
            public static class Function {
                /** string representation for file structure "[out1, out2] = function(in1, in2)" */
                private String functionString = null;

                /** actual node of Function (FUNCTION) */
                private MTree.Node node = MTree.NULL_NODE;

                /** list of nodes for output arguments */
                private List<MTree.Node> outArgs = Arrays.asList(MTree.NULL_NODE);

                /** list of nodes for input arguments */
                private List<MTree.Node> inArgs = Arrays.asList(MTree.NULL_NODE);

                private Function() {
                }

                public boolean hasOutArgs() {
                    return outArgs.size() > 0 && outArgs.get(0).getType() != JAVA_NULL_NODE;
                }

                public boolean hasInArgs() {
                    return inArgs.size() > 0 && inArgs.get(0).getType() != JAVA_NULL_NODE;
                }

                public boolean isGetter() {
                    return node.getText().startsWith("get.");
                }

                public boolean isSetter() {
                    return node.getText().startsWith("set.");
                }

                public MTree.Node getNode() {
                    return node;
                }

                public List<MTree.Node> getOutArgs() {
                    return outArgs;
                }

                public List<MTree.Node> getInArgs() {
                    return inArgs;
                }

                /** string representation for file structure "[out1, out2] = function(in1, in2)" */
                public String getFunctionString() {
                    if (functionString == null) {
                        StringBuilder string = new StringBuilder(30);
                        string.append(getOutArgsString());
                        if (hasOutArgs()) {
                            string.append(" = ");
                        }
                        string.append(getNode().getText());
                        string.append(getInArgsString());
                        functionString = string.toString();
                    }
                    return functionString;
                }

                /** string representation "[out1, out2]" */
                public String getOutArgsString() {
                    StringBuilder string = new StringBuilder(30);
                    if (hasOutArgs()) {
                        string.append("[");
                        for (MTree.Node node : outArgs){
                            string.append(node.getText()).append(", ");
                        }
                        string.delete(string.length()-2, string.length());
                        string.append("]");
                    }
                    return string.toString();
                }

                /** string representation "(in1, in2)" */
                public String getInArgsString() {
                    StringBuilder string = new StringBuilder(30);
                    if (hasInArgs()) {
                        string.append("(");
                        for (MTree.Node node : inArgs){
                            if (node.getType() == NOT) {
                                string.append(TreeUtilsV2.stringForMTreeNodeType(node.getType()));
                            } else {
                                string.append(node.getText());
                            }
                            string.append(", ");
                        }
                        string.delete(string.length()-2, string.length());
                        string.append(")");
                    }
                    return string.toString();
                }

                public static List<Function> construct(List<MTree.Node> mtnFunction) {
                    List<Function> functions = new ArrayList<>(mtnFunction.size());

                    for (MTree.Node node : mtnFunction) {
                        Function function = new Function();

                        function.node = node.getFunctionName();
                        function.outArgs = node.getOutputArguments();
                        function.inArgs = node.getInputArguments();

                        functions.add(function);
                    }

                    return functions;
                }
            }
        }
    }
}
