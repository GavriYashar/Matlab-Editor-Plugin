package at.justin.matlab.gui.fileStructure;

import at.justin.matlab.util.NodeUtils;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * Created by z0032f1t on 02.08.2016.
 *
 * Represents a Node in JTree and also an Matlab MTree.Node class.
 */
public class Node extends DefaultMutableTreeNode {
    private MTree.Node node; // might not always be set, e.g.: First node is just the string of the filename
    private String nodeText = "DEFAULT NODE TEXT";

    public Node(MTree.Node node) {
        super(node);
        this.node = node;
        this.nodeText = NodeUtils.getTextFormattedForNode(node);
    }

    public Node(String nodeText) {
        this.nodeText = nodeText;
    }

    public MTree.Node node() {
        return node;
    }

    public boolean hasNode() {
        return node != null;
    }

    public String nodeText() {
        return nodeText;
    }

    public String nodeDocumentation() {
        if (!(getType() == MTree.NodeType.FUNCTION || getType() == MTree.NodeType.CLASSDEF)) {
            return "";
        }
        List<MTree.Node> nodeList = NodeUtils.getDocumentationNodesForNode(node);
        String s = "";
        for (MTree.Node node : nodeList) {
            s += NodeUtils.getTextForNode(node) + "\n";
        }
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public MTree.NodeType getType() {
        if (hasNode()) {
            return node.getType();
        } else {
            return MTree.NodeType.JAVA_NULL_NODE;
        }
    }

}
