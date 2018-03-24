package at.mep;

import at.mep.editor.EditorWrapper;
import at.mep.mepr.MEPR;
import at.mep.prefs.Settings;
import at.mep.util.KeyStrokeUtil;
import com.mathworks.mde.cmdwin.XCmdWndView;
import com.mathworks.mde.editor.EditorSyntaxTextPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016 - 02 - 09. */
public class KeyReleasedHandler {
    private static String opEqString;
    private static Pattern opEqPattern = Pattern.compile("[\\+]"); // "[\\+\\-\\*/]"
    private static Pattern opEqLeftArgPattern = Pattern.compile("(\\s*)(.*?)(?=\\s*[\\+\\-\\*/]{2})");
    private static final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (Matlab.getInstance().isBusy()) return;
            KeyReleasedHandler.doYourThing(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (Settings.getPropertyBoolean("enableDoubleOperator")) {
                KeyReleasedHandler.doOperatorThing(e);
            }
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
                    && Settings.getPropertyBoolean("feature.enableReplacements")) {
                MEPR.doYourThing();
            }
        }
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
}
