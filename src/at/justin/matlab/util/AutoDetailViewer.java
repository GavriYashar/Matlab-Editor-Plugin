package at.justin.matlab.util;

import at.justin.matlab.EditorWrapper;
import com.mathworks.matlab.api.explorer.FileLocation;
import com.mathworks.matlab.api.explorer.FileSystemEntry;
import com.mathworks.mde.explorer.Explorer;
import com.mathworks.mlwidgets.explorer.DetailViewer;
import com.mathworks.mlwidgets.explorer.model.realfs.RealFileSystem;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;

public class AutoDetailViewer {
    public static DetailViewer detailViewer = Explorer.getInstance().getDetailViewer();
    public static final JCheckBox jCheckBox = new JCheckBox();

    private static boolean added = false;

    static {
        System.out.println("static stuff");
        addCheckbox();
        jCheckBox.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                String val = "false";
                if (jCheckBox.isSelected()) val = "true";
                Settings.customProps.setProperty("autoDetailViewer",val);
                try {
                    Settings.store();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void addCheckbox() {
        if (added) return;
        added = true;
        jCheckBox.setText("Auto Select");
        detailViewer.getButton().getParent().add(jCheckBox);
    }

    public static void doYourThing() {
        addCheckbox();
        FileLocation fileLocation = new FileLocation(EditorWrapper.getInstance().getLongName());
        FileSystemEntry fileSystemEntry = null;
        try {
            System.out.println(fileLocation);
            fileSystemEntry = RealFileSystem.getInstance().getEntry(fileLocation);
            detailViewer.setFile(fileSystemEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
