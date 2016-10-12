package at.justin.matlab;

import at.justin.matlab.editor.EditorWrapper;
import at.justin.matlab.editor.MEPActionE;
import at.justin.matlab.mepr.MEPR;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.KeyStrokeUtil;
import com.mathworks.mde.cmdwin.XCmdWndView;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016 - 02 - 09. */
public class KeyReleasedHandler {
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
            if (Matlab.getInstance().isBusy()) return;
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

    public static KeyListener getKeyListener() {
        return keyListener;
    }

    /**
     * adds a matlab function call to the matlab call stack
     *
     * @param string valid matlab function which can be called
     */
    public static void addMatlabCallback(String string) throws Exception {
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

        boolean isCmdWin = e.getSource() instanceof XCmdWndView;
        boolean isEditor = e.getSource() instanceof EditorSyntaxTextPane;

        if (isEditor && !isCmdWin) {
            // do only editor
            if (e.getKeyChar() == ("%").charAt(0)
                    && Settings.getPropertyBoolean("feature.enableReplacements"))
                MEPR.doYourThing();
            if (KS_BOOKMARK.getModifiers() == mod && KS_BOOKMARK.getKeyCode() == e.getKeyCode())
                ctrlf2 = true;
                // Doing the bookmarks here is error prone and won't work correctly.
                // this function ist called every time (afaik) before Matlab's keyRelease
            else if (KS_SHOWBOOKARKS.getModifiers() == mod && KS_SHOWBOOKARKS.getKeyCode() == e.getKeyCode())
                MEPActionE.MEP_SHOW_BOOKMARKS.getAction().actionPerformed(null);

        } else if (!isEditor && isCmdWin) {
            // do only cmdWin
            if (ctrlFlag && e.getKeyCode() == KeyEvent.VK_C)
                MEPActionE.doCopyActionCmdView();
        }
        // do editor and cmdWin
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V)
            MEPActionE.MEP_SHOW_CLIP_BOARD_STACK.getAction().actionPerformed(null);
        if (Settings.DEBUG && ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_E)
            MEPActionE.MEP_DEBUG.getAction().actionPerformed(null);
    }

    private static void operatorEqualsThing(KeyEvent e) {
        if (opEqString.length() != 2) return;
        String keyStr = Character.toString(e.getKeyChar());
        String currentLineStr = EditorWrapper.getCurrentLineText() + keyStr; // current line does not include currently pressed character
        Matcher matcher = opEqLeftArgPattern.matcher(currentLineStr);
        String newLine;
        if (!matcher.find()) return;

        newLine = matcher.group(2) + " = " + matcher.group(2) + " " + keyStr + "  ";
        int currentLine = EditorWrapper.getCurrentLine();
        EditorWrapper.goToLine(currentLine, true);
        EditorWrapper.setSelectedTxt(newLine);
        EditorWrapper.goToLineCol(currentLine, newLine.length() + matcher.group(1).length() + 2);
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
            MEPActionE.MEP_BOOKMARK.getAction().actionPerformed(null);
        }
    }
}
