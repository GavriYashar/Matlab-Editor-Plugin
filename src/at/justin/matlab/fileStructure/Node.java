package at.justin.matlab.fileStructure;

import at.justin.matlab.util.NodeUtils;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by z0032f1t on 02.08.2016.
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

    public MTree.NodeType getType() {
        if (hasNode()) {
            return node.getType();
        } else {
            return MTree.NodeType.JAVA_NULL_NODE;
        }
    }

}
