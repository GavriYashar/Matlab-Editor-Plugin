package at.mep.gui.fileStructure;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;

import java.awt.*;
import java.io.IOException;

/** Created by Andreas Justin on 2016 - 02 - 24. */
public class FileStructureUndecoratedFrame extends UndecoratedFrame {
    private static FileStructureUndecoratedFrame instance;

    private FileStructureUndecoratedFrame() {
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.fileStructureViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileStructureUndecoratedFrame getInstance() {
        if (instance == null){
            instance = new FileStructureUndecoratedFrame();
            setLayout();
        }
        return instance;
    }

    private static void setLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        FileStructure fileStructure = FileStructure.getInstance();
        instance.setLayout(new GridBagLayout());
        instance.add(fileStructure, gbc);
        instance.setSize(Settings.getPropertyDimension("dim.fileStructureViewer"));

        Point sc = ScreenSize.getScreenCenterOfMouse();
        Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
        instance.setLocation(pos);
    }
}
