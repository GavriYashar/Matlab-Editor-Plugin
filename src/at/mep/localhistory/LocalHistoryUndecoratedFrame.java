package at.mep.localhistory;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;

import java.awt.*;
import java.io.IOException;

/** Created by Andreas Justin on 2018-12-19. */
public class LocalHistoryUndecoratedFrame extends UndecoratedFrame {
    private static LocalHistoryUndecoratedFrame instance;

    private LocalHistoryUndecoratedFrame() {
        setLayout();
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.bookmarksViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LocalHistoryUndecoratedFrame getInstance() {
        if (instance == null) instance = new LocalHistoryUndecoratedFrame();
        return instance;
    }

    private void setLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        LocalHistoryViewer localHistoryViewer = LocalHistoryViewer.getInstance();
        instance.setLayout(new GridBagLayout());
        instance.add(localHistoryViewer, gbc);
        instance.setSize(Settings.getPropertyDimension("dim.localHistoryViewer"));

        Point sc = ScreenSize.getScreenCenterOfMouse();
        Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
        instance.setLocation(pos);
    }
}
