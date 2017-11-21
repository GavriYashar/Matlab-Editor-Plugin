package at.mep.editor;

import at.mep.Matlab;
import com.mathworks.mde.liveeditor.LiveEditor;
import com.mathworks.mde.liveeditor.LiveEditorApplication;
import com.mathworks.mde.liveeditor.LiveEditorClient;

import javax.swing.*;
import java.io.File;

public class LiveEditorWrapper {
    /** returns true if current editor is an LiveEditor */
    public static boolean isActiveEditorLive() {
        if (Matlab.verLessThan(Matlab.R2017a)) {
            return false;
        }
        LiveEditorClient liveEditorClient = LiveEditorApplication.getLastActiveLiveEditorClient();
        return liveEditorClient != null;
    }

    public static LiveEditorClient getLiveEditorClient() {
        return LiveEditorApplication.getLastActiveLiveEditorClient();
    }

    /** returns active LiveEditor. Check first if current editor is an LiveEditor with EditorWrapper.isActiveEditorLive() */
    public static LiveEditor getActiveLiveEditor() {
        if (Matlab.verLessThan(Matlab.R2017a)
                || LiveEditorWrapper.getLiveEditorClient() == null) {
            return null;
        }

        return LiveEditorApplication.getLastActiveLiveEditorClient().getLiveEditor();
    }
    public static InputMap getInputMap() {
        if (Matlab.verLessThan(Matlab.R2017a)
                || LiveEditorWrapper.getLiveEditorClient() == null) {
            return null;
        }

        return LiveEditorApplication.getLastActiveLiveEditorClient().getInputMap();
    }
    public static File getFile() {
        if (Matlab.verLessThan(Matlab.R2017a)
                || LiveEditorWrapper.getLiveEditorClient() == null) {
            return new File("");
        }

        return new File(LiveEditorWrapper.getActiveLiveEditor().getLongName());
    }
    public static String getShortName() {
        if (Matlab.verLessThan(Matlab.R2017a)
                || LiveEditorWrapper.getLiveEditorClient() == null) {
            return "";
        }

        return LiveEditorWrapper.getActiveLiveEditor().getShortName();
    }
    public static String getLongName() {
        if (Matlab.verLessThan(Matlab.R2017a)
                || LiveEditorWrapper.getLiveEditorClient() == null) {
            return "";
        }

        return LiveEditorWrapper.getActiveLiveEditor().getLongName();
    }
}
