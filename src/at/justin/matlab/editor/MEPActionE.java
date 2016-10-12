package at.justin.matlab.editor;

import at.justin.debug.Debug;
import at.justin.matlab.CommandWindow;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.gui.bookmarks.BookmarksViewer;
import at.justin.matlab.gui.clipboardStack.ClipboardStack;
import at.justin.matlab.gui.fileStructure.FileStructure;
import at.justin.matlab.gui.mepr.MEPRViewer;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.ClipboardUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.ActionID;
import com.mathworks.mde.editor.EditorSyntaxTextPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

/** Created by Andreas Justin on 2016-10-12. */
public enum MEPActionE {
    MEP_DEBUG(new AbstractAction("MEP_SHOW_FILE_STRUCTURE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.DEBUG) return;
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

    MEP_SHOW_CLIP_BOARD_STACK(new AbstractAction("MEP_SHOW_CLIP_BOARD_STACK") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            showClipboardStack();
        }
    }),

    MEP_COPY_CLIP_BOARD(new AbstractAction("MEP_COPY_CLIP_BOARD") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableClipboardStack")) return;
            doCopyAction();
        }
    }),

    MEP_BOOKMARK(new AbstractAction("MEP_BOOKMARK") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("MEP_BOOKMARK");

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

    MEP_DELETE_CURRENT_LINE(new AbstractAction("MEP_DELETE_CURRENT_LINE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableDeleteCurrentLine")) return;
            doDeleteLineAction();
        }
    }),

    MEP_DUPLICATE_CURRENT_LINE(new AbstractAction("MEP_DUPLICATE_CURRENT_LINE") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Settings.getPropertyBoolean("feature.enableDuplicateLine")) return;
            doDuplicateLineAction();
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
    });


    private final AbstractAction action;

    MEPActionE(AbstractAction action) {
        this.action = action;
    }

    public AbstractAction getAction() {
        return action;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////// //

    private static void doDeleteLineAction() {
        EditorWrapper.deleteCurrentLine();
    }

    private static void doDuplicateLineAction() {
        EditorWrapper.duplicateCurrentLine();
    }

    private static void showClipboardStack() {
        ClipboardStack.getInstance().setVisible(true);
    }

    private static void showFileStructure() {
        FileStructure.getInstance().populateTree();
        FileStructure.getInstance().showDialog();
    }

    private static void showBookmarksViewer() {
        BookmarksViewer.getInstance().showDialog();
    }

    private static void doCopyAction() {
        String selText = EditorWrapper.getSelectedTxt();
        if (selText == null || selText.length() < 1) return;
        ClipboardStack.getInstance().add(selText);
        ClipboardUtil.addToClipboard(selText);
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
}
