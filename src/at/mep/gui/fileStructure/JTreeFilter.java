package at.mep.gui.fileStructure;

import at.mep.meta.MetaClass;
import at.mep.meta.MetaMethod;
import at.mep.meta.MetaProperty;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-08-02. */
class JTreeFilter extends JTree {
    private String filterText;
    private Pattern pattern;
    private boolean useRegex;
    private Node originalRoot;

    JTreeFilter() {
    }

    void setOriginalRoot(Node originalRoot) {
        this.originalRoot = originalRoot;
    }

    Node filter(String filterText) {
        this.filterText = filterText;
        this.pattern = null;
        useRegex = false;
        return filter();
    }

    Node filter(Pattern pattern) {
        this.filterText = "";
        this.pattern = pattern;
        useRegex = true;
        return filter();
    }

    private Node filter() {
        Node filteredNode = deepCopyNode(originalRoot);
        Enumeration<Node> e = filteredNode.depthFirstEnumeration();
        List<Node> list = Collections.list(e);
        for (int i = list.size() - 1; i >= 0; i--) {
            Node n = list.get(i);
            String str = n.nodeText();
            if (str.endsWith(".m")
                    || n.getType().equals(MTree.NodeType.CLASSDEF)
                    || n.getType().equals(MTree.NodeType.METHODS)
                    || n.geteMetaNodeType() == EMetaNodeType.META_CLASS) {
                continue;
            }
            if (!useRegex && !str.toLowerCase().contains(filterText.toLowerCase())) {
                n.removeFromParent();
            } else if (useRegex && !pattern.matcher(str).find()) {
                n.removeFromParent();
            }
        }
        return filteredNode;
    }

    private Node deepCopyNode(Node node) {
        Node copiedNode;
        if (node.geteMetaNodeType() == EMetaNodeType.MATLAB && node.hasNode()) {
            copiedNode = new Node(node.node());
        } else if (node.geteMetaNodeType() == EMetaNodeType.STRING) {
            copiedNode = new Node(node.nodeText());
        } else if (node.geteMetaNodeType() == EMetaNodeType.META_CLASS) {
            copiedNode = new Node((MetaClass) node.getMeta(), node.node());
        } else if (node.geteMetaNodeType() == EMetaNodeType.META_PROPERTY) {
            copiedNode = new Node((MetaProperty) node.getMeta(), node.node());
        } else if (node.geteMetaNodeType() == EMetaNodeType.META_METHOD) {
            copiedNode = new Node((MetaMethod) node.getMeta(), node.node());
        } else {
            throw new IllegalArgumentException("unknown Node");
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

    private List<List<MTree.Node>> deepCopyDocu(List<List<MTree.Node>> docu) {
        List<List<MTree.Node>> copiedDocu = new ArrayList<>(docu.size());
        for (List<MTree.Node> comments : docu) {
            List<MTree.Node> copiedComments = new ArrayList<>(comments.size());
            for (MTree.Node node : comments) {
                copiedComments.add(node);
            }
            copiedDocu.add(copiedComments);
        }
        return copiedDocu;
    }
}
