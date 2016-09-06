package at.justin.matlab;

import at.justin.debug.Debug;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.gui.bookmarks.BookmarksViewer;
import at.justin.matlab.gui.clipboardStack.ClipboardStack;
import at.justin.matlab.gui.fileStructure.FileStructure;
import at.justin.matlab.mesr.MESR;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.KeyStrokeUtil;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
class KeyReleasedHandler {
    private static List<String> mCallbacks = new ArrayList<>();
    private static String opEqString;
    private static Pattern opEqPattern = Pattern.compile("[\\+]"); // "[\\+\\-\\*/]"
    private static Pattern opEqLeftArgPattern = Pattern.compile("(\\s*)(.*?)(?=\\s*[\\+\\-\\*/]{2})");
    private static boolean ctrlf2 = false; // if ctrl is released before F2. there may be a better way to fix the keyevent issue
    private static KeyStroke KS_BOOKMARK = KeyStrokeUtil.getMatlabKeyStroke(MatlabKeyStrokesCommands.CTRL_PRESSED_F2);
    private static KeyStroke KS_SHOWBOOKARKS = KeyStrokeUtil.getKeyStroke(
            KS_BOOKMARK.getKeyCode(),
            (KS_BOOKMARK.getModifiers() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK,
            (KS_BOOKMARK.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK,
            false);
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

    private KeyReleasedHandler() {
    }

    static KeyListener getKeyListener() {
        return keyListener;
    }

    /**
     * adds a matlab function call to the matlab call stack
     *
     * @param string valid matlab function which can be called
     */
    static void addMatlabCallback(String string) throws Exception {
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
    private static boolean testMatlabCallback(String string) {
        try {
            Matlab.getInstance().proxyHolder.get().feval(string);
            return true;
        } catch (MatlabInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void doYourThing(KeyEvent e) {
        boolean ctrlFlag = e.isControlDown() | e.getKeyCode() == KeyEvent.VK_CONTROL;
        boolean shiftFlag = e.isShiftDown();
        boolean altFlag = e.isAltDown();
        boolean ctrlShiftFlag = ctrlFlag && shiftFlag && !altFlag;
        boolean ctrlShiftAltFlag = ctrlFlag && shiftFlag && altFlag;
        boolean ctrlOnlyFlag = ctrlFlag && !shiftFlag && !altFlag;
        boolean altOnlyFlag = !ctrlFlag && !shiftFlag && altFlag;
        int mod = KeyStrokeUtil.keyEventModifiersToKeyStrokeModifiers(e.getModifiers());

        if (ctrlFlag && e.getKeyCode() == KeyEvent.VK_C) doCopyAction(null);
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V) showClipboardStack(null);
        if (ctrlOnlyFlag && e.getKeyCode() == KeyEvent.VK_F12) showFileStructure(null);
        if (Settings.DEBUG && ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_E) DEBUG(null);

        // bookmark thing
        if (KS_BOOKMARK.getModifiers() == mod && KS_BOOKMARK.getKeyCode() == e.getKeyCode()) {
            ctrlf2 = true;
            // Doing the bookmarks here is error prone and won't work correctly.
            // this function ist called every time (afaik) before Matlab's keyRelease
        } else if (KS_SHOWBOOKARKS.getModifiers() == mod && KS_SHOWBOOKARKS.getKeyCode() == e.getKeyCode()) {
            BookmarksViewer.getInstance().showDialog();
        }
    }

    private static void operatorEqualsThing(KeyEvent e) {
        if (opEqString.length() != 2) return;
        EditorWrapper ew = EditorWrapper.getInstance();
        String keyStr = Character.toString(e.getKeyChar());
        String currentLineStr = ew.getCurrentLineText() + keyStr; // current line does not include currently pressed character
        Matcher matcher = opEqLeftArgPattern.matcher(currentLineStr);
        String newLine;
        if (!matcher.find()) return;

        newLine = matcher.group(2) + " = " + matcher.group(2) + " " + keyStr + "  ";
        int currentLine = ew.getCurrentLine();
        ew.goToLine(currentLine, true);
        ew.setSelectedTxt(newLine);
        ew.goToLineCol(currentLine, newLine.length() + matcher.group(1).length() + 2);
    }

    private static void doOperatorThing(KeyEvent e) {
        String keyString = Character.toString(e.getKeyChar());
        Matcher matcher = opEqPattern.matcher(keyString);
        if (matcher.find()) {
            opEqString = opEqString + keyString;
        } else {
            opEqString = "";
        }
        operatorEqualsThing(e);
    }

    private static void doBookmarkThing(KeyEvent e) {
        if (ctrlf2) {
            ctrlf2 = false;
            Bookmarks.getInstance().setBookmarks(EditorWrapper.getInstance());
            if (BookmarksViewer.getInstance().isVisible()) {
                BookmarksViewer.getInstance().updateList();
            }
            Bookmarks.getInstance().save();
        }
    }

    static void doCopyAction(ActionEvent event) {
        ClipboardStack.getInstance().add(EditorWrapper.getInstance().getSelectedTxt());
        // try {
        //     String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        //     if (data != null) clipboardStack.getInstance().add(data);
        // } catch (UnsupportedFlavorException | IOException ignored) {
        //     e.printStackTrace();
        // }
    }

    static void showClipboardStack(ActionEvent event) {
        ClipboardStack.getInstance().setVisible(true);
    }

    static void showFileStructure(ActionEvent event) {
        FileStructure.getINSTANCE().populate(EditorWrapper.getInstance());
        FileStructure.getINSTANCE().showDialog();
    }

    static void DEBUG(ActionEvent event) {
        Debug.assignObjectsToMatlab();
    }

    static void showBookmarksViewer(ActionEvent event) {
        BookmarksViewer.getInstance().showDialog();
    }
}
