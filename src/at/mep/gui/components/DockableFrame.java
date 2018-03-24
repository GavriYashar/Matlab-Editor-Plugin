package at.mep.gui.components;

import at.mep.editor.EditorWrapper;
import at.mep.gui.bookmarks.BookmarksViewerUndecoratedFrame;
import at.mep.gui.fileStructure.FileStructureUndecoratedFrame;
import at.mep.gui.mepr.MEPRViewerUndecoratedFrame;
import at.mep.gui.recentlyClosed.RecentlyClosedUndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.KeyStrokeUtil;
import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.mde.desk.MLMainFrame;
import com.mathworks.widgets.desk.DTSingleClientFrame;

import javax.swing.*;
import java.awt.event.*;

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
        FILE_STRUCTURE("FileStructure"),
        BOOKMARKS("Bookmarks"),
        LIVE_TEMPLATES("LiveTemplates"),
        RECENTLY_CLOSED("RecentlyClosed"),
        BREAKPOINTS("Breakpoints")
        ;

        private final String text;

        EViewer(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private EViewer viewer;
    protected final AbstractAction escAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        if (!isDockable() || isFloating()) {
            setVisible(false);
        }
        EditorWrapper.getActiveEditor().getTextComponent().requestFocus();
        }
    };

    public DockableFrame(EViewer eViewer) {
        this.viewer = eViewer;

        KeyStroke ksESC = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ESCAPE);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(ksESC, "ESCAPE");
        getActionMap().put("ESCAPE", escAction);
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

    public boolean isDockable() {
        return Settings.getPropertyBoolean("feature.enableDockableWindows");
    }

    public boolean isFloating() {
        return getTopLevelAncestor() instanceof DTSingleClientFrame;
    }

    protected void addFocusListener(JComponent jComponent) {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jComponent.requestFocus();
            }
        });
    }

    protected void addEnterAction(JComponent jComponent, AbstractAction enterAction) {
        KeyStroke ksENTER = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        jComponent.getInputMap(JComponent.WHEN_FOCUSED).put(ksENTER, "ENTER");
        jComponent.getActionMap().put("ENTER", enterAction);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        switch (getEState()) {
            case INVALID:
                break;
            case DOCKABLE_NEVER_SHOWN:
                MLDesktop.getInstance().addClient(this, viewer.getText());
                break;
            case FLOATING:
                getTopLevelAncestor().setVisible(visible);
                break;
            case DOCKED:
                getParent().getParent().setVisible(visible);
                break;
            case UNDECORATED:
                switch (viewer) {
                    case FILE_STRUCTURE:
                        FileStructureUndecoratedFrame.getInstance().setVisible(visible);
                        break;
                    case BOOKMARKS:
                        BookmarksViewerUndecoratedFrame.getInstance().setVisible(visible);
                        break;
                    case LIVE_TEMPLATES:
                        MEPRViewerUndecoratedFrame.getInstance().setVisible(visible);
                        break;
                    case RECENTLY_CLOSED:
                        RecentlyClosedUndecoratedFrame.getInstance().setVisible(visible);
                        break;
                }
                break;
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
