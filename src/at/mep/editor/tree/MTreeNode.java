package at.mep.editor.tree;

import at.mep.meta.MetaProperty;
import at.mep.util.StringUtils;
import at.mep.util.TreeUtilsV2;
import com.mathworks.widgets.text.mcode.MTree;
import com.mathworks.widgets.text.mcode.MTree.Node;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

/**
 * this is a helper class, for debugging purposes, so MTree is easier to understand
 */
public class MTreeNode {
    // mTree = at.mep.editor.EditorWrapper.getMTree();
    // mtn = at.mep.editor.tree.MTreeNode.construct(mTree);
    // mtn.attributeString
    //
    // parent: getParent() - above level
    // attributes: getLeft() - same level for current node
    // children: getRight() - lower Level
    // neighbour: getNext() - same Level
    private MTree mTree;

    private MTree.Node mtNode;
    private MTreeNode parent;
    private List<MTreeNode> attributes = new ArrayList<MTreeNode>(10);
    private List<MTreeNode> children = new ArrayList<MTreeNode>(10);

    private MTreeNode(MTree.Node mtNode) {
        this.mtNode = mtNode;
    }

    public void populate(MetaProperty metaProperty, MTreeNode node) {
        List<MTree.Node> propBlock = node.getMtNode().getSubtree();
    }


    private static StringBuilder buildStringAttributes(StringBuilder stringBuilder, MTreeNode node) {
        /* short summery how the code below works
         *  1. has attribute?
         *       true: go deeper, go to 1.
         *       false: get nodeText (ID[name], LT[<], AND[&], ATBASE[@])
         *  2. has Children?
         *       true: go deeper, go to 1.
         *       false: return;
         *
         *       name: can be the name of the class, property, or method
         *          <: class is a subclass
         *          &: class has more subclasses
         *          @: property undocumented and legacy type definition
         */

        if (node.hasAttributes()) {
            for (int i = 0; i < node.attributes.size(); i++) {
                buildStringAttributes(stringBuilder, node.attributes.get(i));
            }
        }

        stringBuilder.append(node.getText());
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != ' ')
            stringBuilder.append(" ");

        if (EnumSet.of(LT, AND, ATBASE).contains(node.getType())) {
            stringBuilder.append(TreeUtilsV2.stringForMTreeNodeType(node.getType()) + " ");
        }

        if (node.hasChildren()) {
            if (node.children.size() > 1) {
                return stringBuilder;
            }
            buildStringAttributes(stringBuilder, node.children.get(0));
        }
        return stringBuilder;
    }

    private static StringBuilder buildStringNewLevel(StringBuilder stringBuilder, MTreeNode node, int lvl) {
        if (node.getType() == MTree.NodeType.JAVA_NULL_NODE) return stringBuilder;
        stringBuilder.append("\n");
        String b = StringUtils.blanks(lvl * 2);

        stringBuilder.append(lvl + " " + b + " + " + node.getType().toString());

        for (MTreeNode n : node.children) {
            buildStringNewLevel(stringBuilder, n, lvl + 1);
        }
        return stringBuilder;
    }

    public static MTreeNode construct(MTree mTree) {
        if (mTree.size() < 1) return null;
        return MTreeNode.construct(mTree.getNode(0), false);
    }

    /**
     * construct an easier to understand tree than MTree.
     * @param mtNode
     * @param ignoreChildrenOfThisNode ignores children (getRight) of this level, attributes (getLeft) are constructed with children
     *
     * @return
     */
    public static MTreeNode construct(MTree.Node mtNode, boolean ignoreChildrenOfThisNode) {
        MTreeNode node = new MTreeNode(mtNode);
        if (mtNode.getType() == MTree.NodeType.JAVA_NULL_NODE) {
            return node;
        }
        node.parent = new MTreeNode(mtNode.getParent());

        // Attributes
        MTree.Node attribute = mtNode.getLeft();
        while (attribute.getType() != MTree.NodeType.JAVA_NULL_NODE) {
            node.attributes.add(MTreeNode.construct(attribute, false));
            attribute = attribute.getNext(); // TODO: getListOfNextNodes
        }

        // Children
        if (!ignoreChildrenOfThisNode) {
            MTree.Node child = mtNode.getRight();
            while (child.getType() != MTree.NodeType.JAVA_NULL_NODE) {
                node.children.add(MTreeNode.construct(child, false));
                child = child.getNext(); // TODO: getListOfNextNodes
            }
        }
        return node;
    }

    public String toString() {
        return "" + mtNode;
    }

    public void printTree() {
        System.out.println("Displaying Tree:");
        StringBuilder stringBuilder = buildStringNewLevel(new StringBuilder(), this, 0);
        System.out.println(stringBuilder);
    }

    public String attributeString() {
        String string = buildStringAttributes(new StringBuilder(""), this).toString();
        return StringUtils.trimEnd(string);
    }

    private boolean hasAttributes() {
        return attributes.size() > 0;
    }

    private boolean hasChildren() {
        return children.size() > 0;
    }

    public Node getMtNode() {
        return mtNode;
    }

    public MTree.NodeType getType() {
        return getMtNode().getType();
    }

    public String getText() {
        return getMtNode().getText();
    }

    public MTreeNode getParent() {
        return parent;
    }

    public List<MTreeNode> getAttributes() {
        return attributes;
    }

    public List<MTreeNode> getChildren() {
        return children;
    }
}
