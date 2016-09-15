package at.justin.matlab;

import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.installer.Install;
import at.justin.matlab.prefs.Settings;
import com.mathworks.mlwidgets.prefs.PrefsChanger;

import java.io.File;
import java.io.IOException;

/** Created by Andreas Justin on 2016-08-25. */
public class Start {
    public static void start(String customSettings, String defaultSettings) {
        if (customSettings != null && defaultSettings != null) {
            loadSettings(customSettings, defaultSettings);
        }
        setEditorCallbacks();
        setCmdWinCallbacks();
        addPrefs();
        MatlabKeyStrokesCommands.setCustomKeyStrokes();
        Bookmarks.getInstance().load();
        addShortcut();
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
        EditorApp.getInstance().setCallbacks();
    }

    private static void setCmdWinCallbacks() {
        CommandWindow.setCallbacks();
    }

    private static void addPrefs() {
        try {
            PrefsChanger.addPrefs();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addShortcut() {
        String label = "MEP Help";
        String callback = "fprintf('MEP HELP')";
        String icon = "";
        String category = "";
        String isEditable = "false";
        // ShortcutUtils.addShortcutToBottom(label, callback, icon, category, isEditable);
    }
}
