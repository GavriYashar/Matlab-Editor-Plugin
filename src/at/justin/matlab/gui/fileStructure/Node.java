package at.justin.matlab.gui.fileStructure;

import at.justin.matlab.util.NodeUtils;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by z0032f1t on 02.08.2016.
 * <p>
 * Represents a Node in JTree and also an Matlab MTree.Node class.
 */
public class Node extends DefaultMutableTreeNode {
    private static final Pattern staticFalse = Pattern.compile("(Static|Constant)\\s*=\\s*false");
    private static final Pattern accessPrivate = Pattern.compile("[Aa]ccess\\s*=\\s*private");
    private static final Pattern setAccessPrivate = Pattern.compile("[Ss]etAccess\\s*=\\s*private");
    private static final Pattern getAccessPrivate = Pattern.compile("[Gg]etAccess\\s*=\\s*private");
    private MTree.Node node; // might not always be set, e.g.: First node is just the string of the filename
    private String nodeText = "DEFAULT NODE TEXT";
    private String documentationText = "DEFAULT DOCUMENTATION";
    private String parentNodeLineText = "";

    public Node(MTree.Node node) {
        super(node);
        this.node = node;
        this.nodeText = NodeUtils.getTextFormattedForNode(node);
        this.documentationText = nodeDocumentation();
        if (node.getParent() != null) {
            if (node.getType() == MTree.NodeType.FUNCTION) {
                this.parentNodeLineText = NodeUtils.getTextForNode(node.getParent()).trim();
            } else if (node.getType() == MTree.NodeType.ID) {
                this.parentNodeLineText = NodeUtils.getTextForNode(node.getParent().getParent()).trim();
            }
        }
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

    public String getDocumentationText() {
        return documentationText;
    }

    public MTree.NodeType getType() {
        if (hasNode()) {
            return node.getType();
        } else {
            return MTree.NodeType.JAVA_NULL_NODE;
        }
    }

    public boolean isProperty() {
        MTree.Node node = node().getParent();
        if (node == null) return false;
        return node.getType() != MTree.NodeType.PROPERTIES;
    }

    public boolean isStatic() {
        MTree.Node node = node().getParent();
        boolean bool = false;
        if (node == null
                || !(node.getType() == MTree.NodeType.METHODS || node.getType() == MTree.NodeType.PROPERTIES)) {
            return false;
        }
        if (parentNodeLineText.contains("Static")
                || parentNodeLineText.contains("Constant")) {
            bool = !staticFalse.matcher(parentNodeLineText).find();
        }
        return bool;
    }

    public boolean isAccessPrivate() {
        MTree.Node node = node().getParent();
        if (node == null || node.getType() != MTree.NodeType.METHODS) {
            return false;
        }
        return accessPrivate.matcher(parentNodeLineText).find();
    }

    public boolean isGetAccessPrivate() {
        MTree.Node node = node().getParent();
        if (node == null || node.getType() != MTree.NodeType.METHODS) {
            return false;
        }
        return getAccessPrivate.matcher(parentNodeLineText).find();
    }

    public boolean isSetAccessPrivate() {
        MTree.Node node = node().getParent();
        if (node == null || node.getType() != MTree.NodeType.METHODS) {
            return false;
        }
        return setAccessPrivate.matcher(parentNodeLineText).find();
    }


    private String nodeDocumentation() {
        if (!(getType() == MTree.NodeType.FUNCTION || getType() == MTree.NodeType.CLASSDEF)) {
            return "";
        }
        List<MTree.Node> nodeList = NodeUtils.getDocumentationNodesForNode(node);
        String s = "";
        for (MTree.Node node : nodeList) {
            s += NodeUtils.getTextForNode(node).trim() + "\n";
        }
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
