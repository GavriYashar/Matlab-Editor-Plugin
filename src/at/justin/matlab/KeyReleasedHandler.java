package at.justin.matlab;

import at.justin.matlab.clipboardStack.ClipboardStack;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;
import matlabcontrol.MatlabInvocationException;

import java.awt.event.KeyEvent;

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
            //     if (data != null) clipboardStack.getInstance().add(data);
            // } catch (UnsupportedFlavorException | IOException ignored) {
            //     e.printStackTrace();
            // }
        }
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V) {
            ClipboardStack.getInstance().setVisible(true);
        }

        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_E) {
            MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());

            Tree commentTree = mTree.findAsTree(MTree.NodeType.COMMENT, MTree.NodeType.BLOCK_COMMENT);
            Tree functionTree = mTree.findAsTree(MTree.NodeType.FUNCTION, MTree.NodeType.CELL_TITLE, MTree.NodeType.PROPERTIES, MTree.NodeType.ENUMERATION, MTree.NodeType.EVENT);

            try {
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "mTree", mTree);
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "commentTree", commentTree);
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "functionTree", functionTree);
            } catch (MatlabInvocationException ignored) { }
        }

    }

}
