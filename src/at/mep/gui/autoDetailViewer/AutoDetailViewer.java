package at.mep.gui.autoDetailViewer;

import at.mep.editor.EditorWrapper;
import at.mep.gui.Icons;
import at.mep.prefs.Settings;
import com.mathworks.matlab.api.explorer.FileLocation;
import com.mathworks.matlab.api.explorer.FileSystemEntry;
import com.mathworks.mde.explorer.Explorer;
import com.mathworks.mlwidgets.explorer.DetailViewer;
import com.mathworks.mlwidgets.explorer.model.realfs.RealFileSystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

/**
 * creates a checkbox on the DetailsViewer Bar in matlab
 */
public class AutoDetailViewer {
    private static DetailViewer detailViewer = Explorer.getInstance().getDetailViewer();
    private static JCheckBox jCheckBox = new JCheckBox("");

    private static boolean added = false;

    static {
        addCheckbox();
    }

    public static void doYourThing() {
        addCheckbox();
        if (!EditorWrapper.getFile().exists()) {
            // check for "Untitled" as name is not good, since a "Untitled.m" can exist
            return;
        }
        FileLocation fileLocation = new FileLocation(EditorWrapper.getLongName());
        FileSystemEntry fileSystemEntry;
        try {
            fileSystemEntry = RealFileSystem.getInstance().getEntry(fileLocation);
            detailViewer.setFile(fileSystemEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addCheckbox() {
        if (added) return;
        added = true;
        setJCheckBox();
        if (Settings.getPropertyBoolean("feature.enableAutoDetailViewer")) {
            jCheckBox.setIcon(Icons.DETAIL_VIEWER_A.getIcon());
        } else {
            jCheckBox.setIcon(Icons.DETAIL_VIEWER_I.getIcon());
        }
        jCheckBox.setSelected(Settings.getPropertyBoolean("feature.enableAutoDetailViewer"));

        jCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String val = "false";
                if (jCheckBox.isSelected()) {
                    val = "true";
                    jCheckBox.setIcon(Icons.DETAIL_VIEWER_A.getIcon());
                    doYourThing();
                } else {
                    jCheckBox.setIcon(Icons.DETAIL_VIEWER_I.getIcon());
                }
                Settings.setProperty("feature.enableAutoDetailViewer", val);
                try {
                    Settings.store();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static JCheckBox getJCheckBox() {
        return jCheckBox;
    }

    /**
     * to prevent creating a new checkbox on "clear classes" in matlab, and starting the plugin again.
     * Instead using already created one
     *
     * @return
     */
    private static void setJCheckBox() {
        Container container = detailViewer.getButton().getParent();
        for (Component c : container.getComponents()) {
            if (c instanceof JCheckBox) {
                jCheckBox = (JCheckBox) c;
                return;
            }
        }
        detailViewer.getButton().getParent().add(jCheckBox);
    }

    public static DetailViewer getDetailViewer() {
        return detailViewer;
    }
}
