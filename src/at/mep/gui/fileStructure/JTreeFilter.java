package at.mep.gui.fileStructure;

import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import java.util.Collections;
import java.util.EnumSet;
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
            if (str.endsWith(".m") || EnumSet.of(MTree.NodeType.CLASSDEF, MTree.NodeType.METHODS).contains(n.getType())) {
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
        NodeFS copiedNodeFS = new NodeFS(nodeFS);
        int c = nodeFS.getChildCount();
        for (int i = 0; i < c; i++) {
            copiedNodeFS.add(deepCopyNode((NodeFS) nodeFS.getChildAt(i)));
        }
        return copiedNodeFS;
    }
}
