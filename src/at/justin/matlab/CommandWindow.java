package at.justin.matlab;

import com.mathworks.mde.cmdwin.XCmdWndView;

/** Created by Andreas Justin on 2016-09-15. */
public class CommandWindow {
    public static void setCallbacks() {
        //CmdWin cmdWin = CmdWin.getInstance();
        getXCmdWndView().addKeyListener(KeyReleasedHandler.getKeyListener());
    }

    public static String getSelectedTxt() {
        return XCmdWndView.getInstance().getSelectedText();
    }

    public static XCmdWndView getXCmdWndView() {
        return XCmdWndView.getInstance();
    }
}
