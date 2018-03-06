package at.mep.debug;

import at.mep.editor.EditorApp;
import at.mep.Matlab;
import at.mep.editor.EditorWrapper;
import at.mep.editor.tree.MFile;
import at.mep.gui.bookmarks.Bookmarks;
import com.mathworks.matlab.api.debug.BreakpointMargin;
import com.mathworks.mde.editor.ExecutionArrowDisplay;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;
import matlabcontrol.MatlabInvocationException;

/** Created by Andreas Justin on 2016-08-25. */
public class Debug {
    /**
     * should always be false
     * if needed enable in startup "at.mep.debug.Debug.setIsDebugEnabled(true)"
     */
    private static boolean IS_DEBUG_ENABLED = false;

    public static void debug() {
        System.out.println("yay");
    }

    /** assigns various objects to Matlab's workspace */
    public static void assignObjectsToMatlab() {
        MTree mTree = MTree.parse(EditorWrapper.getText());

        Tree<MTree.Node> commentTree = mTree.findAsTree(MTree.NodeType.COMMENT, MTree.NodeType.BLOCK_COMMENT);
        Tree<MTree.Node> functionTree = mTree.findAsTree(MTree.NodeType.FUNCTION, MTree.NodeType.CELL_TITLE, MTree.NodeType.PROPERTIES, MTree.NodeType.ENUMERATION, MTree.NodeType.EVENT);
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);
        EditorApp ea = EditorApp.getInstance();
        BreakpointMargin bpm = ea.getActiveEditor().getBreakpointMargin();
        ExecutionArrowDisplay ead = (ExecutionArrowDisplay) ea.getActiveEditor().getExecutionArrowMargin();

        assignObjectToMatlab("mTree", mTree);
        assignObjectToMatlab("mfile", MFile.construct(EditorWrapper.getActiveEditor()));
        assignObjectToMatlab("commentTree", commentTree);
        assignObjectToMatlab("functionTree", functionTree);
        assignObjectToMatlab("methodsTree", methodsTree);
        assignObjectToMatlab("breakpointMargin", bpm);
        assignObjectToMatlab("executionArrowDisplay", ead);
        assignObjectToMatlab("mlDesktop", Matlab.getInstance().getMlDesktop());

        assignObjectToMatlab("editorApp", ea);
        assignObjectToMatlab("bookmarks", Bookmarks.getInstance());
    }

    public static void assignObjectToMatlab(String variableName, Object o) {
        try {
            Matlab.getInstance().proxyHolder.get().feval("assignin", "base", variableName, o);
        } catch (MatlabInvocationException ignored) {
        }
    }

    public static void checkProxy() {
        System.out.println("isConnected: " + Matlab.getInstance().proxyHolder.get().isConnected());
        System.out.println("isExistingSession: " + Matlab.getInstance().proxyHolder.get().isExistingSession());
        System.out.println("isRunningInsideMatlab: " + Matlab.getInstance().proxyHolder.get().isRunningInsideMatlab());
    }

    public static boolean isDebugEnabled() {
        return IS_DEBUG_ENABLED;
    }

    public static void setIsDebugEnabled(boolean isDebugEnabled) {
        IS_DEBUG_ENABLED = isDebugEnabled;
    }
}
