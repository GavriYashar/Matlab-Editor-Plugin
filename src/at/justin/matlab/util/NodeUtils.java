package at.justin.matlab.util;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */

import at.justin.matlab.EditorWrapper;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.Iterator;
import java.util.List;

/**
 * Get node Strings
 * a Node may look like the following
 * lvl   node                                     description
 * 1     METHODS [12, 5] to [15, 7]               this is the whole method body
 * 2      ATTRIBUTES [12, 13] to [12, 36],        method attributes like static, private etc.
 * 3       ATTR [12, 14] to [0, 0],               introducing an attribute
 * 4        ID (Static) [12, 14] to [0, 0],       attribute
 * 3       ATTR [12, 29] to [0, 0],               introducing an attribute
 * 4        ID (Hidden) [12, 22] to [0, 0],       attribute
 * 4        ID (false) [12, 31] to [0, 0],        attribute flag
 * 3       FUNCTION [13, 9] to [14, 11],          introducing a function
 * 4        ETC [13, 24] to [0, 0]
 * 5         ID (a) [13, 19] to [0, 0]            out arg1
 * 5         ID (b) [13, 21] to [0, 0]            out arg2
 * 5         ETC [13, 30] to [13, 43],
 * 6          ID (asdf) [13, 26] to [0, 0],       function name
 * 6          ID (i) [13, 31] to [0, 0],          in arg1
 * 6          ID (want) [13, 34] to [0, 0],       in arg2
 * 6          ID (sth) [13, 40] to [0, 0]         in arg3
 */
public final class NodeUtils {
    private NodeUtils() {
    }

    private static String concatenateArgs(Iterator iterator) {
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            MTree.Node varID = (MTree.Node) iterator.next();
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }

            if (varID.getType().equals(MTree.NodeType.NOT)) {
                stringBuilder.append("~");
            } else {
                stringBuilder.append(varID.getText());
            }
        }
        return stringBuilder.toString();
    }

    public static String getPropertiesHeader(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.PROPERTIES) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.PROPERTIES");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("properties ");

        List<MTree.Node> propertyNodes = node.getSubtree();
        // find ATTRIBUTES node
        for (MTree.Node propertyNextNode : propertyNodes) {
            if (propertyNextNode.getType() == MTree.NodeType.ATTRIBUTES) {
                stringBuilder.append(NodeUtils.getTextForNode(propertyNextNode));
            }
        }
        return stringBuilder.toString();
    }

    public static String getMethodHeader(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.METHODS) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.METHODS");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("methods ");

        List<MTree.Node> methodNodes = node.getSubtree();
        // find ATTRIBUTES node
        for (MTree.Node methodNextNode : methodNodes) {
            if (methodNextNode.getType() == MTree.NodeType.ATTRIBUTES) {
                stringBuilder.append(NodeUtils.getTextForNode(methodNextNode));
            }
        }
        return stringBuilder.toString();
    }

    public static String getClassdef(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.CLASSDEF) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.CLASSDEF");
        }
        List<MTree.Node> classNodes = node.getSubtree();
        // find ID node in CEXPR node
        for (MTree.Node nextClassNode : classNodes) {
            if (nextClassNode.getType() == MTree.NodeType.CEXPR) {
                List<MTree.Node> cexprNodes = nextClassNode.getSubtree();
                for (MTree.Node nextCEXPRNode : cexprNodes) {
                    if (nextCEXPRNode.getType() == MTree.NodeType.ID) {
                        return "classdef " + nextCEXPRNode.getText();
                    }
                }
            }
        }
        return "no class constructor found";
    }

    public static String getFunctionInArgs(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.FUNCTION) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.FUNCTION");
        }
        Iterator inputIter = node.getInputArguments().iterator();
        return concatenateArgs(inputIter);
    }

    public static String getFunctionOutArgs(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.FUNCTION) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.FUNCTION");
        }
        Iterator outIter = node.getOutputArguments().iterator();
        return concatenateArgs(outIter);
    }

    public static String getFunctionHeader(final MTree.Node node, boolean withSignature) {
        String functionName = node.getFunctionName().getText();
        if (functionName != null) {
            if (withSignature) {
                StringBuilder stringBuilder = new StringBuilder();
                String outArgs = getFunctionOutArgs(node);
                String inArgs = getFunctionInArgs(node);

                if (outArgs.length() > 0) {
                    stringBuilder.append("[").append(outArgs).append("] = ");
                }
                return stringBuilder.append(functionName).append("(").append(inArgs).append(")").toString();
            } else {
                return functionName;
            }
        } else {
            return null;
        }
    }

    public static String getCellName(final MTree.Node node) {
        if (node.getType() != MTree.NodeType.CELL_TITLE) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.CELL_TITLE");
        }
        String sectionName = node.getText();
        if (sectionName.contains("%%")) {
            sectionName = sectionName.substring(sectionName.indexOf("%%"));
        }

        sectionName = sectionName.trim();
        return sectionName.length() == 0 ? null : sectionName;
    }

    public static String getTextForNode(final MTree.Node node) {
        EditorWrapper ew = EditorWrapper.getInstance();
        int startPos = ew.lc2pos(node.getStartLine(), node.getStartColumn());
        int endPos = ew.lc2pos(node.getEndLine(), node.getEndColumn() + 1);
        return ew.getText(startPos, endPos);
    }

    public static String getTextFormattedForNode(final MTree.Node node) {
        switch (node.getType()) {
            case CLASSDEF:
                return NodeUtils.getClassdef(node);
            case METHODS:
                return NodeUtils.getMethodHeader(node);
            case FUNCTION:
                return NodeUtils.getFunctionHeader(node, true);
            case CELL_TITLE:
                return NodeUtils.getCellName(node);
            default:
                return node.getText();
        }
    }

}
