package at.justin.matlab;

import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.installer.Install;
import at.justin.matlab.prefs.Settings;
import com.mathworks.mlwidgets.prefs.PrefsChanger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class Start {
    public static void start(String customSettings, String defaultSettings) {
        if (customSettings != null && defaultSettings != null) {
            loadSettings(customSettings, defaultSettings);
        }
        setEditorCallbacks();
        addPrefs();
        MatlabKeyStrokesCommands.setCustomKeyStrokes();
        Bookmarks.getInstance().load();
    }

    public static void start() {
        File customFile;
        File defaultFile;
        try {
            defaultFile = Install.getDefaultPropertyFile();
            customFile = Install.getCustomPropertyFile();
            start(defaultFile.getAbsolutePath(), customFile.getAbsolutePath());
        } catch (IOException ignored) {
        }
        start(null, null);
    }


    private static void loadSettings(String customFile, String defaultFile) {
        Settings.loadSettings(customFile, defaultFile);
    }

    private static void setEditorCallbacks() {
        at.justin.matlab.EditorApp.getInstance().setCallbacks();
    }

    private static void addPrefs() {
        try {
            PrefsChanger.addPrefs();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
