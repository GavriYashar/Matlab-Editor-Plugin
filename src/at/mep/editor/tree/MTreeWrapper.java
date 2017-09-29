package at.mep.editor.tree;

import at.mep.meta.EMetaAccess;
import at.mep.meta.Meta;
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
            MTreeNodeProperties mTreeNodeProperties = new MTreeNodeProperties(mtNodeProperty);
            list.addAll(mTreeNodeProperties.metaProperties);
        }
        return list;
    }

    public List<MetaMethod> metaMethods() {
        if (!isValidMethods) return new ArrayList<>(0);
        List<MetaMethod> list = new ArrayList<>(INITCAPACITY);

        for (MTree.Node mtNodeMethod : mtNodeMethods) {
            MTreeNodeMethods mTreeNodeMethods = new MTreeNodeMethods(mtNodeMethod);
            // list.addAll(mTreeNodeMethods.metaMethods);
        }
        return list;
    }

    public static abstract class MTreeNodePropertyMethod {
        MTree.Node mtNode = null;
        List<MTree.Node> attributes = new ArrayList<>(10);
        List<TreeUtilsV2.AttributeHolder> attributeHolders = new ArrayList<>(10);

        public MTreeNodePropertyMethod(MTree.Node mtNode) {
            this.mtNode = mtNode;
            if (!EnumSet.of(PROPERTIES, METHODS).contains(mtNode.getType())) {
                return;
            }
            if (!isValid()) {
                return;
            }
            attributes = TreeUtilsV2.searchForAttributes(mtNode);
            attributeHolders = TreeUtilsV2.convertAttributes(attributes);
        }

        boolean isValid() {
            return mtNode != null;
        }

        public List<MTree.Node> getAttributes() {
            return attributes;
        }
    }

    // since methods and properties work basically the same
    public static class MTreeNodeMethods extends MTreeNodePropertyMethod {
        private List<MTree.Node> functions = new ArrayList<>(10);
        private List<MetaMethod> metaMethods = new ArrayList<>(10);

        public MTreeNodeMethods(MTree.Node mtNode) {
            super(mtNode);
            if (!isValid()) {
                return;
            }
            // functions = TreeUtilsV2.searchForFunctions(mtNode);
        }
    }

    static class MTreeNodeProperties extends MTreeNodePropertyMethod {
        private List<MTree.Node> properties = new ArrayList<>(10);
        private List<MetaProperty> metaProperties = new ArrayList<>(10);
        List<TreeUtilsV2.PropertyHolder> propertyHolders = new ArrayList<>(10);

        MTreeNodeProperties(MTree.Node mtNode) {
            super(mtNode);
            if (!isValid()) {
                return;
            }
            properties = TreeUtilsV2.searchForProperties(mtNode);
            propertyHolders = TreeUtilsV2.convertProperties(properties);
            createMetaProperties();
        }

        private void createMetaProperties() {
            for (MTree.Node prop : properties) {
                MetaProperty meta = new MetaProperty();
                populateProperty(meta, prop);
                metaProperties.add(meta);
            }
        }

        private void populateProperty(MetaProperty meta, MTree.Node prop) {
            for (TreeUtilsV2.AttributeHolder attributeHolder : attributeHolders) {
                meta.populate(attributeHolder);
            }

        }

        public List<MTree.Node> getProperties() {
            return properties;
        }
    }
}
