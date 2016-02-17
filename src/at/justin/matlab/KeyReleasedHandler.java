package at.justin.matlab;

import at.justin.matlab.ClipboardStack.ClipboardStack;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class KeyReleasedHandler {
    private KeyReleasedHandler() {}

    public static void doYourThing(KeyEvent e) {
        boolean ctrlFlag = e.isControlDown() | e.getKeyCode() == KeyEvent.VK_CONTROL;
        boolean shiftFlag = e.isShiftDown();
        boolean altFlag = e.isAltDown();
        boolean ctrlShiftFlag    =  ctrlFlag &&  shiftFlag && !altFlag;
        boolean ctrlShiftAltFlag =  ctrlFlag &&  shiftFlag &&  altFlag;
        boolean ctrlOnlyFlag     =  ctrlFlag && !shiftFlag && !altFlag;
        boolean altOnlyFlag      = !ctrlFlag && !shiftFlag &&  altFlag;

        if (ctrlFlag && e.getKeyCode() == KeyEvent.VK_C) {
            ClipboardStack.getInstance().add(EditorWrapper.getInstance().getSelectedTxt());
            // try {
            //     String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            //     if (data != null) ClipboardStack.getInstance().add(data);
            // } catch (UnsupportedFlavorException | IOException ignored) {
            //     e.printStackTrace();
            // }
        }
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V) {
            ClipboardStack.getInstance().setVisible(true);
        }

    }

}
