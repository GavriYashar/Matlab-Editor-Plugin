package at.mep.gui;

import at.mep.editor.EditorWrapper;
import at.mep.prefs.Settings;
import com.mathworks.matlab.api.explorer.FileLocation;
import com.mathworks.matlab.api.explorer.FileSystemEntry;
import com.mathworks.mde.explorer.Explorer;
import com.mathworks.mlwidgets.explorer.DetailViewer;
import com.mathworks.mlwidgets.explorer.model.navigation.InvalidLocationException;
import com.mathworks.mlwidgets.explorer.model.navigation.NavigationContext;
import com.mathworks.mlwidgets.explorer.model.realfs.RealFileSystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

/** Created by Gavri on 2017-04-28. */
public class AutoSwitcher {
//    private static DTTitleBar dtTitleBar;
    private static DetailViewer detailViewer = Explorer.getInstance().getDetailViewer();
    private static NavigationContext navigationContext = Explorer.getInstance().getContext();

    private static final String NAME_DETAIL_VIEWER = "detailviewer";
    private static final String NAME_SWITCH_FOLDER= "switchfolder";
    private static final String TOOLTIP_DETAIL_VIEWER = "If on, details pane follows file currently opened in editor";
    private static final String TOOLTIP_SWITCH_FOLDER= "If on, working directory follows file currently opened in editor";

    private static JCheckBox jCBDetailViewer = new JCheckBox("");
    private static JCheckBox jCBSwitchFolder = new JCheckBox("");

    private static boolean added = false;

    public static void doYourThing() {
        addCheckbox();
        if (!EditorWrapper.getFile().exists()) {
            // check for "Untitled" as name is not good, since a "Untitled.m" can exist
            return;
        }
        FileLocation fileLocation = new FileLocation(EditorWrapper.getLongName());
        FileSystemEntry fileSystemEntry;
        if (jCBSwitchFolder.isSelected()) {
            try {
                navigationContext.setLocation(fileLocation.getParent());
            } catch (InvalidLocationException e) {
                e.printStackTrace();
            }
        }
        if (jCBDetailViewer.isSelected()) {
            try {
                fileSystemEntry = RealFileSystem.getInstance().getEntry(fileLocation);
                detailViewer.setFile(fileSystemEntry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addCheckbox() {
        if (added) return;
        added = true;
//        dtTitleBar = ComponentUtil.getCurrentFolderTitleBar();
//        dtTitleBar.add(jCBSwitchFolder);

        setJCheckBox();

        jCBDetailViewer.setSelected(Settings.getPropertyBoolean("feature.enableAutoDetailViewer"));
        if (jCBDetailViewer.isSelected()) {
            jCBDetailViewer.setIcon(EIcons.DETAIL_VIEWER_A.getIcon());
        } else {
            jCBDetailViewer.setIcon(EIcons.DETAIL_VIEWER_I.getIcon());
        }
        jCBDetailViewer.addChangeListener(e -> {
            String val = "false";
            if (jCBDetailViewer.isSelected()) {
                val = "true";
                jCBDetailViewer.setIcon(EIcons.DETAIL_VIEWER_A.getIcon());
                doYourThing();
            } else {
                jCBDetailViewer.setIcon(EIcons.DETAIL_VIEWER_I.getIcon());
            }
            Settings.setProperty("feature.enableAutoDetailViewer", val);
            try {
                Settings.store();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        jCBSwitchFolder.setSelected(Settings.getPropertyBoolean("feature.enableAutoCurrentFolder"));
        if (jCBSwitchFolder.isSelected()) {
            jCBSwitchFolder.setIcon(EIcons.GO_TO_FOLDER_A.getIcon());
        } else {
            jCBSwitchFolder.setIcon(EIcons.GO_TO_FOLDER_I.getIcon());
        }
        jCBSwitchFolder.addChangeListener(e -> {
            String val = "false";
            if (jCBSwitchFolder.isSelected()) {
                val = "true";
                jCBSwitchFolder.setIcon(EIcons.GO_TO_FOLDER_A.getIcon());
                doYourThing();
            } else {
                jCBSwitchFolder.setIcon(EIcons.GO_TO_FOLDER_I.getIcon());
            }
            Settings.setProperty("feature.enableAutoCurrentFolder", val);
            try {
                Settings.store();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private static void setJCheckBox() {
        Container container = detailViewer.getButton().getParent();
        for (Component c : container.getComponents()) {
            if (c instanceof JCheckBox) {
                switch (c.getName()) {
                    case NAME_DETAIL_VIEWER: {
                        jCBDetailViewer = (JCheckBox) c;
                        break;
                    }
                    case NAME_SWITCH_FOLDER: {
                        jCBDetailViewer = (JCheckBox) c;
                        break;
                    }
                }
            }
        }
        jCBDetailViewer.setName(NAME_DETAIL_VIEWER);
        jCBSwitchFolder.setName(NAME_SWITCH_FOLDER);
        jCBDetailViewer.setToolTipText(TOOLTIP_DETAIL_VIEWER);
        jCBSwitchFolder.setToolTipText(TOOLTIP_SWITCH_FOLDER);
        detailViewer.getButton().getParent().add(jCBDetailViewer);
        detailViewer.getButton().getParent().add(jCBSwitchFolder);
    }

    public static JCheckBox getjCBDetailViewer() {
        return jCBDetailViewer;
    }

    public static JCheckBox getjCBSwitchFolder() {
        return jCBSwitchFolder;
    }

    public static DetailViewer getDetailViewer() {
        return detailViewer;
    }
    
    public static NavigationContext getNavigationContext() {
        return navigationContext;
    }
}
