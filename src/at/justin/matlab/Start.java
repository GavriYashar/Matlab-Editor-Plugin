package at.justin.matlab;

import at.justin.matlab.editor.EditorApp;
import at.justin.matlab.editor.EditorWrapper;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.installer.Install;
import at.justin.matlab.prefs.Settings;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mlwidgets.prefs.PrefsChanger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/** Created by Andreas Justin on 2016-08-25. */
public class Start {
    public static void start(String customSettings, String defaultSettings) {
        if (!Settings.getPropertyBoolean("feature.enableMEP")) return;

        if (customSettings != null && defaultSettings != null) {
            loadSettings(customSettings, defaultSettings);
        }

        openEditorIfNecessary();

        try {
            setEditorCallbacks();
            setCmdWinCallbacks();
            addPrefs();
            MatlabKeyStrokesCommands.setCustomKeyStrokes();
            Bookmarks.getInstance().load();
            addShortcut();
            setReplacementPath();
            Settings.store();
        } catch (Exception | NoClassDefFoundError e) {
            try {
                EditorApp.getInstance().removeCallbacks();
            } catch (Exception ignored) {
            }
            e.printStackTrace();
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (int i = stackTraceElements.length-1; i >= 0 ; i--) {
                JOptionPane.showMessageDialog(
                        new JFrame(""),
                        stackTraceElements[i].getClassName() + "\n\n"
                        + stackTraceElements[i].getFileName() + "\n\n"
                        + stackTraceElements[i].getMethodName() + "\n\n"
                        + stackTraceElements[i].getLineNumber(),
                        "something went wrong, very very wrong",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void openEditorIfNecessary() {
        // ISSUE: #56
        // it was wrong to put this code in EditorWrapper. getActiveEditor should never open a new editor.
        // this issue needs to be handled in "Start"

        // ISSUE: #51
        // When Matlab starts, there is no active editor, yet. So the first editor is selected, but
        // if there is nothing, a new editor is opened.
        Editor editor = EditorWrapper.getActiveEditor();
        if (editor == null) {
            List<Editor> editors = EditorWrapper.getOpenEditors();
            if (editors.size() < 1) {
                EditorWrapper.getMatlabEditorApplication().newEditor(
                        "MEP: I'm sorry i opened a new document, but would you rather watch the world burn? (issue #51)");
                // TODO: this afterwards?
            }
        }
    }

    public static void start() {
        if (!Settings.getPropertyBoolean("feature.enableMEP")) return;
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

    private static void setReplacementPath() {
        if (Settings.getPropertyBoolean("feature.enableReplacements")
                && Settings.getProperty("path.mepr.rep").length() > 1)
            return;
        try {
            File fileRep = new File(Install.getJarFile().getParentFile().getPath() + "\\Replacements");
            File fileVar = new File(Install.getJarFile().getParentFile().getPath() + "\\Replacements\\Variables");
            if (!fileRep.exists() || !fileVar.exists()) {
                System.out.println(
                        "Live Templates has been disabled for one or both following reasons:"
                                + "\n  * No Replacement folder found in \"" + fileRep.getParent()
                                + "\n  * No Variables folder found in \"" + fileVar.getParent() + "\""
                                + "\nplease set \"path.mepr.rep\" and \"path.mepr.var\" manually");
                Settings.setPropertyBoolean("feature.enableReplacements", false);
                return;
            }
            Settings.setProperty("path.mepr.rep", fileRep.getPath());
            Settings.setProperty("path.mepr.var", fileVar.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage() + "\nplease set \"path.mepr.rep\" and \"path.mepr.var\" manually",
                    "something went wrong, very very wrong",
                    JOptionPane.ERROR_MESSAGE);
        }
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
