package at.justin.matlab.editor;

import at.justin.matlab.KeyReleasedHandler;
import at.justin.matlab.Matlab;
import at.justin.matlab.gui.autoDetailViewer.AutoDetailViewer;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.mepr.MEPR;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.ComponentUtil;
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
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2016 - 02 - 09. */

/**
 * clear classes
 * clc
 * javaaddpath('D:\Matlab\MATLAB\matlabcontrol-4.1.0.jar')
 * javaaddpath('D:\Matlab\MATLAB\matlab-editor-plugin_01.jar')
 * import at.justin.matlab.editor.EditorApp;
 * ea = EditorApp.getInstance();
 * ea.setCallbacks
 * ea.addMatlabCallback('testFunc')
 * ea.addMatlabCallback('TestFunction2')
 */


public class EditorApp {
    public static final Color ENABLED = new Color(179, 203, 111);
    public static final Color DISABLED = new Color(240, 240, 240);
    private static final int WF = JComponent.WHEN_FOCUSED;
    private static EditorApp INSTANCE;
    private static List<Editor> editors = new ArrayList<>();

    public static EditorApp getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new EditorApp();
        INSTANCE.addListener();
        return INSTANCE;
    }

    public void addMatlabCallback(String string) throws Exception {
        KeyReleasedHandler.addMatlabCallback(string);
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
        return EditorWrapper.getMatlabEditorApplication().getActiveEditor();
    }


    public Editor openEditor(File file) {
        return EditorWrapper.openEditor(file);
    }

    public void setCallbacks() {
        List<Editor> openEditors = EditorWrapper.getOpenEditors();
        for (final Editor editor : openEditors) {
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

            EditorSyntaxTextPane editorSyntaxTextPane = ComponentUtil.getEditorSyntaxTextPaneForEditor(editor);
            if (editorSyntaxTextPane == null) continue;
            KeyListener[] keyListeners = editorSyntaxTextPane.getKeyListeners();
            for (KeyListener keyListener1 : keyListeners) {
                if (keyListener1.toString().equals(KeyReleasedHandler.getKeyListener().toString())) {
                    editorSyntaxTextPane.removeKeyListener(keyListener1);
                    // this will assure that the new keylistener is added and the previous one is removed
                    // while matlab is still running and the .jar is replaced
                }
            }

            addKeyStrokes(editorSyntaxTextPane);
            editorSyntaxTextPane.addKeyListener(KeyReleasedHandler.getKeyListener());
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

    private void addKeyStrokes(EditorSyntaxTextPane editorSyntaxTextPane) {
        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DEBUG.getKeyStroke(), "MEP_DEBUG");
        editorSyntaxTextPane.getActionMap().put("MEP_DEBUG", MEPActionE.MEP_DEBUG.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_EXECUTE_CURRENT_LINE.getKeyStroke(), "MEP_EXECUTE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_EXECUTE_CURRENT_LINE", MEPActionE.MEP_EXECUTE_CURRENT_LINE.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_FILE_STRUCTURE.getKeyStroke(), "MEP_SHOW_FILE_STRUCTURE");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_FILE_STRUCTURE", MEPActionE.MEP_SHOW_FILE_STRUCTURE.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_COPY_CLIP_BOARD.getKeyStroke(), "MEP_SHOW_CLIP_BOARD_STACK");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_CLIP_BOARD_STACK", MEPActionE.MEP_SHOW_CLIP_BOARD_STACK.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_COPY_CLIP_BOARD.getKeyStroke(), "MEP_COPY_CLIP_BOARD");
        editorSyntaxTextPane.getActionMap().put("MEP_COPY_CLIP_BOARD", MEPActionE.MEP_COPY_CLIP_BOARD.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_SHOW_BOOKMARKS.getKeyStroke(), "MEP_SHOW_BOOKMARKS");
        editorSyntaxTextPane.getActionMap().put("MEP_SHOW_BOOKMARKS", MEPActionE.MEP_SHOW_BOOKMARKS.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DELETE_CURRENT_LINE.getKeyStroke(), "MEP_DELETE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_DELETE_CURRENT_LINE", MEPActionE.MEP_DELETE_CURRENT_LINE.getAction());

        editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_DUPLICATE_CURRENT_LINE.getKeyStroke(), "MEP_DUPLICATE_CURRENT_LINE");
        editorSyntaxTextPane.getActionMap().put("MEP_DUPLICATE_CURRENT_LINE", MEPActionE.MEP_DUPLICATE_CURRENT_LINE.getAction());

        // for some reason bookmarks don't work if editor is opened, while the others (actions) do
        // editorSyntaxTextPane.getInputMap(WF).put(MEPKeyStrokesE.KS_MEP_BOOKMARK.getKeyStroke(), "MEP_BOOKMARK");
        // editorSyntaxTextPane.getActionMap().put("MEP_BOOKMARK", MEPActionE.MEP_BOOKMARK.getAction());
    }

    public void removeCallbacks() {
        List<Editor> openEditors = EditorWrapper.getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            EditorSyntaxTextPane editorSyntaxTextPane = ComponentUtil.getEditorSyntaxTextPaneForEditor(editor);
            editorSyntaxTextPane.removeKeyListener(KeyReleasedHandler.getKeyListener());
        }
        colorizeBreakpointView(DISABLED);
    }

    public void colorizeBreakpointView(Color color) {
        List<Editor> openEditors = EditorWrapper.getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            BreakpointView.Background breakpointView = ComponentUtil.getBreakPointViewForEditor(editor);
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
