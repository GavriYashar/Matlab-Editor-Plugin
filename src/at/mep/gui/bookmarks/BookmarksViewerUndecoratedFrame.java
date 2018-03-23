package at.mep.gui.bookmarks;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;

import java.awt.*;
import java.io.IOException;

/** Created by Andreas Justin on 2016-08-25. */
public class BookmarksViewerUndecoratedFrame extends UndecoratedFrame {
    private static BookmarksViewerUndecoratedFrame instance;

    private BookmarksViewerUndecoratedFrame() {
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

    public static BookmarksViewerUndecoratedFrame getInstance() {
        if (instance == null) instance = new BookmarksViewerUndecoratedFrame();
        return instance;
    }

    private void setLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        BookmarksViewer bookmarksViewer = BookmarksViewer.getInstance();
        instance.setLayout(new GridBagLayout());
        instance.add(bookmarksViewer, gbc);
        instance.setSize(Settings.getPropertyDimension("dim.bookmarksViewer"));

        Point sc = ScreenSize.getScreenCenterOfMouse();
        Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
        instance.setLocation(pos);
    }
}
