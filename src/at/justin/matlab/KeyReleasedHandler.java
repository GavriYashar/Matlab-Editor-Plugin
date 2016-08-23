package at.justin.matlab;

import at.justin.matlab.clipboardStack.ClipboardStack;
import at.justin.matlab.fileStructure.FileStructure;
import at.justin.matlab.prefs.Settings;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;
import matlabcontrol.MatlabInvocationException;

import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class KeyReleasedHandler {
    private static String opEqString;

    private static Pattern opEqPattern = Pattern.compile("[\\+\\-\\*/]");
    private static Pattern opEqLeftArgPattern = Pattern.compile("(\\s*)(.*?)(?=\\s*[\\+\\-\\*/]{2})");

    private KeyReleasedHandler() {
    }


    public static void doYourThing(KeyEvent e) {
        boolean ctrlFlag = e.isControlDown() | e.getKeyCode() == KeyEvent.VK_CONTROL;
        boolean shiftFlag = e.isShiftDown();
        boolean altFlag = e.isAltDown();
        boolean ctrlShiftFlag = ctrlFlag && shiftFlag && !altFlag;
        boolean ctrlShiftAltFlag = ctrlFlag && shiftFlag && altFlag;
        boolean ctrlOnlyFlag = ctrlFlag && !shiftFlag && !altFlag;
        boolean altOnlyFlag = !ctrlFlag && !shiftFlag && altFlag;

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

        if (ctrlOnlyFlag && e.getKeyCode() == KeyEvent.VK_F12) {
            FileStructure.getINSTANCE().populate(EditorWrapper.getInstance());
            FileStructure.getINSTANCE().setVisible(true);
        }

        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_E) {
            MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());

            Tree<MTree.Node> commentTree = mTree.findAsTree(MTree.NodeType.COMMENT, MTree.NodeType.BLOCK_COMMENT);
            Tree<MTree.Node> functionTree = mTree.findAsTree(MTree.NodeType.FUNCTION, MTree.NodeType.CELL_TITLE, MTree.NodeType.PROPERTIES, MTree.NodeType.ENUMERATION, MTree.NodeType.EVENT);
            Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);

            try {
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "mTree", mTree);
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "commentTree", commentTree);
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "functionTree", functionTree);
                Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "methodsTree", methodsTree);
            } catch (MatlabInvocationException ignored) {
            }
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
        if (Settings.getPropertyBoolean("verbose")) {
            System.out.println(
                    "inserted: " + newLine + " now select [l: " + currentLine + " c: " + (newLine.length() + matcher.group(1).length()) + "]");
        }
        ew.goToLineCol(currentLine, newLine.length() + matcher.group(1).length() + 2);
    }

    public static void doOperatorThing(KeyEvent e) {
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
