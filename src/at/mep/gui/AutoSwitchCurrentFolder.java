package at.mep.gui;

import at.mep.editor.EditorWrapper;
import at.mep.prefs.Settings;
import at.mep.util.ComponentUtil;
import com.mathworks.matlab.api.explorer.FileLocation;
import com.mathworks.mde.explorer.Explorer;
import com.mathworks.mlwidgets.explorer.model.navigation.InvalidLocationException;
import com.mathworks.mlwidgets.explorer.model.navigation.NavigationContext;
import com.mathworks.widgets.desk.DTTitleBar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

/** Created by Gavri on 2017-03-20. */
public class AutoSwitchCurrentFolder {
    private static NavigationContext navigationContext = Explorer.getInstance().getContext();
    private static DTTitleBar dtTitleBar;
    private static JToggleButton jToggleButton = new JToggleButton("TEST");

    private static boolean added = false;

    static {
        // i ran in some issues with this, the button is not displayed, but it can be found in components list
        //
        // tb = at.mep.util.ComponentUtil.getCurrentFolderTitleBar()
        // tb.getComponents
        // tb.getComponent(5).getText
        //
        //
        // dtTitleBar = ComponentUtil.getCurrentFolderTitleBar();
        // addCheckbox();
    }

    public static void doYourThing() {
        if (!EditorWrapper.getFile().exists()) {
            return;
        }
        FileLocation fileLocation = new FileLocation(EditorWrapper.getFile().getParent());
        try {
            navigationContext.setLocation(fileLocation);
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }
    }

    private static void addCheckbox() {
        if (added) return;
        added = true;
        setJToggleButton();
        jToggleButton.setText("<->");
        jToggleButton.setSelected(Settings.getPropertyBoolean("feature.enableAutoCurrentFolder"));

        jToggleButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String val = "false";
                if (jToggleButton.isSelected()) val = "true";
                Settings.setProperty("feature.enableAutoCurrentFolder", val);
                try {
                    Settings.store();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static JToggleButton getjToggleButton() {
        return jToggleButton;
    }

    /**
     * to prevent creating a new checkbox on "clear classes" in matlab, and starting the plugin again.
     * Instead using already created one
     *
     * @return
     */
    private static void setJToggleButton() {
        Component[] components = dtTitleBar.getComponents();
        for (Component c : components) {
            if (c instanceof JToggleButton) {
                jToggleButton = (JToggleButton) c;
                return;
            }
        }
        dtTitleBar.add(jToggleButton);
    }

    public static NavigationContext getNavigationContext() {
        return navigationContext;
    }
}
