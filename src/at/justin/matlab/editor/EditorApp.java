package at.justin.matlab.editor;

import at.justin.matlab.KeyReleasedHandler;
import at.justin.matlab.Matlab;
import at.justin.matlab.gui.autoDetailViewer.AutoDetailViewer;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.mepr.MEPR;
import at.justin.matlab.prefs.Settings;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.matlab.api.editor.EditorApplicationListener;
import com.mathworks.matlab.api.editor.EditorEvent;
import com.mathworks.matlab.api.editor.EditorEventListener;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import com.mathworks.widgets.editor.breakpoints.BreakpointView;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2016 - 02 - 09. */

public class EditorApp {
    public static final Color ENABLED = new Color(179, 203, 111);
    public static final Color DISABLED = new Color(240, 240, 240);
    private static final int WF = JComponent.WHEN_FOCUSED;
    private static List<String> mCallbacks = new ArrayList<>();
    private static List<KeyStroke> keyStrokes = new ArrayList<>();
    private static List<String> actionMapKeys = new ArrayList<>();
    private static EditorApp INSTANCE;
    private static List<Editor> editors = new ArrayList<>();

    public static EditorApp getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new EditorApp();
        INSTANCE.addListener();
        return INSTANCE;
    }


    /** adds a matlab function call to the matlab call stack */
    public static void addMatlabCallback(String string, KeyStroke keyStroke, String actionMapKey) throws Exception {
        if (!testMatlabCallback(string)) {
            throw new Exception("'" + string + "' is not a valid function");
        }
        if (!mCallbacks.contains(string)) {
            mCallbacks.add(string);
            keyStrokes.add(keyStroke);
            actionMapKeys.add(actionMapKey);
            EditorApp.getInstance().setCallbacks();
        } else System.out.println("'" + string + "' already added");
    }

    /**
     * user can test if the passed string will actually be called as intended. will call the function w/o passing any
     * input arguments
     */
    private static boolean testMatlabCallback(String string) {
        try {
            Matlab.getInstance().proxyHolder.get().feval(string);
            return true;
        } catch (MatlabInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * on clear classes this listener will just be added to the editor application, which isn't the general idea of "good"
     * TODO: fix me
     */
    private void addListener() {
        EditorWrapper.getMatlabEditorApplication().addEditorApplicationListener(new EditorApplicationListener() {
            @Override
            public void editorOpened(Editor editor) {
                setCallbacks();
                Bookmarks.getInstance().setEditorBookmarks(editor);
                Bookmarks.getInstance().enableBookmarksForMatlab(editor);
            }

            @Override
            public void editorClosed(Editor editor) {
                if (Settings.getPropertyBoolean("verbose")) {
                    System.out.println(editor.getLongName() + " has been closed");
                    editors.remove(editor);
                }
            }

            @Override
            public String toString() {
                return this.getClass().toString();
            }
        });
    }

    public Editor getActiveEditor() {
        Editor editor = EditorWrapper.getMatlabEditorApplication().getActiveEditor();
        if (editor == null) {
            EditorWrapper.getMatlabEditorApplication().newEditor(
                    "MEP: Sorry!"
            );
        }
        return editor;
    }


    public Editor openEditor(File file) {
        return EditorWrapper.openEditor(file);
    }

    public void setCallbacks() {
        List<Editor> openEditors = EditorWrapper.getOpenEditors();
        for (final Editor editor : openEditors) {
            EditorSyntaxTextPane editorSyntaxTextPane = EditorWrapper.getEditorSyntaxTextPane(editor);
            if (editorSyntaxTextPane == null) continue;
            addKeyStrokes(editorSyntaxTextPane);
            addCustomKeyStrokes(editorSyntaxTextPane);
            if (editors.contains(editor)) continue;

            editors.add(editor);
            editor.addEventListener(new EditorEventListener() {
                @Override
                public void eventOccurred(EditorEvent editorEvent) {
                    // Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "editorEvent", editorEvent);
                    if (editorEvent == EditorEvent.ACTIVATED && Settings.getPropertyBoolean("feature.enableAutoDetailViewer")) {
                        AutoDetailViewer.doYourThing();
                    }
                }
            });

            boolean useListener = true;
            if (useListener) {
                KeyListener[] keyListeners = editorSyntaxTextPane.getKeyListeners();
                for (KeyListener keyListener1 : keyListeners) {
                    if (keyListener1.toString().equals(KeyReleasedHandler.getKeyListener().toString())) {
                        editorSyntaxTextPane.removeKeyListener(keyListener1);
                        // this will assure that the new keylistener is added and the previous one is removed
                        // while matlab is still running and the .jar is replaced
                    }
                }
                editorSyntaxTextPane.addKeyListener(KeyReleasedHandler.getKeyListener());
            }
            editorSyntaxTextPane.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    Bookmarks.getInstance().adjustBookmarks(e, true);
                    try {
                        String insertString = e.getDocument().getText(e.getOffset(), e.getLength());
                        if (insertString.equals("%")) MEPR.doReplace();
                    } catch (BadLocationException ignored) {
                        ignored.printStackTrace();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    Bookmarks.getInstance().adjustBookmarks(e, false);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    EditorWrapper.setDirtyIfLastEditorChanged(editor);
                    EditorWrapper.setIsActiveEditorDirty(true);
                }
            });
        }

        if (Settings.containsKey("bpColor")) {
            colorizeBreakpointView(Settings.getPropertyColor("bpColor"));
        } else {
            colorizeBreakpointView(ENABLED);
        }
    }

    private void addCustomKeyStrokes(EditorSyntaxTextPane editorSyntaxTextPane) {
        for (int i = 0; i < mCallbacks.size(); i++) {
            editorSyntaxTextPane.getInputMap(WF).put(keyStrokes.get(i), actionMapKeys.get(i));
            final int finalI = i;
            editorSyntaxTextPane.getActionMap().put(actionMapKeys.get(i), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Matlab.getInstance().proxyHolder.get().feval(mCallbacks.get(finalI), e);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }

    private void addKeyStrokes(EditorSyntaxTextPane editorSyntaxTextPane) {
        // NOTE: enable/disable feature cannot be checked here. the problem in the current design is, that matlab would
        //       need a restart after enabling features afterwards. that's why the features are checked in the
        //       "MEPActionE" Class 

        // DEBUG
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DEBUG.getKeyStroke(), "MEP_DEBUG");
        editorSyntaxTextPane.getActionMap().put("MEP_DEBUG", MEPActionE.MEP_DEBUG.getAction());

        // CURRENT LINES
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_EXECUTE_CURRENT_LINE.getKeyStroke(), "MEP_EXECUTE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_EXECUTE_CURRENT_LINE", MEPActionE.MEP_EXECUTE_CURRENT_LINE.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DELETE_CURRENT_LINE.getKeyStroke(), "MEP_DELETE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_DELETE_CURRENT_LINE", MEPActionE.MEP_DELETE_CURRENT_LINE.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DUPLICATE_CURRENT_LINE.getKeyStroke(), "MEP_DUPLICATE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_DUPLICATE_CURRENT_LINE", MEPActionE.MEP_DUPLICATE_CURRENT_LINE.getAction());

        // FILE STRUCTURE
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_FILE_STRUCTURE.getKeyStroke(), "MEP_SHOW_FILE_STRUCTURE");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_FILE_STRUCTURE", MEPActionE.MEP_SHOW_FILE_STRUCTURE.getAction());

        // CLIPBOARD
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_COPY_CLIP_BOARD.getKeyStroke(), "MEP_SHOW_CLIP_BOARD_STACK");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_CLIP_BOARD_STACK", MEPActionE.MEP_SHOW_CLIP_BOARD_STACK.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_COPY_CLIP_BOARD.getKeyStroke(), "MEP_COPY_CLIP_BOARD");
        editorSyntaxTextPane.getActionMap().put("MEP_COPY_CLIP_BOARD", MEPActionE.MEP_COPY_CLIP_BOARD.getAction());

        // MEPR
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_MEPR_INSERT.getKeyStroke(), "MEP_MEPR_INSERT");
        editorSyntaxTextPane.getActionMap().put("MEP_MEPR_INSERT", MEPActionE.MEP_MEPR_INSERT.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_MEPR_QUICK_SEARCH.getKeyStroke(), "MEP_MEPR_QUICK_SEARCH");
        editorSyntaxTextPane.getActionMap().put("MEP_MEPR_QUICK_SEARCH", MEPActionE.MEP_MEPR_QUICK_SEARCH.getAction());

        // BOOKMARKS
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_BOOKMARKS.getKeyStroke(), "MEP_SHOW_BOOKMARKS");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_BOOKMARKS", MEPActionE.MEP_SHOW_BOOKMARKS.getAction());
        // for some reason bookmarks don't work if editor is opened, while the others (actions) do
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_BOOKMARK.getKeyStroke(), "MEP_BOOKMARK");
        editorSyntaxTextPane.getActionMap().put("MEP_BOOKMARK", MEPActionE.MEP_BOOKMARK.getAction());
    }

    public void removeCallbacks() {
        List<Editor> openEditors = EditorWrapper.getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            EditorSyntaxTextPane editorSyntaxTextPane = EditorWrapper.getEditorSyntaxTextPane(editor);
            editorSyntaxTextPane.removeKeyListener(KeyReleasedHandler.getKeyListener());
        }
        colorizeBreakpointView(DISABLED);
    }

    public void colorizeBreakpointView(Color color) {
        List<Editor> openEditors = EditorWrapper.getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            BreakpointView.Background breakpointView = EditorWrapper.getBreakPointView(editor);
            if (breakpointView != null) breakpointView.setBackground(color);
        }
    }
}

//////////////////////////////////////////
// UNUSED CODE
// old  breakpointview color
// List<Component> list = Matlab.getInstance().getComponents("BreakpointView$2");
// for (Component component : list) {
//     component.setBackground(color);
// }
