package at.mep.editor.tree;

import at.mep.meta.MetaMethod;
import at.mep.meta.MetaProperty;
import at.mep.util.TreeUtilsV2;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

/** Created by Andreas Justin on 2017-09-28. */
public class MTreeWrapper {
    private static final int INITCAPACITY = 10;
    private MTree mTree;

    private Tree<MTree.Node> mTreeClassDef;
    private Tree<MTree.Node> mTreeProperties;
    private Tree<MTree.Node> mTreeMethods;
    private Tree<MTree.Node> mTreeFunctions;
    private Tree<MTree.Node> mTreeCellTitles;

    private List<MTree.Node> mtNodeClassDef;
    private List<MTree.Node> mtNodeProperties;
    private List<MTree.Node> mtNodeMethods;
    private List<MTree.Node> mtNodeFunctions;
    private List<MTree.Node> mtNodeCellTitles;

    private boolean isValidClassDef = false;
    private boolean isValidProperties = false;
    private boolean isValidMethods = false;
    private boolean isValidFunctions = false;
    private boolean isValidCellTitles = false;

    public MTreeWrapper(MTree mTree) {
        this.mTree = mTree;
        this.mTreeClassDef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        this.mTreeProperties = mTree.findAsTree(MTree.NodeType.PROPERTIES);
        this.mTreeMethods = mTree.findAsTree(MTree.NodeType.METHODS);
        this.mTreeFunctions = mTree.findAsTree(MTree.NodeType.FUNCTION);
        this.mTreeCellTitles = mTree.findAsTree(MTree.NodeType.CELL_TITLE);

        isValidClassDef = TreeUtilsV2.hasChildren(mTreeClassDef);
        isValidProperties = TreeUtilsV2.hasChildren(mTreeProperties);
        isValidMethods = TreeUtilsV2.hasChildren(mTreeMethods);
        isValidFunctions = TreeUtilsV2.hasChildren(mTreeFunctions);
        isValidCellTitles = TreeUtilsV2.hasChildren(mTreeCellTitles);

        if (isValidClassDef) this.mtNodeClassDef = TreeUtilsV2.treeToArrayList(mTreeClassDef);
        if (isValidProperties) this.mtNodeProperties = TreeUtilsV2.treeToArrayList(mTreeProperties);
        if (isValidMethods) this.mtNodeMethods = TreeUtilsV2.treeToArrayList(mTreeMethods);
        if (isValidFunctions) this.mtNodeFunctions = TreeUtilsV2.treeToArrayList(mTreeFunctions);
        if (isValidCellTitles) this.mtNodeCellTitles = TreeUtilsV2.treeToArrayList(mTreeCellTitles);
    }

    public List<MetaProperty> metaProperties() {
        if (!isValidProperties) return new ArrayList<>(0);
        List<MetaProperty> list = new ArrayList<>(INITCAPACITY);

        for (MTree.Node mtNodeProperty : mtNodeProperties) {
            MetaProperty p = new MetaProperty();
            MTreeNodeProperties mTreeNodeProperties = propertyAttributes(mtNodeProperty);
        }

        return list;
    }

    public List<MetaMethod> metaMethods() {
        if (!isValidMethods) return new ArrayList<>(0);
        List<MetaMethod> list = new ArrayList<>(INITCAPACITY);

        for (MTree.Node mtNodeProperty : mtNodeMethods) {
            MetaMethod m = new MetaMethod();
            MTreeNodeMethods mTreeNodeMethods = methodAttributes(mtNodeProperty);
        }

        return list;
    }

    private static MTreeNodeProperties propertyAttributes(MTree.Node node) {
        return new MTreeNodeProperties(node);
    }

    private static MTreeNodeMethods methodAttributes(MTree.Node node) {
        return new MTreeNodeMethods(node);
    }

    // since methods and properties work basically the same
    public static class MTreeNodeMethods extends MTreeNodeProperties {
        public MTreeNodeMethods(MTree.Node mtNode) {
            super(mtNode);
        }
    }

    static class MTreeNodeProperties {
        private MTree.Node mtNode = null;
        private List<MTree.Node> attributes = new ArrayList<>(10);
        private List<MTree.Node> properties = new ArrayList<>(10);

        MTreeNodeProperties(MTree.Node mtNode) {
            // since methods and properties work basically the same
            if (!EnumSet.of(PROPERTIES, METHODS).contains(mtNode.getType())) {
                return;
            }
            this.mtNode = mtNode;
            attributes = searchForAttributes();
        }

        boolean isValid() {
            return mtNode != null;
        }

        List<MTree.Node> searchForAttributes() {
            if (!isValid()) {
                return new ArrayList<>(0);
            }
            List<MTree.Node> attributes = TreeUtilsV2.findNode(mtNode.getSubtree(), ATTRIBUTES);
            List<MTree.Node> attrs = new ArrayList<>(10);
            for (MTree.Node n : attributes) {
                List<MTree.Node> attributeBlock = n.getSubtree();
                for (MTree.Node mtNode : attributeBlock) {
                    if (mtNode.getType() == ATTR) {
                        attrs.add(mtNode);
                    }
                }
            }

            return attrs;
        }
    }
}
