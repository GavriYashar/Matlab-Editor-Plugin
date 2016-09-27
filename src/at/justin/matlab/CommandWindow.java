package at.justin.matlab;

import at.justin.matlab.util.RunnableUtil;
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

    public static void insertTextAtPos(final String string, final int pos) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CommandWindow.getXCmdWndView().insert(string, pos);
            }
        };
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);
    }

    public static int getCaretPosition() {
        return getXCmdWndView().getCaretPosition();
    }

    public static XCmdWndView getXCmdWndView() {
        return XCmdWndView.getInstance();
    }
}
