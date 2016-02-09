package at.justin.matlab;

import at.justin.matlab.Clipboard.Clipboard;

import java.awt.event.KeyEvent;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class KeyReleasedHandler {
    private KeyReleasedHandler() {}

    public static void doYourThing(KeyEvent e) {
        boolean ctrlFlag = e.isControlDown();
        boolean shiftFlag = e.isShiftDown();
        boolean altFlag = e.isAltDown();
        boolean ctrlShiftFlag    =  ctrlFlag &&  shiftFlag && !altFlag;
        boolean ctrlShiftAltFlag =  ctrlFlag &&  shiftFlag &&  altFlag;
        boolean ctrlOnlyFlag     =  ctrlFlag && !shiftFlag && !altFlag;
        boolean altOnlyFlag      = !ctrlFlag && !shiftFlag &&  altFlag;

        if (ctrlFlag && e.getKeyCode() == KeyEvent.VK_C) {
            EditorWrapper ew = EditorWrapper.getInstance();
            Clipboard.getInstance().add(ew.getSelectedTxt());
        }
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V) {
            Clipboard.getInstance().setVisible(true);
        }

    }

}
