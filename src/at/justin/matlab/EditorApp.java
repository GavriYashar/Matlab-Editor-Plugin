package at.justin.matlab;

import at.justin.matlab.gui.autoDetailViewer.AutoDetailViewer;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.prefs.Settings;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.matlab.api.editor.EditorApplicationListener;
import com.mathworks.matlab.api.editor.EditorEvent;
import com.mathworks.matlab.api.editor.EditorEventListener;
import com.mathworks.mde.editor.MatlabEditorApplication;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
    private static final AbstractAction copyAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("AE: CTRL + C");
        }
    };
    private static EditorApp INSTANCE;
    private static List<Editor> editors = new ArrayList<>();
    private static List<String> mCallbacks = new ArrayList<>();
    private static final KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            for (String s : mCallbacks) {
                try {
                    Matlab.getInstance().proxyHolder.get().feval(s, e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            KeyReleasedHandler.doYourThing(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (Settings.getPropertyBoolean("enableDoubleOperator")) {
                KeyReleasedHandler.doOperatorThing(e);
            }
            KeyReleasedHandler.doBookmarkThing(e);
        }

        /**
         * In matlab clear classes will remove all instances of EditorApp but not the keyListener
         * to prevent creating and adding new keyListeners to editor objects while they still have one.
         *
         * This is a quick and dirty way to prevent it. TODO: fix me
         * @return class string
         */
        @Override
        public String toString() {
            return this.getClass().toString();
        }
    };

    public static EditorApp getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new EditorApp();
        INSTANCE.addListener();
        return INSTANCE;
    }

    /**
     * adds a matlab function call to the matlab call stack
     *
     * @param string valid matlab function which can be called
     */
    public void addMatlabCallback(String string) throws Exception {
        if (!testMatlabCallback(string)) {
            throw new Exception("'" + string + "' is not a valid function");
        }
        if (!mCallbacks.contains(string))
            mCallbacks.add(string);
        else System.out.println("'" + string + "' already added");
    }

    /**
     * user can test if the passed string will actually be called as intended. will call the function w/o passing any
     * input arguments
     *
     * @param string valid matlab function which can be called
     * @return returns a boolean value true if succeeded
     */
    public boolean testMatlabCallback(String string) {
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
        getMatlabEditorApplication().addEditorApplicationListener(new EditorApplicationListener() {
            @Override
            public void editorOpened(Editor editor) {
                if (Settings.getPropertyBoolean("verbose")) {
                    System.out.println(editor.getLongName() + " has been opened");
                }
                setCallbacks();
                Bookmarks.getInstance().setEditorBookmarks(editor);
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
            if (editors.contains(editor)) {
                continue;
            }
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
        }

        List<Component> list = Matlab.getInstance().getComponents("EditorSyntaxTextPane");
        for (Component component : list) {
            KeyListener[] keyListeners = component.getKeyListeners();
            if (Settings.getPropertyBoolean("verbose")) {
                System.out.println("\n" + keyListeners.length + " keylisteners");
                for (KeyListener keyListener : keyListeners) {
                    System.out.println(keyListener.toString());
                }
            }

            for (KeyListener keyListener1 : keyListeners) {
                if (keyListener1.toString().equals(keyListener.toString())) {
                    component.removeKeyListener(keyListener1);  // this will assure that the new keylistener is added and the previous one is removed
                    // while matlab is still running and the .jar is replaced
                }
            }
            component.addKeyListener(keyListener);
            // JComponent jComponent = (JComponent) component;
            // jComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copyAction");
            // jComponent.getActionMap().put("copyAction", copyAction);
        }
        if (Settings.containsKey("bpColor")) {
            colorizeBreakpointView(Settings.getPropertyColor("bpColor"));
        } else {
            colorizeBreakpointView(ENABLED);
        }
    }

    public void colorizeBreakpointView(Color color) {
        List<Component> list = Matlab.getInstance().getComponents("BreakpointView$2");
        for (Component component : list) {
            component.setBackground(color);
        }
    }

    public void removeCallbacks() {
        List<Component> list = Matlab.getInstance().getComponents("EditorSyntaxTextPane");
        for (Component component : list) {
            component.removeKeyListener(keyListener);
        }
        colorizeBreakpointView(DISABLED);
    }
}
