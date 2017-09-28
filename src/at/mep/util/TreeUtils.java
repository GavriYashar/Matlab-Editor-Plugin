package at.mep.util;

import at.mep.gui.fileStructure.Node;
import at.mep.meta.MetaClass;
import at.mep.meta.MetaMethod;
import at.mep.meta.MetaProperty;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.List;

/** Created by Gavri on 2017-04-28. */
public class TreeUtils {
    public static Node toFileStructureNodeClass(MTree mTree, String nameRoot) {
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
        Node classDefNode = new Node(metaClass, mTree.getNode(0));

        // creates Method ("function") Nodes for FileStructure
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.FUNCTION);
        java.util.List<MTree.Node> methodNodes = createNodesForClassDef(methodsTree, MTree.NodeType.FUNCTION);

        // creates Property Nodes for FileStructure
        // these are only properties with the new type definition e.g.:
        //    var string = "some string"
        Tree<MTree.Node> propertiesTree = mTree.findAsTree(MTree.NodeType.PROPTYPEDECL);
        java.util.List<MTree.Node> propertyNodes = createNodesForClassDef(propertiesTree, MTree.NodeType.PROPTYPEDECL);

        // population of ClassDefNode
        populateClassDefNodeWithProperties(classDefNode, metaClass, methodNodes, propertyNodes);
        populateClassDefNodeWithMethods(classDefNode, metaClass, methodNodes);

        return classDefNode;
    }

    public static Node toFileStructureNodeFunction(Tree<MTree.Node> nodeTree, String nameRoot) {
        return createNode(nodeTree, nameRoot);
    }

    public static Node toFileStructureNodeSection(Tree<MTree.Node> nodeTree, String nameRoot) {
        return createNode(nodeTree, nameRoot);
    }

    private static Node createNode(Tree<MTree.Node> nodeTree, String nameRoot) {
        Node node = new Node(nameRoot);
        if (nodeTree.getChildCount(nodeTree.getRoot()) < 1) {
            return null;
        }

        for (int i = 0; i < nodeTree.getChildCount(nodeTree.getRoot()); i++) {
            MTree.Node subNode = nodeTree.getChild(nodeTree.getRoot(), i);
            node.add(new Node(subNode));
        }
        return node;
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
     * @param classDefNode
     * @param metaClass
     * @param propertyNodes
     */
    private static void populateClassDefNodeWithProperties(Node classDefNode, MetaClass metaClass, java.util.List<MTree.Node> methodNodes, java.util.List<MTree.Node> propertyNodes) {
        int counter = 0; // max: methodNodes.size()
        for (int i = 0; i < propertyNodes.size(); i++) {
            String nodeString = NodeUtils.getPropertyDecl(propertyNodes.get(i));
            for (MetaProperty p : metaClass.getProperties()) {
                Node property = null;
                if (nodeString != null && nodeString.equals(p.getName())) {
                    property = new Node(p, propertyNodes.get(i));
                }
                if (property == null) continue;

                if (p.isHasGetter()) {
                    Node node = findPropertySetGetMethod(methodNodes, p, "get.");
                    classDefNode.add(node);
                    property.add(node);
                }
                if (p.isHasSetter()) {
                    Node node = findPropertySetGetMethod(methodNodes, p, "set.");
                    classDefNode.add(node);
                    property.add(node);
                }
                classDefNode.add(property);

                counter++;
                if (counter == propertyNodes.size()) break;
            }
        }
    }

    private static Node findPropertySetGetMethod(List<MTree.Node> methodNodes, MetaProperty p, String strGetSet) {
        if ( !(strGetSet.equals("set.") || strGetSet.equals("get.")) ) {
            throw new IllegalArgumentException("namePropertyMethod has to be equal to 'set.' or 'get.'");
        }
        for (MTree.Node mtNode : methodNodes) {
            Node method = null;

            String namePropSetGet = strGetSet + p.getName();
            if (namePropSetGet.equals(mtNode.getFunctionName().getText())) {
                method = new Node(p, mtNode);
            }
            if (method == null) continue;
            return method;
        }
        return null;
    }

    /**
     * Helper method to create nodes to populate FileStructure
     * @param classDefNode
     * @param metaClass
     * @param methodNodes
     */
    private static void populateClassDefNodeWithMethods(Node classDefNode, MetaClass metaClass, java.util.List<MTree.Node> methodNodes) {
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

                Node method = null;
                if (nodeString != null && nodeString.equals(m.getName())) {
                    method = new Node(m, methodNodes.get(i));
                }
                if (method == null) continue;
                classDefNode.add(method);
                counter++;
                if (counter == methodNodes.size()) break;
            }
        }
    }
}
