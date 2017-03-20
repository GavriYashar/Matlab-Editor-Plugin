package at.mep.gui;

import at.mep.editor.EditorWrapper;
import com.mathworks.matlab.api.explorer.FileLocation;
import com.mathworks.mde.explorer.Explorer;
import com.mathworks.mlwidgets.explorer.model.navigation.InvalidLocationException;
import com.mathworks.mlwidgets.explorer.model.navigation.NavigationContext;

/** Created by Gavri on 2017-03-20. */
public class AutoSwitchCurrentFolder {
    public static NavigationContext navigationContext = Explorer.getInstance().getContext();

    public static void doYourThing() {
        if (!EditorWrapper.getFile().exists()) {
            // check for "Untitled" as name is not good, since a "Untitled.m" can exist
            return;
        }
        FileLocation fileLocation = new FileLocation(EditorWrapper.getFile().getParent());
        try {
            navigationContext.setLocation(fileLocation);
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }
    }
}
