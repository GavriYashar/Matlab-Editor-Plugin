package at.mep.gui.recentlyClosed;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;

import java.awt.*;
import java.io.IOException;

/** Created by Andreas Justin on 2017-10-11. */
public class RecentlyClosedUndecoratedFrame extends UndecoratedFrame {
    private static RecentlyClosedUndecoratedFrame instance;

    private RecentlyClosedUndecoratedFrame() {
        setLayout();
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.recentlyClosedViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RecentlyClosedUndecoratedFrame getInstance() {
        if (instance == null) instance = new RecentlyClosedUndecoratedFrame();
        return instance;
    }

    private void setLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        RecentlyClosed recentlyClosed = RecentlyClosed.getInstance();
        instance.setLayout(new GridBagLayout());
        instance.add(recentlyClosed, gbc);
        instance.setSize(Settings.getPropertyDimension("dim.recentlyClosedViewer"));

        Point sc = ScreenSize.getScreenCenterOfMouse();
        Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
        instance.setLocation(pos);
    }
}
