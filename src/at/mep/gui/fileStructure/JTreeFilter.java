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
    private NodeFS originalRoot;

    JTreeFilter() {
    }

    void setOriginalRoot(NodeFS originalRoot) {
        this.originalRoot = originalRoot;
    }

    NodeFS filter(String filterText) {
        this.filterText = filterText;
        this.pattern = null;
        useRegex = false;
        return filter();
    }

    NodeFS filter(Pattern pattern) {
        this.filterText = "";
        this.pattern = pattern;
        useRegex = true;
        return filter();
    }

    private NodeFS filter() {
        NodeFS filteredNodeFS = deepCopyNode(originalRoot);
        Enumeration<NodeFS> e = filteredNodeFS.depthFirstEnumeration();
        List<NodeFS> list = Collections.list(e);
        for (int i = list.size() - 1; i >= 0; i--) {
            NodeFS n = list.get(i);
            String str = n.nodeText();
            if (str.endsWith(".m")
                    || n.getType().equals(MTree.NodeType.CLASSDEF)
                    || n.getType().equals(MTree.NodeType.METHODS)
                    || n.getEMetaNodeType() == EMetaNodeType.META_CLASS) {
                continue;
            }
            if (!useRegex && !str.toLowerCase().contains(filterText.toLowerCase())) {
                n.removeFromParent();
            } else if (useRegex && !pattern.matcher(str).find()) {
                n.removeFromParent();
            }
        }
        return filteredNodeFS;
    }

    private NodeFS deepCopyNode(NodeFS nodeFS) {
        NodeFS copiedNodeFS;
        if (nodeFS.getEMetaNodeType() == EMetaNodeType.MATLAB && nodeFS.hasNode()) {
            copiedNodeFS = new NodeFS(nodeFS.node());
        } else if (nodeFS.getEMetaNodeType() == EMetaNodeType.STRING) {
            copiedNodeFS = new NodeFS(nodeFS.nodeText());
        } else if (nodeFS.getEMetaNodeType() == EMetaNodeType.META_CLASS) {
            copiedNodeFS = new NodeFS((MetaClass) nodeFS.getMeta(), nodeFS.node());
        } else if (nodeFS.getEMetaNodeType() == EMetaNodeType.META_PROPERTY) {
            copiedNodeFS = new NodeFS((MetaProperty) nodeFS.getMeta(), nodeFS.node());
        } else if (nodeFS.getEMetaNodeType() == EMetaNodeType.META_METHOD) {
            copiedNodeFS = new NodeFS((MetaMethod) nodeFS.getMeta(), nodeFS.node());
        } else {
            throw new IllegalArgumentException("unknown NodeFS");
        }

        // System.out.println(" ");
        // for (int i= 0; i < nodeFS.getLevel(); i++) {
        //     System.out.print("\t");
        // }
        int c = nodeFS.getChildCount();
        // System.out.print("child count: " + c);
        for (int i = 0; i < c; i++) {
            // System.out.print("\t copying NodeFS: " + nodeFS.nodeText());
            copiedNodeFS.add(deepCopyNode((NodeFS) nodeFS.getChildAt(i)));
        }
        return copiedNodeFS;
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
