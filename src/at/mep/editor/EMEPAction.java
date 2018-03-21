package at.mep.editor;

import at.mep.CommandWindow;
import at.mep.debug.Debug;
import at.mep.gui.bookmarks.Bookmarks;
import at.mep.gui.bookmarks.BookmarksViewer;
import at.mep.gui.clipboardStack.ClipboardStack;
import at.mep.gui.clipboardStack.EClipboardParent;
import at.mep.gui.fileStructure.FileStructure;
import at.mep.gui.mepr.MEPRViewer;
import at.mep.gui.recentlyClosed.RecentlyClosed;
import at.mep.prefs.Settings;
import at.mep.util.ClipboardUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.ActionID;
import com.mathworks.mde.editor.EditorSyntaxTextPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

/** Created by Andreas Justin on 2016-10-12. */
public enum EMEPAction {
    MEP_DEBUG(new AbstractAction("MEP_SHOW_FILE_STRUCTURE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Debug.isDebugEnabled()) return;
            Debug.assignObjectsToMatlab();
        }
    }),

    MEP_EXECUTE_CURRENT_LINE(new AbstractAction("MEP_EXECUTE_CURRENT_LINE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableExecuteCurrentLine")) return;
            doExecuteCurrentLine();
        }
    }),

    MEP_SHOW_FILE_STRUCTURE(new AbstractAction("MEP_SHOW_FILE_STRUCTURE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableFileStructure")) return;
            showFileStructure();
        }
    }),

    MEP_SHOW_RECENTLY_CLOSED(new AbstractAction("MEP_SHOW_RECENTLY_CLOSED") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableRecentlyClosed")) return;
            showRecentlyClosed();
        }
    }),

    MEP_SHOW_CLIP_BOARD_STACK_CMD(new AbstractAction("MEP_SHOW_CLIP_BOARD_STACK_CMD") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            showClipboardStack(EClipboardParent.COMMAND);
        }
    }),

    MEP_SHOW_CLIP_BOARD_STACK_EDT(new AbstractAction("MEP_SHOW_CLIP_BOARD_STACK_EDT") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            showClipboardStack(EClipboardParent.EDITOR);
        }
    }),

    MEP_COPY_CLIP_BOARD(new AbstractAction("MEP_COPY_CLIP_BOARD") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // ISSUE #52
            // The problem of checking here is that the CTRL+C command is overwritten by MEP and this prevents everyone
            // from copying
            // if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            doCopyAction();
        }
    }),

    MEP_CUT_CLIP_BOARD(new AbstractAction("MEP_CUT_CLIP_BOARD") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // ISSUE #52
            // The problem of checking here is that the CTRL+X command is overwritten by MEP and this prevents everyone
            // from cutting
            // if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            doCutAction();
        }
    }),

    MEP_COPY_CLIP_BOARD_CMD(new AbstractAction("MEP_COPY_CLIP_BOARD_CMD") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            doCopyActionCmdView();
        }
    }),

    MEP_BOOKMARK(new AbstractAction("MEP_BOOKMARK") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableBookmarksViewer")) return;
            doToggleBookmark();
        }
    }),

    MEP_SHOW_BOOKMARKS(new AbstractAction("MEP_SHOW_BOOKMARKS") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableBookmarksViewer")) return;
            showBookmarksViewer();
        }
    }),

    MEP_DELETE_CURRENT_LINES(new AbstractAction("MEP_DELETE_CURRENT_LINE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableDeleteCurrentLine")) return;
            doDeleteLinesAction();
        }
    }),

    MEP_DUPLICATE_CURRENT_LINE_OR_SELECTION(new AbstractAction("MEP_DUPLICATE_CURRENT_LINE_OR_SELECTION") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableDuplicateLine")) return;
            doDuplicateLineOrSelectionAction();
        }
    }),

    MEP_MOVE_CURRENT_LINE_UP(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableLineMovement")) return;
            doMoveLineUp();
        }
    }),

    MEP_MOVE_CURRENT_LINE_DOWN(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableLineMovement")) return;
            doMoveLineDown();
        }
    }),

    MEP_MEPR_INSERT(new AbstractAction("MEP_MEPR_INSERT") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableReplacements")) return;
            MEPRViewer.getInstance().showDialog();
        }
    }),

    MEP_MEPR_QUICK_SEARCH(new AbstractAction("MEP_MEPR_QUICK_SEARCH") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableReplacements")) return;
            MEPRViewer.getInstance().quickSearch();
        }
    })
    ;


    private final AbstractAction action;

    EMEPAction(AbstractAction action) {
        this.action = action;
    }

    public AbstractAction getAction() {
        return action;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////// //

    private static void doDeleteLinesAction() {
        EditorWrapper.deleteCurrentLines();
    }

    private static void doDuplicateLineOrSelectionAction() {
        EditorWrapper.duplicateCurrentLineOrSelection();
    }

    private static void showClipboardStack(EClipboardParent eClipboardParent) {
        ClipboardStack.getInstance().setEClipboardParent(eClipboardParent);
        ClipboardStack.getInstance().setVisible(true);
    }

    private static void showFileStructure() {
        FileStructure.getInstance().populateTree();
        FileStructure.getInstance().showDialog();
    }

    private static void showRecentlyClosed() {
        RecentlyClosed.getInstance().showDialog();
    }

    private static void showBookmarksViewer() {
        BookmarksViewer.getInstance().showDialog();
    }

    private static void doCopyAction() {
        String selText = EditorWrapper.getSelectedTxt();
        if (selText == null || selText.length() < 1) return;
        ClipboardUtil.addToClipboard(selText);
        // Issue: #52
        if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
        ClipboardStack.getInstance().add(selText);
    }

    private static void doCutAction() {
        String selText = EditorWrapper.getSelectedTxt();
        if (selText == null || selText.length() < 1) return;
        ClipboardUtil.addToClipboard(selText);
        EditorWrapper.setSelectedTxt("");
        // Issue: #52
        if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
        ClipboardStack.getInstance().add(selText);
    }

    private static void doToggleBookmark() {
        Bookmarks.getInstance().setBookmarks();
        if (BookmarksViewer.getInstance().isVisible()) {
            BookmarksViewer.getInstance().updateList();
        }
        Bookmarks.getInstance().save();
    }

    public static void doCopyActionCmdView() {
        String selText = CommandWindow.getSelectedTxt();
        if (selText == null || selText.length() < 1) return;
        ClipboardStack.getInstance().add(selText);
        ClipboardUtil.addToClipboard(selText);
    }

    private static void doExecuteCurrentLine() {
        Editor editor = EditorWrapper.getActiveEditor();
        int[] position = EditorWrapper.getSelectionPosition(editor);
        int[] lcStart = EditorWrapper.pos2lc(editor, position[0]);
        int[] lcEnd = EditorWrapper.pos2lc(editor, position[1]);

        EditorWrapper.selectLine(editor, lcStart[0]);
        int newStart = EditorWrapper.getSelectionPositionStart();
        EditorWrapper.selectLine(editor, lcEnd[0]);
        int newEnd = EditorWrapper.getSelectionPositionEnd();

        EditorWrapper.goToPositionAndHighlight(editor, newStart, newEnd);

        EditorSyntaxTextPane editorSyntaxTextPane = EditorWrapper.getEditorSyntaxTextPane();
        Action action = editorSyntaxTextPane.getActionMap().get(ActionID.EVALUATE_SELECTION.getId());
        if (action == null) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    "Something went wrong: could not get action for \"" + ActionID.EVALUATE_SELECTION.getId() + "\"",
                    "Uh-Oh",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        action.actionPerformed(new ActionEvent(editorSyntaxTextPane, 0, null));
    }

    private static void doMoveLineUp() {
        EditorWrapper.moveCurrentLinesUp(EditorWrapper.getActiveEditor());
    }

    private static void doMoveLineDown() {
        EditorWrapper.moveCurrentLinesDown(EditorWrapper.getActiveEditor());
    }
}
