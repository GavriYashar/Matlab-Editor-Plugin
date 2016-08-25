package at.justin.matlab.gui.components;

import at.justin.matlab.gui.fileStructure.Node;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016-08-02.
 */
public class JTreeFilter extends JTree {
    private String filterText;
    private Pattern pattern;
    private boolean useRegex;
    private Node originalRoot;

    public JTreeFilter() {
    }

    public void setOriginalRoot(Node originalRoot) {
        this.originalRoot = originalRoot;
    }

    public Node filter(String filterText) {
        this.filterText = filterText;
        this.pattern = null;
        useRegex = false;
        return filter();
    }

    public Node filter(Pattern pattern) {
        this.filterText = "";
        this.pattern = pattern;
        useRegex = true;
        return filter();
    }

    private Node filter() {
        Node copiedNode = deepCopyNode(originalRoot);
        Enumeration<Node> e = copiedNode.depthFirstEnumeration();
        List<Node> list = Collections.list(e);
        for (int i = 0; i < list.size(); i++) {
            Node n = list.get(i);
            String str = n.nodeText();
            if (str.endsWith(".m")
                    || n.getType().equals(MTree.NodeType.CLASSDEF)
                    || n.getType().equals(MTree.NodeType.METHODS)) {
                continue;
            }
            if (!useRegex && !str.toLowerCase().contains(filterText.toLowerCase())) {
                n.removeFromParent();
            } else if (useRegex && !pattern.matcher(str).find()) {
                n.removeFromParent();
            }
        }
        return copiedNode;
    }

    private Node deepCopyNode(Node node) {
        Node copiedNode;
        if (node.hasNode()) {
            copiedNode = new Node(node.node());
        } else {
            copiedNode = new Node(node.nodeText());
        }

        // System.out.println(" ");
        // for (int i= 0; i < node.getLevel(); i++) {
        //     System.out.print("\t");
        // }
        int c = node.getChildCount();
        // System.out.print("child count: " + c);
        for (int i = 0; i < c; i++) {
            // System.out.print("\t copying Node: " + node.nodeText());
            copiedNode.add(deepCopyNode((Node) node.getChildAt(i)));
        }
        return copiedNode;
    }

}
