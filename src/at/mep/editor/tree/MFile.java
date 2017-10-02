package at.mep.editor.tree;

import at.mep.editor.EditorWrapper;
import at.mep.meta.EMetaAccess;
import at.mep.util.StringUtils;
import at.mep.util.TreeUtilsV2;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

public class MFile {
    private String name = "NAME NOT SET";
    private List<ClassDef> classDefs;
    private List<CellTitle> cellTitles;
    private List<ClassDef.Method.Function> functions;

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

    public static class CellTitle{
        private String titleString = null;
        private MTree.Node name = MTree.NULL_NODE;

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
                cellTitles.add(cellTitle);
            }

            return cellTitles;
        }

        public String getTitleString() {
            if (titleString == null) {
                titleString = StringUtils.trimStart(node.getText());
                titleString = StringUtils.trimEnd(titleString);
            }
            return titleString;
        }
    }

    public static class Attributes {
        private List<Attribute> attributeList;

        private Attributes() {
        }

        public List<Attribute> getAttributeList() {
            return attributeList;
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

        public static class Attribute {
            private MTree.Node node = MTree.NULL_NODE;
            private List<MTree.Node> value = Arrays.asList(MTree.NULL_NODE);

            private EAttributes attributeAsEAttribute = EAttributes.INVALID;
            private EMetaAccess accessAsEMetaAccess = EMetaAccess.INVALID;

            private Attribute() {
            }

            public EAttributes getAttributeAsEAttribute() {
                if (attributeAsEAttribute == EAttributes.INVALID) {
                    attributeAsEAttribute = EAttributes.valueOf(node.getText().toUpperCase());
                }
                return attributeAsEAttribute;
            }

            public EMetaAccess getAccessAsEMetaAccess() {
                if (accessAsEMetaAccess == EMetaAccess.INVALID) {
                    if (value.size() == 1 && value.get(0).getType() != JAVA_NULL_NODE) {
                        accessAsEMetaAccess = EMetaAccess.valueOf(value.get(0).getText().toUpperCase());
                    } else if (value.size() == 1 && value.get(0).getType() == JAVA_NULL_NODE) {
                        accessAsEMetaAccess = EMetaAccess.INVALID;
                    } else {
                        accessAsEMetaAccess = EMetaAccess.META;
                    }
                }
                return accessAsEMetaAccess;
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

    public static class ClassDef {
        private MTree.Node node = MTree.NULL_NODE;
        private List<MTree.Node> superclasses = Arrays.asList(MTree.NULL_NODE);
        private List<Attributes> attributes = new ArrayList<>(0);
        private List<Properties> properties = new ArrayList<>(0);
        private List<Method> method = new ArrayList<>(0);

        private ClassDef() {
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

        public static class Properties {
            private List<Attributes> attributes = new ArrayList<>(0);
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

            public static class Property {
                MTree.Node node = MTree.NULL_NODE;
                MTree.Node definition = MTree.NULL_NODE;
                List<MTree.Node> validators = Arrays.asList(MTree.NULL_NODE);

                Method.Function setter = new Method.Function();
                Method.Function getter = new Method.Function();

                private Property() {
                }

                public static List<Property> construct(List<MTree.Node> mtnProperty) {
                    List<Property> propertyList = new ArrayList<>(mtnProperty.size());

                    for (MTree.Node node : mtnProperty) {
                        Property property = new Property();

                        property.node = TreeUtilsV2.mTreeNodeGetPropertyName(node);

                        switch (node.getLeft().getType()) {
                            case ATBASE:
                                property.definition = node.getLeft().getRight();
                                break;
                            case PROPTYPEDECL:
                                property.definition = node.getLeft().getRight().getLeft();
                                property.validators = TreeUtilsV2.findNode(node.getLeft().getRight().getRight().getSubtree(), ID);
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

        public static class Method {
            private List<Attributes> attributes = new ArrayList<>(0);
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

            public static class Function {
                private String functionString = null;
                private MTree.Node node = MTree.NULL_NODE;
                private List<MTree.Node> outArgs = Arrays.asList(MTree.NULL_NODE);
                private List<MTree.Node> inArgs = Arrays.asList(MTree.NULL_NODE);

                private Function() {
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

                public String getFunctionString() {
                    if (functionString == null) {
                        StringBuilder string = new StringBuilder();

                        if (outArgs.size() > 0 && outArgs.get(0).getType() != JAVA_NULL_NODE) {
                            string.append("[");
                            for (MTree.Node node : outArgs){
                                string.append(node.getText()).append(", ");
                            }
                            string.delete(string.length()-2, string.length());
                            string.append("] = ");
                        }
                        string.append(getNode().getText());
                        if ((inArgs.size() > 0 && inArgs.get(0).getType() != JAVA_NULL_NODE)) {
                            string.append("(");
                            for (MTree.Node node : inArgs){
                                string.append(node.getText()).append(", ");
                            }
                            string.delete(string.length()-2, string.length());
                            string.append(")");
                        }
                        functionString = string.toString();
                    }
                    return functionString;
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
