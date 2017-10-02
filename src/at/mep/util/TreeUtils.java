package at.mep.util;

import at.mep.gui.fileStructure.NodeFS;
import at.mep.meta.MetaClass;
import at.mep.meta.MetaMethod;
import at.mep.meta.MetaProperty;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.List;

/** Created by Gavri on 2017-04-28. */
public class TreeUtils {
    public static NodeFS toFileStructureNodeClass(MTree mTree, String nameRoot) {
        Tree<MTree.Node> treeClassdef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        if (treeClassdef.getChildCount(treeClassdef.getRoot()) < 1) {
            return null;
        }

        // creates MetaObjectWrapper for Matlabs "meta.class"
        MetaClass metaClass;
        try {
            metaClass = MetaClass.getMatlabClass(nameRoot);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // create Class (root) node for FileStructure
        NodeFS classDefNodeFS = new NodeFS(metaClass, mTree.getNode(0));

        // creates Method ("function") Nodes for FileStructure
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.FUNCTION);
        java.util.List<MTree.Node> methodNodes = createNodesForClassDef(methodsTree, MTree.NodeType.FUNCTION);

        // creates Property Nodes for FileStructure
        // these are only properties with the new type definition e.g.:
        //    var string = "some string"
        Tree<MTree.Node> propertiesDeclTree = mTree.findAsTree(MTree.NodeType.PROPTYPEDECL);
        java.util.List<MTree.Node> propertyDeclNodes = createNodesForClassDef(propertiesDeclTree, MTree.NodeType.PROPTYPEDECL);

        // creates Property Nodes for FileStructure
        // these are only properties with no or old type definition e.g.:
        // var
        // var = 1
        // var@double = 1
        Tree<MTree.Node> propertiesTree = mTree.findAsTree(MTree.NodeType.PROPERTIES);
        java.util.List<MTree.Node> propertyNodes = createNodesForClassDefPropNoOldDef(propertiesTree);

        // population of ClassDefNode
        populateClassDefNodeWithProperties(classDefNodeFS, metaClass, methodNodes, propertyNodes);
        populateClassDefNodeWithMethods(classDefNodeFS, metaClass, methodNodes);

        return classDefNodeFS;
    }

    public static NodeFS toFileStructureNodeFunction(Tree<MTree.Node> nodeTree, String nameRoot) {
        return createNode(nodeTree, nameRoot);
    }

    public static NodeFS toFileStructureNodeSection(Tree<MTree.Node> nodeTree, String nameRoot) {
        return createNode(nodeTree, nameRoot);
    }

    private static NodeFS createNode(Tree<MTree.Node> nodeTree, String nameRoot) {
        NodeFS nodeFS = new NodeFS(nameRoot);
        if (nodeTree.getChildCount(nodeTree.getRoot()) < 1) {
            return null;
        }

        for (int i = 0; i < nodeTree.getChildCount(nodeTree.getRoot()); i++) {
            MTree.Node subNode = nodeTree.getChild(nodeTree.getRoot(), i);
            nodeFS.add(new NodeFS(subNode));
        }
        return nodeFS;
    }

    /**
     * creates properties nodes for properties w/o type def or old type def e.g.:
     * var
     * var = 1
     * var@double
     * var@double = 1
     * @param tree
     * @return
     */
    private static java.util.List<MTree.Node> createNodesForClassDefPropNoOldDef(Tree<MTree.Node> tree) {
        java.util.List<MTree.Node> nodes = new ArrayList<>(10);
        for (int i = 0; i < tree.getChildCount(tree.getRoot()); i++) {
            MTree.Node mtNode = tree.getChild(tree.getRoot(), i);
            java.util.List<MTree.Node> propDefMTree = mtNode.getSubtree();
        }
        return nodes;
    }

    private static java.util.List<MTree.Node> createNodesForClassDef(Tree<MTree.Node> tree, MTree.NodeType nodeType) {
        java.util.List<MTree.Node> nodes = new ArrayList<>(10);
        for (int i = 0; i < tree.getChildCount(tree.getRoot()); i++) {
            MTree.Node mtNode = tree.getChild(tree.getRoot(), i);
            java.util.List<MTree.Node> mtSubList = mtNode.getSubtree();
            for (MTree.Node mtNodeSub : mtSubList) {
                if (mtNodeSub.getType() == nodeType) {
                    nodes.add(mtNodeSub);
                }
            }
        }
        return nodes;
    }

    /**
     * Helper method to create nodes to populate FileStructure
     * @param classDefNodeFS
     * @param metaClass
     * @param propertyNodes
     */
    private static void populateClassDefNodeWithProperties(NodeFS classDefNodeFS, MetaClass metaClass, java.util.List<MTree.Node> methodNodes, java.util.List<MTree.Node> propertyNodes) {
        int counter = 0; // max: methodNodes.size()
        for (int i = 0; i < propertyNodes.size(); i++) {
            String nodeString = NodeUtils.stringForPrptyDeclName(propertyNodes.get(i));
            for (MetaProperty p : metaClass.getProperties()) {
                NodeFS property = null;
                if (nodeString != null && nodeString.equals(p.getName())) {
                    property = new NodeFS(p, propertyNodes.get(i));
                }
                if (property == null) continue;

                if (p.isHasGetter()) {
                    NodeFS nodeFS = findPropertySetGetMethod(methodNodes, p, "get.");
                    classDefNodeFS.add(nodeFS);
                    property.add(nodeFS);
                }
                if (p.isHasSetter()) {
                    NodeFS nodeFS = findPropertySetGetMethod(methodNodes, p, "set.");
                    classDefNodeFS.add(nodeFS);
                    property.add(nodeFS);
                }
                classDefNodeFS.add(property);

                counter++;
                if (counter == propertyNodes.size()) break;
            }
        }
    }

    private static NodeFS findPropertySetGetMethod(List<MTree.Node> methodNodes, MetaProperty p, String strGetSet) {
        if ( !(strGetSet.equals("set.") || strGetSet.equals("get.")) ) {
            throw new IllegalArgumentException("namePropertyMethod has to be equal to 'set.' or 'get.'");
        }
        for (MTree.Node mtNode : methodNodes) {
            NodeFS method = null;

            String namePropSetGet = strGetSet + p.getName();
            if (namePropSetGet.equals(mtNode.getFunctionName().getText())) {
                method = new NodeFS(p, mtNode);
            }
            if (method == null) continue;
            return method;
        }
        return null;
    }

    /**
     * Helper method to create nodes to populate FileStructure
     * @param classDefNodeFS
     * @param metaClass
     * @param methodNodes
     */
    private static void populateClassDefNodeWithMethods(NodeFS classDefNodeFS, MetaClass metaClass, java.util.List<MTree.Node> methodNodes) {
        int counter = 0; // max: methodNodes.size()
        for (int i = 0; i < methodNodes.size(); i++) {
            String nodeString = NodeUtils.getFunctionHeader(methodNodes.get(i), false);
            for (MetaMethod m : metaClass.getMethods()) {
                // inherited, see FileStructure inherited checkbox
                // if (!inherited.isSelected()) {
                //     if (!m.getDefiningClass().equals(fqn)) {
                //         continue;
                //     }
                // }

                NodeFS method = null;
                if (nodeString != null && nodeString.equals(m.getName())) {
                    method = new NodeFS(m, methodNodes.get(i));
                }
                if (method == null) continue;
                classDefNodeFS.add(method);
                counter++;
                if (counter == methodNodes.size()) break;
            }
        }
    }
}
