package at.justin.matlab;

import com.mathworks.mde.cmdwin.XCmdWndView;

/** Created by Andreas Justin on 2016-09-15. */
public class CommandWindow {
    public static void setCallbacks() {
        //CmdWin cmdWin = CmdWin.getInstance();
        getXCmdWndView().addKeyListener(KeyReleasedHandler.getKeyListener());
    }

    public static String getSelectedTxt() {
        return getXCmdWndView().getSelectedText();
    }

    public static void insertTextAtPos(String string, int pos) {
        getXCmdWndView().insert(string, pos);
    }

    public static int getCaretPosition() {
        return getXCmdWndView().getCaretPosition();
    }

    public static XCmdWndView getXCmdWndView() {
        return XCmdWndView.getInstance();
    }
}
