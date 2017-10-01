package at.mep.editor.tree;

import com.mathworks.widgets.text.mcode.MTree;

import java.util.List;

public class MFile {
    private List<ClassDef> classDefs;
    private List<CellTitle> cellTitles;
    private List<ClassDef.Methods.Function> functions;

    private MFile() {
    }

    public static MFile constructForCellTitles(MTree mTree) {
        MFile mFile = new MFile();
        return mFile;
    }

    public static MFile constructFunctions(MTree mTree) {
        MFile mFile = new MFile();
        return mFile;
    }

    public static MFile constructForClassDef(MTree mTree) {
        MFile mFile = new MFile();
        return mFile;
    }

    public static class CellTitle{
        public CellTitle() {

        }

        public static List<MTree.Node> construct(MTree mTree) {
            return mTree.findAsList(MTree.NodeType.CELL_TITLE);
        }
    }

    public static class Attributes {
        public Attributes() {
        }

        public static List<MTree.Node> construct(MTree mTree) {
            return mTree.findAsList(MTree.NodeType.ATTRIBUTES);
        }

        public static class Attribute {
            public Attribute() {
            }

            public static List<MTree.Node> construct(MTree mTree) {
                return mTree.findAsList(MTree.NodeType.ATTR);
            }
        }
    }

    public static class ClassDef {
        public ClassDef() {
        }

        public static List<MTree.Node> construct(MTree mTree) {
            return mTree.findAsList(MTree.NodeType.CLASSDEF);
        }

        public static class Properties {
            public Properties() {
            }

            public static List<MTree.Node> construct(MTree mTree) {
                return mTree.findAsList(MTree.NodeType.PROPERTIES);
            }

            public static class Property {
                public Property() {
                }

                public static List<MTree.Node> construct(MTree mTree) {
                    return mTree.findAsList(MTree.NodeType.EQUALS);
                }
            }
        }

        public static class Methods {
            public Methods() {
            }

            public static List<MTree.Node> construct(MTree mTree) {
                return mTree.findAsList(MTree.NodeType.METHODS);
            }

            public static class Function {
                public Function() {
                }

                public static List<MTree.Node> construct(MTree mTree) {
                    return mTree.findAsList(MTree.NodeType.FUNCTION);
                }
            }
        }
    }
}
