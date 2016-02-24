package at.justin.matlab.util;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */

import at.justin.matlab.EditorWrapper;
import com.mathworks.widgets.text.mcode.MTree;
import java.util.Iterator;
import java.util.List;

public final class NodeUtils {
    private NodeUtils() {
    }

    private static String concatenateArgs(Iterator iterator) {
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            MTree.Node varID = (MTree.Node)iterator.next();
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

    public static String getMethodHeader(final MTree.Node node) {
        // may look like the following
        // lvl   node                                     description
        // 1     METHODS [12, 5] to [15, 7]               this is the whole method body
        // 2      ATTRIBUTES [12, 13] to [12, 36],        method attributes like static, private etc.
        // 3       ATTR [12, 14] to [0, 0],               introducing an attribute
        // 4        ID (Static) [12, 14] to [0, 0],       attribute
        // 3       ATTR [12, 29] to [0, 0],               introducing an attribute
        // 4        ID (Hidden) [12, 22] to [0, 0],       attribute
        // 4        ID (false) [12, 31] to [0, 0],        attribute flag
        // 3       FUNCTION [13, 9] to [14, 11],          introducing a function
        // 4        ETC [13, 24] to [0, 0]
        // 5         ID (a) [13, 19] to [0, 0]            out arg1
        // 5         ID (b) [13, 21] to [0, 0]            out arg2
        // 5         ETC [13, 30] to [13, 43],
        // 6          ID (asdf) [13, 26] to [0, 0],       function name
        // 6          ID (i) [13, 31] to [0, 0],          in arg1
        // 6          ID (want) [13, 34] to [0, 0],       in arg2
        // 6          ID (sth) [13, 40] to [0, 0]         in arg3
        if (node.getType() != MTree.NodeType.METHODS) {
            throw new IllegalArgumentException("node has to be a MTree.NodeType.METHODS");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("methods ");

        List<MTree.Node> methodNodes = node.getSubtree();
        Iterator methodIterator = methodNodes.iterator();
        // find ATTRIBUTES node
        while (methodIterator.hasNext()) {
            MTree.Node methodNextNode = (MTree.Node) methodIterator.next();
            if (methodNextNode.getType() == MTree.NodeType.ATTRIBUTES) {
                EditorWrapper ew = EditorWrapper.getInstance();
                int startPos = ew.lc2pos(methodNextNode.getStartLine(),methodNextNode.getStartColumn());
                int endPos = ew.lc2pos(methodNextNode.getEndLine(),methodNextNode.getEndColumn());
                stringBuilder.append(ew.getText(startPos,endPos));
            }
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String getFunctionInArgs(final MTree.Node node) {
        Iterator inputIter = node.getInputArguments().iterator();
        return concatenateArgs(inputIter);
    }

    public static String getFunctionOutArgs(final MTree.Node node) {
        Iterator outIter = node.getInputArguments().iterator();
        return concatenateArgs(outIter);
    }

    public static String getFunctionHeader(final MTree.Node node, boolean withSignature) {
        String functionName = node.getFunctionName().getText();
        if (functionName != null) {
            if (withSignature) {
                return "[" + getFunctionOutArgs(node) + "] = "
                        + functionName
                        + "(" + getFunctionInArgs(node) + ")";
            } else {
                return functionName;
            }
        } else {
            return null;
        }
    }

    public static String getCellName(final MTree.Node node) {
        String sectionName = node.getText();
        if (sectionName.contains("%%")) {
            sectionName = sectionName.substring(sectionName.indexOf("%%") + 2);
        }

        sectionName = sectionName.trim();
        return sectionName.length() == 0 ? null : sectionName;
    }
}
