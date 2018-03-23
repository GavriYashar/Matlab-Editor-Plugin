package at.mep.gui.mepr;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;
import java.awt.*;
import java.io.IOException;

/** Created by Andreas Justin on 2016-09-20. */
public class MEPRViewerUndecoratedFrame extends UndecoratedFrame {
    private static MEPRViewerUndecoratedFrame instance;

    private MEPRViewerUndecoratedFrame() {
        setLayout();
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.MEPRViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MEPRViewerUndecoratedFrame getInstance() {
        if (instance == null) instance = new MEPRViewerUndecoratedFrame();
        return instance;
    }

    private void setLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        MEPRViewer meprViewer = MEPRViewer.getInstance();
        instance.setLayout(new GridBagLayout());
        instance.add(meprViewer, gbc);
        instance.setSize(Settings.getPropertyDimension("dim.MEPRViewer"));

        Point sc = ScreenSize.getScreenCenterOfMouse();
        Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
        instance.setLocation(pos);
    }
}
