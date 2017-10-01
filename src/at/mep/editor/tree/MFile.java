package at.mep.editor.tree;

import at.mep.util.TreeUtilsV2;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

public class MFile {
    private List<ClassDef> classDefs;
    private List<CellTitle> cellTitles;
    private List<ClassDef.Method.Function> functions;

    private MFile() {
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
        private MTree.Node name = MTree.NULL_NODE;

        private CellTitle() {
        }

        public MTree.Node getName() {
            return name;
        }

        public static List<CellTitle> construct(List<MTree.Node> mtnCellTitle) {
            List<CellTitle> cellTitles = new ArrayList<>(mtnCellTitle.size());

            for (MTree.Node node : mtnCellTitle) {
                CellTitle cellTitle = new CellTitle();
                cellTitle.name = node;
                cellTitles.add(cellTitle);
            }

            return cellTitles;
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
            private MTree.Node attribute = MTree.NULL_NODE;
            private List<MTree.Node> value = Arrays.asList(MTree.NULL_NODE);

            private Attribute() {
            }

            public MTree.Node getAttribute() {
                return attribute;
            }

            public List<MTree.Node> getValue() {
                return value;
            }

            public static List<Attribute> construct(List<MTree.Node> mtnAttribute) {
                List<Attribute> attributeList = new ArrayList<>(mtnAttribute.size());
                for (MTree.Node node : mtnAttribute) {
                    Attribute attribute = new Attribute();

                    attribute.attribute = node.getLeft();

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
        private MTree.Node name = MTree.NULL_NODE;
        private List<MTree.Node> superclasses = Arrays.asList(MTree.NULL_NODE);
        private List<Attributes> attributes;
        private List<Properties> properties;
        private List<Method> method;

        private ClassDef() {
        }

        public MTree.Node getName() {
            return name;
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

                classDef.name = TreeUtilsV2.mTreeNodeGetClassName(node);

                List<MTree.Node> superclasses = TreeUtilsV2.findNode(node.getLeft().getRight().getSubtree(), ID);
                if (superclasses.size() > 1) {
                    classDef.superclasses = superclasses.subList(1, superclasses.size());
                }

                classDef.attributes = Attributes.construct(node.getLeft().getLeft().getListOfNextNodes());

                List<MTree.Node> pn = TreeUtilsV2.findNode(mtnClassDef.get(0).getRight().getListOfNextNodes(), MTree.NodeType.PROPERTIES);
                classDef.properties = Properties.construct(pn);

                List<MTree.Node> mn = TreeUtilsV2.findNode(mtnClassDef.get(0).getRight().getListOfNextNodes(), MTree.NodeType.METHODS);
                classDef.method = Method.construct(mn);

                classDefs.add(classDef);
            }
            return classDefs;
        }

        public static class Properties {
            private List<Attributes> attributes;
            private List<Property> propertyList;

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

            public List<Attributes> getAttributes() {
                return attributes;
            }

            public List<Property> getPropertyList() {
                return propertyList;
            }

            public static class Property {
                MTree.Node name = MTree.NULL_NODE;
                MTree.Node definition = MTree.NULL_NODE;
                MTree.Node validators = MTree.NULL_NODE;

                private Property() {
                }

                public static List<Property> construct(List<MTree.Node> mtnProperty) {
                    List<Property> propertyList = new ArrayList<>(mtnProperty.size());

                    for (MTree.Node node : mtnProperty) {
                        Property property = new Property();

                        property.name = TreeUtilsV2.mTreeNodeGetPropertyName(node);

                        switch (node.getLeft().getType()) {
                            case ATBASE:
                                property.definition = node.getLeft().getRight();
                                property.validators = MTree.NULL_NODE;
                                break;
                            case PROPTYPEDECL:
                                property.definition = node.getLeft().getRight().getLeft();
                                property.validators = MTree.NULL_NODE;
                                break;
                        }

                        propertyList.add(property);
                    }
                    return propertyList;
                }

                public MTree.Node getName() {
                    return name;
                }

                public MTree.Node getDefinition() {
                    return definition;
                }

                public MTree.Node getValidators() {
                    return validators;
                }
            }
        }

        public static class Method {
            private List<Attributes> attributes;
            private List<Function> functionList;

            private Method() {
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
                private MTree.Node name = MTree.NULL_NODE;
                private List<MTree.Node> outArgs = Arrays.asList(MTree.NULL_NODE);
                private List<MTree.Node> inArgs = Arrays.asList(MTree.NULL_NODE);

                private Function() {
                }

                public MTree.Node getName() {
                    return name;
                }

                public List<MTree.Node> getOutArgs() {
                    return outArgs;
                }

                public List<MTree.Node> getInArgs() {
                    return inArgs;
                }

                public static List<Function> construct(List<MTree.Node> mtnFunction) {
                    List<Function> functions = new ArrayList<>(mtnFunction.size());

                    for (MTree.Node node : mtnFunction) {
                        Function function = new Function();

                        function.name = node.getFunctionName();
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
