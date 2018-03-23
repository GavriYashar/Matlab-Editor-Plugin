package at.mep.gui.components;

import at.mep.gui.bookmarks.BookmarksViewerUndecoratedFrame;
import at.mep.gui.fileStructure.FileStructureUndecoratedFrame;
import at.mep.gui.mepr.MEPRViewer;
import at.mep.gui.mepr.MEPRViewerUndecoratedFrame;
import at.mep.prefs.Settings;
import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.mde.desk.MLMainFrame;
import com.mathworks.widgets.desk.DTSingleClientFrame;

import javax.swing.*;

public class DockableFrame extends JPanel {
    private enum EState {
        INVALID,

        /** is dockable, but has never been shown, so add to desktop */
        DOCKABLE_NEVER_SHOWN,

        /** is dockable, but its permitted to roam free... for a while */
        FLOATING,

        /** is docked */
        DOCKED,

        /** floating around and loves it's freedom */
        UNDECORATED;
    }

    public enum EViewer {
        FILESTRUCTURE("FileStructure"),
        BOOKMARKS("BookmarksViewer"),
        LIVETEMPLATES("LiveTemplates");

        private final String text;

        EViewer(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public boolean isDockable() {
        return Settings.getPropertyBoolean("feature.enableDockableWindows");
    }

    public boolean isFloating() {
        return getTopLevelAncestor() instanceof DTSingleClientFrame;
    }

    public void setVisible(boolean visible, EViewer eViewer) {
        super.setVisible(visible);
        switch (getEState()) {
            case INVALID:
                break;
            case DOCKABLE_NEVER_SHOWN:
                MLDesktop.getInstance().addClient(this, eViewer.getText());
                break;
            case FLOATING:
                getTopLevelAncestor().setVisible(visible);
                break;
            case DOCKED:
                getParent().getParent().setVisible(true);
                break;
            case UNDECORATED:
                switch (eViewer) {
                    case FILESTRUCTURE:
                        FileStructureUndecoratedFrame.getInstance().setVisible(true);
                        break;
                    case BOOKMARKS:
                        BookmarksViewerUndecoratedFrame.getInstance().setVisible(true);
                        break;
                    case LIVETEMPLATES:
                        MEPRViewerUndecoratedFrame.getInstance().setVisible(true);
                        break;
                }
                break;
        }
    }

    public EState getEState() {
        if (isDockable() && getParent() == null && getTopLevelAncestor() == null) {
            return EState.DOCKABLE_NEVER_SHOWN;
        } else if (isDockable() && getTopLevelAncestor() instanceof MLMainFrame) {
            return EState.DOCKED;
        } else if (isDockable() && getTopLevelAncestor() instanceof DTSingleClientFrame) {
            return EState.FLOATING;
        } else if (isDockable()) {
            return EState.DOCKABLE_NEVER_SHOWN;
        } else {
            return EState.UNDECORATED;
        }
    }
}

/*

// CREATION OF FLOATING MATLAB GUI
DTClient dtc = new DTClient(MLDesktop.getInstance(), "var2", "FileStructure");
DTSingleClientFrame dtscf;
try {
    Method createUndockedFrame = MLDesktop.getInstance().getClass().getDeclaredMethod(
            "createUndockedFrame",
            DTClient.class);

    Method setClientShowing = Desktop.class.getDeclaredMethod(
            "setClientShowing",
            DTClient.class,
            boolean.class,
            DTLocation.class,
            boolean.class);

    Class dtFloatingLocationClass = Class.forName("com.mathworks.widgets.desk.DTFloatingLocation");
    Constructor dtLocation = dtFloatingLocationClass.getDeclaredConstructor(boolean.class);

    createUndockedFrame.setAccessible(true);
    setClientShowing.setAccessible(true);
    dtLocation.setAccessible(true);

    dtscf = (DTSingleClientFrame) createUndockedFrame.invoke(MLDesktop.getInstance(), dtc);
    DTOnTopWindow dtOnTopWindow = (DTOnTopWindow) setClientShowing.invoke(MLDesktop.getInstance(), dtc, true, dtLocation.newInstance(true), true);

} catch (InstantiationException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
    e.printStackTrace();
    return;
}

 */
