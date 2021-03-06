package at.mep;

import at.mep.debug.Debug;
import at.mep.editor.CustomShortCutKey;
import at.mep.editor.EditorApp;
import at.mep.editor.EditorWrapper;
import at.mep.gui.bookmarks.Bookmarks;
import at.mep.installer.Install;
import at.mep.path.MPath;
import at.mep.prefs.PrefsWindow;
import at.mep.prefs.Settings;
import at.mep.util.RunnableUtil;
import at.mep.workspace.WorkspaceWrapper;
import com.mathworks.mlwidgets.prefs.PrefsChanger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Andreas Justin on 2016-08-25.
 *
 * Starting point to run MEP from Matlab (e.g.: startup.m)
 */
public class Start {
    public static void start(String customSettings, String defaultSettings) {
        if (!Settings.getPropertyBoolean("feature.enableMEP")) return;

        if (customSettings != null && defaultSettings != null) {
            loadSettings(customSettings, defaultSettings);
        }

        // i don't get it. issue #56
        // now it froze on splash screen, an editor was open... i really don't get it.
        Runnable runnable = EditorWrapper::getFirstNonLiveEditor;
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);

        try {
            System.out.println();
            System.out.print("Initializing MEP: ");
            if (Debug.isDebugEnabled()) {
                System.out.println("Reloading custom Shortcuts");
            }
            CustomShortCutKey.reload();

            if (Debug.isDebugEnabled()) {
                System.out.println("Setting Editor Callbacks");
            }
            setEditorCallbacks();

            if (Debug.isDebugEnabled()) {
                System.out.println("Setting Command Window Callbacks");
            }
            setCmdWinCallbacks();

            if (Debug.isDebugEnabled()) {
                System.out.println("Setting Workspace Window Callbacks");
            }
            setWorkspaceWinCallbacks();

            if (Debug.isDebugEnabled()) {
                System.out.println("Adding Preferences");
            }
            addPrefs();

            if (Debug.isDebugEnabled()) {
                System.out.println("Setting Custom Keystrokes");
            }
            EMatlabKeyStrokesCommands.setCustomKeyStrokes();

            if (Debug.isDebugEnabled()) {
                System.out.println("Loading Bookmarks");
            }
            Bookmarks.getInstance().load();
            addShortcut();
            setReplacementPath();
            Settings.store();

            // indexing here is unnecessary since no path is set in com.mathworks.fileutils.MatlabPath at this time
            // indexing is done in background on first use
            if (MPath.getIndexStoredFile().exists()) {
                MPath.load();
            }
            System.out.print("done");
            System.out.println();
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

    /** entry point if custom and default properties are in the same folder as MEP*.jar */
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

    @SuppressWarnings("unused")
    public static void openPrefsPanel() {
        PrefsWindow.showDialog();
    }

    private static void setReplacementPath() {
        boolean enabledRep = Settings.getPropertyBoolean("feature.enableReplacements");
        if (!enabledRep) {
            return;
        }

        if (Settings.getPropertyBoolean("feature.enableReplacements")
                && Settings.getProperty("path.mepr.rep").length() > 1) {
            return;
        }

        try {
            File fileRep = new File(Install.getJarFile().getParentFile().getPath(), "Replacements");
            File fileVar = new File(fileRep, "Variables");
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
        // DTDocumentAccessor.addListener();
        EditorApp.getInstance().setCallbacks();
    }

    private static void setCmdWinCallbacks() {
        CommandWindow.setCallbacks();
    }

    private static void setWorkspaceWinCallbacks() {
        WorkspaceWrapper.setCallbacks();
    }

    /** will add MEPs preference panel to Matlab's preference window */
    private static void addPrefs() {
        try {
            PrefsChanger.addPrefs();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    private static void addShortcut() {
        // TODO
        String label = "MEP Help";
        String callback = "fprintf('MEP HELP')";
        String icon = "";
        String category = "";
        String isEditable = "false";
        // ShortcutUtils.addShortcutToBottom(label, callback, icon, category, isEditable);
    }
}
