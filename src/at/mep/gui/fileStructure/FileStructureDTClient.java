package at.mep.gui.fileStructure;


import at.mep.prefs.Settings;
import at.mep.util.ScreenSize;
import com.mathworks.widgets.desk.DTClientBase;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/** Created by Andreas Justin on 2018 - 03 - 21. */
@Deprecated
public class FileStructureDTClient extends DTClientBase {
    private static FileStructureDTClient instance;

    private FileStructureDTClient() {

    }
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

        instance.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!FileStructure.getInstance().isFloating()) {
                    // may be on sidebar (or in... or whatever), and does not get set visible otherwise
                    FileStructure.getInstance().setVisible(true);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
    }

}
