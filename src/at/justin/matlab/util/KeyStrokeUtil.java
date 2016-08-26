package at.justin.matlab.util;

import at.justin.matlab.MatlabKeyStrokesCommands;
import at.justin.matlab.EditorWrapper;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

/**
 * Created by z0032f1t on 22.07.2016.
 * basically a strjoin utility for KeyStroke.getKeyStroke("some string")
 */
public class KeyStrokeUtil {

    private static final String CONTROL = "control";
    private static final String SHIFT = "shift";
    private static final String RELEASED = "released";
    private static final String PRESSED = "pressed";

    private static final Pattern p = Pattern.compile("\\S+");

    public static KeyStroke stringToKeyStroke(String s) {
        throw new IllegalAccessError("Work in progress");
        // if (s == null) return null;
        //
        // List<String> allMatches = new ArrayList<String>();
        // Matcher m = p.matcher(s);
        // while (m.find()) {
        //     allMatches.add(m.group());
        // }
        // return null;
    }

    public static String keyStrokeToString(KeyStroke key) {
        if (key == null) return "";
        StringBuilder s = new StringBuilder(30);
        int m = key.getModifiers();
        if ( (m & (InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK)) != 0 ) {
            s.append("Ctrl + ");
        }
        if ( (m & (InputEvent.META_DOWN_MASK | InputEvent.META_MASK)) != 0 ) {
            s.append("Meta + ");
        }
        if ( (m & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK)) != 0 ) {
            s.append("Alt + ");
        }
        if ( (m & (InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK)) != 0 ) {
            s.append("Shift + ");
        }
        if ( (m & (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON1_MASK)) != 0 ) {
            s.append("LeftMouse + ");
        }
        if ( (m & (InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON2_MASK)) != 0 ) {
            s.append("RightMouse + ");
        }
        if ( (m & (InputEvent.BUTTON3_DOWN_MASK | InputEvent.BUTTON3_MASK)) != 0 ) {
            s.append("MiddleMouse + ");
        }
        switch (key.getKeyEventType()) {
            case KeyEvent.KEY_TYPED:
                s.append(key.getKeyChar() + " ");
                break;
            case KeyEvent.KEY_PRESSED:
            case KeyEvent.KEY_RELEASED:
                s.append(getKeyText(key.getKeyCode()) + " ");
                break;
            default:
                s.append("unknown event type ");
                break;
        }
        return s.toString();
    }

    public static KeyStroke[] getMatlabKeyStrokes() {
        return EditorWrapper.getInstance().getInputMap().allKeys();
    }

    public static KeyStroke getMatlabKeyStroke(MatlabKeyStrokesCommands e) {
        KeyStroke[] keyStrokes = getMatlabKeyStrokes();
        InputMap inputMap = EditorWrapper.getInstance().getInputMap();
        for (KeyStroke keystroke : keyStrokes) {
            String action = inputMap.get(keystroke).toString();
            if (action.toLowerCase().equals(e.getCommand().toLowerCase())) {
                return keystroke;
            }
        }
        return null;
    }

    public static KeyStroke getKeyStroke(int keyCode) {
        return KeyStroke.getKeyStroke(getKeyText(keyCode));
    }

    public static KeyStroke getKeyStroke(int keyCode, boolean control, boolean shift, boolean released) {
        return KeyStroke.getKeyStroke(getKeyText(keyCode,control,shift,released));
    }

    public static String getKeyText(int keyCode, boolean control, boolean shift, boolean released) {
        String keyText = getKeyText(keyCode);

        String retText = "";
        if (shift) retText = retText + " " + SHIFT;
        if (control) retText = retText + " " + CONTROL;
        if (released) retText = retText + " " + RELEASED;
        if (!released) retText = retText + " " + PRESSED;
        return retText + " " + keyText;
    }

    public static String getKeyText(int keyCode) {
        return KeyStrokeE.getKeyStrokeE(keyCode).getCommand();
    }
}
