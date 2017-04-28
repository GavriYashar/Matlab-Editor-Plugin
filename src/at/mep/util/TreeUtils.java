package at.mep.util;

import at.mep.gui.fileStructure.Node;
import at.mep.meta.MetaClass;
import at.mep.meta.MetaMethod;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;

/** Created by Gavri on 2017-04-28. */
public class TreeUtils {

    public static Node toFileStructureNodeClass(MTree mTree, String nameRoot) {
        Tree<MTree.Node> treeClassdef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        if (treeClassdef.getChildCount(treeClassdef.getRoot()) < 1) {
            return null;
        }

        MetaClass metaClass;
        try {
            metaClass = MetaClass.getMatlabClass(nameRoot);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);
        java.util.List<MTree.Node> methodNodes = new ArrayList<>(10);
        for (int i = 0; i < methodsTree.getChildCount(methodsTree.getRoot()); i++) {
            MTree.Node method = methodsTree.getChild(methodsTree.getRoot(), i);
            Node methodNode = new Node(method);

            java.util.List<MTree.Node> methodsSub = method.getSubtree();
            for (MTree.Node methodSub : methodsSub) {
                if (methodSub.getType() == MTree.NodeType.FUNCTION) {
                    methodNodes.add(methodSub);
                }
            }
        }
        Node classDefNode = new Node(metaClass, mTree.getNode(0));

        int counter = 0;
        for (int i = 0; i < methodNodes.size(); i++) {
            String nodeString = NodeUtils.getFunctionHeader(methodNodes.get(i), false);
            for (MetaMethod m : metaClass.getMethods()) {
                // inherited, see FileStructure inherited checkbox
//                if (!inherited.isSelected()) {
//                    if (!m.getDefiningClass().equals(fqn)) {
//                        continue;
//                    }
//                }

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
}
