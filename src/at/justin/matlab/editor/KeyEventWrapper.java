package at.justin.matlab.editor;

import java.awt.event.KeyEvent;

/** Created by Andreas Justin on 2016-09-26. */
public class KeyEventWrapper {
    public static boolean ctrlFlag(KeyEvent keyEvent) {
        return keyEvent.isControlDown();
    }

    public static boolean shiftFlag(KeyEvent keyEvent) {
        return keyEvent.isShiftDown();
    }

    public static boolean altFlag(KeyEvent keyEvent) {
        return keyEvent.isAltDown();
    }

    public static boolean ctrlOnlyFlag(KeyEvent keyEvent) {
        return ctrlFlag(keyEvent) && !shiftFlag(keyEvent) && !altFlag(keyEvent);
    }

    public static boolean shiftOnlyFlag(KeyEvent keyEvent) {
        return !ctrlFlag(keyEvent) && shiftFlag(keyEvent) && !altFlag(keyEvent);
    }

    public static boolean altOnlyFlag(KeyEvent keyEvent) {
        return !ctrlFlag(keyEvent) && !shiftFlag(keyEvent) && altFlag(keyEvent);
    }

    public static boolean isEditor(KeyEvent keyEvent) {
        return keyEvent.getSource().toString().endsWith("EditorSyntaxTextPane");
    }

    public static boolean isCmdWin(KeyEvent keyEvent) {
        return keyEvent.getSource().toString().endsWith("XCmdWndView");
    }
}
