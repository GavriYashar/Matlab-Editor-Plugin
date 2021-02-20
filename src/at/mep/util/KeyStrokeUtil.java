package at.mep.util;

import at.mep.EMatlabKeyStrokesCommands;
import at.mep.editor.EditorWrapper;
import java.awt.Toolkit;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Created by z0032f1t on 22.07.2016.
 * basically a strjoin utility for KeyStroke.getKeyStroke("some string")
 */
public class KeyStrokeUtil {

    private static final String ALT = "alt";
    private static final String CONTROL = "control"; // WINDOWS
    private static final String META = "meta"; // MAC OS; #150
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
        if( (m & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0 ){ //| InputEvent.CTRL_MASK)) != 0)
            s.append("Ctrl + ");
        }
        if ((m & (InputEvent.META_DOWN_MASK | InputEvent.META_MASK)) != 0) {
            s.append("Meta + ");
        }
        if ((m & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK)) != 0) {
            s.append("Alt + ");
        }
        if ((m & (InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK)) != 0) {
            s.append("Shift + ");
        }
        if ((m & (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON1_MASK)) != 0) {
            s.append("LeftMouse + ");
        }
        if ((m & (InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON2_MASK)) != 0) {
            s.append("RightMouse + ");
        }
        if ((m & (InputEvent.BUTTON3_DOWN_MASK | InputEvent.BUTTON3_MASK)) != 0) {
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
        return EditorWrapper.getInputMap().allKeys();
    }

    public static KeyStroke getMatlabKeyStroke(EMatlabKeyStrokesCommands e) {
        KeyStroke[] keyStrokes = getMatlabKeyStrokes();
        InputMap inputMap = EditorWrapper.getInputMap();
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

    public static KeyStroke getKeyStroke(int keyCode, boolean control, boolean meta, boolean shift, boolean alt, boolean released) {
        return KeyStroke.getKeyStroke(getKeyText(keyCode, control, meta, shift, alt, released));
    }

    public static KeyStroke getKeyStroke(String kbStringSetting) {
        String[] keys = kbStringSetting.split("\\s*\\++\\s*");
        boolean control = false;
        boolean meta = false;
        boolean shift = false;
        boolean alt = false;
        boolean released = false;

        int keyCode = Integer.MIN_VALUE;

        for (String key : keys) {
            switch (key.toUpperCase()) {
                case "CONTROL":
                    control = true;
                    break;
                case "META":
                    meta = true;
                    break;
                case "SHIFT":
                    shift = true;
                    break;
                case "ALT":
                    alt = true;
                    break;
                default:
                    try {
                        Field vkField = KeyEvent.class.getField("VK_" + key.toUpperCase());
                        keyCode = vkField.getInt(null);
                    } catch (NoSuchFieldException | IllegalAccessException ignored) { }
            }
        }
        if (keyCode == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("String '" + kbStringSetting + "' cannot be parsed make sure it is a valid VK_* string");
        }
        return KeyStroke.getKeyStroke(getKeyText(keyCode, control, meta, shift, alt, released));
    }

    public static String getKeyText(int keyCode, boolean control, boolean meta, boolean shift, boolean alt, boolean released) {
        String keyText = getKeyText(keyCode);

        String retText = "";
        if (shift) retText = retText + " " + SHIFT;
        if (control) retText = retText + " " + CONTROL;
        if (meta) retText = retText + " " + META;
        if (alt) retText = retText + " " + ALT;
        if (released) retText = retText + " " + RELEASED;
        if (!released) retText = retText + " " + PRESSED;
        return retText + " " + keyText;
    }

    public static String getKeyText(int keyCode) {
        return EKeyStroke.getKeyStrokeE(keyCode).getCommand();
    }

    public static int keyEventModifiersToKeyStrokeModifiers(int modifiers) {
        int shift = InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK;
        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // | InputEvent.CTRL_MASK;
        int alt = InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK;
        switch (modifiers) {
            case 1:
                return shift;
            case 2:
                return ctrl;
            case 3:
                return shift | ctrl;
            case 8:
                return alt;
            case 9:
                return shift | alt;
            case 10:
                return ctrl | alt;
            case 11:
                return ctrl | shift | alt;
        }
        return 0;
    }
}
