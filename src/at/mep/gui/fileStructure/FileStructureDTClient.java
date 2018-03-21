package at.mep.gui.fileStructure;


import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;
import com.mathworks.widgets.desk.DTClientBase;

import java.awt.*;

/** Created by Andreas Justin on 2018 - 03 - 21. */
public class FileStructureDTClient extends DTClientBase {
    private static FileStructureDTClient instance;

    public static FileStructureDTClient getInstance() {
        if (instance == null){
            instance = new FileStructureDTClient();
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
        if (fileStructure.isFloating()) {
            instance.getTopLevelAncestor().setSize(Settings.getPropertyDimension("dim.fileStructureViewer"));
            Point sc = ScreenSize.getScreenCenterOfMouse();
            Point pos = new Point(sc.x - instance.getWidth()/2, sc.y - instance.getHeight()/2);
            instance.getTopLevelAncestor().setLocation(pos);
        }
    }

}
