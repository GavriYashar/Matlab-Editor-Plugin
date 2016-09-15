package at.justin.matlab;

import at.justin.matlab.gui.autoDetailViewer.AutoDetailViewer;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.mesr.MESR;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.ComponentUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.matlab.api.editor.EditorApplicationListener;
import com.mathworks.matlab.api.editor.EditorEvent;
import com.mathworks.matlab.api.editor.EditorEventListener;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import com.mathworks.mde.editor.MatlabEditorApplication;
import com.mathworks.widgets.editor.breakpoints.BreakpointView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */

/**
 * clear classes
 * clc
 * javaaddpath('D:\Matlab\MATLAB\matlabcontrol-4.1.0.jar')
 * javaaddpath('D:\Matlab\MATLAB\matlab-editor-plugin_01.jar')
 * import at.justin.matlab.EditorApp;
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
        getMatlabEditorApplication().addEditorApplicationListener(new EditorApplicationListener() {
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
        return getMatlabEditorApplication().getActiveEditor();
    }

    public MatlabEditorApplication getMatlabEditorApplication() {
        return MatlabEditorApplication.getInstance();
    }

    public Editor openEditor(File file) {
        return getMatlabEditorApplication().openEditor(file);
    }

    public void setCallbacks() {
        List<Editor> openEditors = getMatlabEditorApplication().getOpenEditors();
        for (final Editor editor : openEditors) {
            if (editors.contains(editor)) continue;
            editors.add(editor);
            editor.addEventListener(new EditorEventListener() {
                @Override
                public void eventOccurred(EditorEvent editorEvent) {
                    // Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "editorEvent", editorEvent);
                    if (editorEvent == EditorEvent.ACTIVATED && Settings.getPropertyBoolean("autoDetailViewer")) {
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

            editorSyntaxTextPane.addKeyListener(KeyReleasedHandler.getKeyListener());
            editorSyntaxTextPane.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    Bookmarks.getInstance().adjustBookmarks(e, true);
                    try {
                        String insertString = e.getDocument().getText(e.getOffset(), e.getLength());
                        if (insertString.equals("%")) MESR.doYourThing();
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
                    EditorWrapper.setIsDirty(true);
                }
            });
        }

        if (Settings.containsKey("bpColor")) {
            colorizeBreakpointView(Settings.getPropertyColor("bpColor"));
        } else {
            colorizeBreakpointView(ENABLED);
        }
    }

    public void removeCallbacks() {
        List<Editor> openEditors = getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            EditorSyntaxTextPane editorSyntaxTextPane = ComponentUtil.getEditorSyntaxTextPaneForEditor(editor);
            editorSyntaxTextPane.removeKeyListener(KeyReleasedHandler.getKeyListener());
        }
        colorizeBreakpointView(DISABLED);
    }

    public void colorizeBreakpointView(Color color) {
        List<Editor> openEditors = getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : openEditors) {
            BreakpointView.Background breakpointView = ComponentUtil.getBreakPointViewForEditor(editor);
            if (breakpointView != null) breakpointView.setBackground(color);
        }
    }
}

//////////////////////////////////////////
// UNUSED CODE

// private static KeyStroke KS_FILESTRUCTURE = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_F12, true, false, false);
// private static KeyStroke KS_COPYCLIPBOARD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_C, true, false, false);
// private static KeyStroke KS_SHOWCLIPBOARD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_V, true, true, false);
// private static KeyStroke KS_DEBUG = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_E, true, true, false);
// private static KeyStroke KS_BOOKMARK = KeyStrokeUtil.getMatlabKeyStroke(MatlabKeyStrokesCommands.CTRL_PRESSED_F2);
// private static KeyStroke KS_SHOWBOOKMARK = KeyStrokeUtil.getKeyStroke(
//         KS_BOOKMARK.getKeyCode(),
//         (KS_BOOKMARK.getModifiers() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK,
//         (KS_BOOKMARK.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK,
//         false);

// JComponent jComponent = (JComponent) component;
// jComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copyAction");
// jComponent.getActionMap().put("copyAction", copyAction);

// addKeyStrokes((EditorSyntaxTextPane) component);
// private void addKeyStrokes(EditorSyntaxTextPane editorSyntaxTextPane) {
//     editorSyntaxTextPane.getInputMap(WF).put(KS_COPYCLIPBOARD, "MEP_CopyClipBoard");
//     editorSyntaxTextPane.getActionMap().put("MEP_CopyClipBoard", new AbstractAction("MEP_CopyClipBoard") {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//             KeyReleasedHandler.doCopyAction(e);
//             System.out.println("MEP_CopyClipBoard");
//         }
//     });
//     editorSyntaxTextPane.getInputMap(WF).put(KS_SHOWCLIPBOARD, "MEP_ShowCopyClipBoard");
//     editorSyntaxTextPane.getActionMap().put("MEP_ShowCopyClipBoard", new AbstractAction("MEP_ShowCopyClipBoard") {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//             KeyReleasedHandler.showClipboardStack(e);
//             System.out.println("MEP_ShowCopyClipBoard");
//         }
//     });
//     editorSyntaxTextPane.getInputMap(WF).put(KS_FILESTRUCTURE, "MEP_ShowFileStructure");
//     editorSyntaxTextPane.getActionMap().put("MEP_ShowFileStructure", new AbstractAction("MEP_ShowFileStructure") {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//             KeyReleasedHandler.showFileStructure(e);
//             System.out.println("MEP_ShowFileStructure");
//         }
//     });
//     editorSyntaxTextPane.getInputMap(WF).put(KS_DEBUG, "MEP_DEBUG");
//     editorSyntaxTextPane.getActionMap().put("MEP_DEBUG", new AbstractAction("MEP_DEBUG") {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//             System.out.println("MEP_DEBUG");
//             KeyReleasedHandler.DEBUG(e);
//         }
//     });
// for some reason bookmakrs don't work if editor is opened, while the others do
// editorSyntaxTextPane.getInputMap(WF).put(KS_BOOKMARK, "MEP_BOOKMARK");
// editorSyntaxTextPane.getActionMap().put("MEP_BOOKMARK", new AbstractAction("MEP_BOOKMARK") {
//     @Override
//     public void actionPerformed(ActionEvent e) {
//         System.out.println("MEP_BOOKMARK");
//     }
// });
// editorSyntaxTextPane.getInputMap(WF).put(KS_SHOWBOOKMARK, "MEP_SHOWBOOKMARKS");
// editorSyntaxTextPane.getActionMap().put("MEP_SHOWBOOKMARKS", new AbstractAction("MEP_SHOWBOOKMARKS") {
//     @Override
//     public void actionPerformed(ActionEvent e) {
//         System.out.println("MEP_SHOWBOOKMARKS");
//         KeyReleasedHandler.showBookmarksViewer(e);
//     }
// });
// }

//
// add callbacks the old way
// List<Component> list = Matlab.getInstance().getComponents("EditorSyntaxTextPane");
//         for (Component component : list) {
//                 KeyListener[] keyListeners = component.getKeyListeners();
//                 if (Settings.getPropertyBoolean("verbose")) {
//                 System.out.println("\n" + keyListeners.length + " keylisteners");
//                 for (KeyListener keyListener : keyListeners) {
//                 System.out.println(keyListener.toString());
//                 }
//                 }
//
//                 for (KeyListener keyListener1 : keyListeners) {
//                 if (keyListener1.toString().equals(KeyReleasedHandler.getKeyListener().toString())) {
//                 component.removeKeyListener(keyListener1);
//              // this will assure that the new keylistener is added and the previous one is removed
//                // while matlab is still running and the .jar is replaced
//                 }
//                 }
//                 component.addKeyListener(KeyReleasedHandler.getKeyListener());
//                 EditorSyntaxTextPane editorSyntaxTextPane = (EditorSyntaxTextPane) component;
//                 editorSyntaxTextPane.getDocument().addDocumentListener(new DocumentListener() {
// @Override
// public void insertUpdate(DocumentEvent e) {
//         Bookmarks.getInstance().adjustBookmarks(e, true);
//         try {
//         String insertString = e.getDocument().getText(e.getOffset(), e.getLength());
//         if (insertString.equals("%")) MESR.doYourThing();
//         } catch (BadLocationException ignored) {
//         ignored.printStackTrace();
//         }
//         }
//
// @Override
// public void removeUpdate(DocumentEvent e) {
//         Bookmarks.getInstance().adjustBookmarks(e, false);
//         }
//
// @Override
// public void changedUpdate(DocumentEvent e) {
//         EditorWrapper.setIsDirty(true);
//         }
//         });
//         }

// old  breakpointview color
// List<Component> list = Matlab.getInstance().getComponents("BreakpointView$2");
// for (Component component : list) {
//     component.setBackground(color);
// }
