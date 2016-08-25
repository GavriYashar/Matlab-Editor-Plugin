package at.justin.debug;

import at.justin.matlab.EditorApp;
import at.justin.matlab.EditorWrapper;
import at.justin.matlab.Matlab;
import com.mathworks.matlab.api.debug.BreakpointMargin;
import com.mathworks.mde.editor.ExecutionArrowDisplay;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;
import matlabcontrol.MatlabInvocationException;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class Debug {
    public static void debug() {
        System.out.println("yay");
    }

    public static void assignObjectsToMatlab() {
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());

        Tree<MTree.Node> commentTree = mTree.findAsTree(MTree.NodeType.COMMENT, MTree.NodeType.BLOCK_COMMENT);
        Tree<MTree.Node> functionTree = mTree.findAsTree(MTree.NodeType.FUNCTION, MTree.NodeType.CELL_TITLE, MTree.NodeType.PROPERTIES, MTree.NodeType.ENUMERATION, MTree.NodeType.EVENT);
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);
        EditorApp ea = EditorApp.getInstance();
        BreakpointMargin bpm = ea.getActiveEditor().getBreakpointMargin();
        ExecutionArrowDisplay ead = (ExecutionArrowDisplay) ea.getActiveEditor().getExecutionArrowMargin();

        try {
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "mTree", mTree);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "commentTree", commentTree);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "functionTree", functionTree);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "methodsTree", methodsTree);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "editorApp", ea);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "breakpointMargin", bpm);
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "executionArrowDisplay", ead);
        } catch (MatlabInvocationException ignored) {
        }
    }
}
