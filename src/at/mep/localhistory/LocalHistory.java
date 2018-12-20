package at.mep.localhistory;

import at.mep.Matlab;
import at.mep.editor.EditorWrapper;
import at.mep.prefs.Settings;
import at.mep.util.DateUtil;
import at.mep.util.FileUtils;
import at.mep.util.RunnableUtil;
import com.mathworks.matlab.api.editor.Editor;
import matlabcontrol.MatlabInvocationException;

import java.io.File;

/** Created by Andreas Justin on 2018-12-17. */
public class LocalHistory {
    private static File FOLDER = new File(Settings.getUserDirectory(), "LocalHistory");
    private static long lastSweep = 0L;
    private static boolean isSweeping = false;

    /**
     * creates a copy of currently saved editor in $user.directory with current saving time as suffix.
     * Does not check whether the file has actually been modified
     * @param editor
     */
    public static void saveEditor(Editor editor) {
        try {
            // negotiateSave() does not throw an error if being debugged
            editor.negotiateSave();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (!Settings.getPropertyBoolean("feature.enableLocalHistory"))
            return;


        String suffix = DateUtil.getCurrentDate("YYYY_MM_dd_HH_mm_ss_SSS");
        try {
            if (!FOLDER.mkdir() && !FOLDER.exists()) {
                System.out.println("could not create local history folder");
                return;
            }
            File file = new File(FOLDER.getAbsolutePath(),
                    getHistoryFileNameForEditor(editor)
                            + "." + suffix + "."
                            + FileUtils.getExtension(EditorWrapper.getFile()));
            FileUtils.writeFileText(file, editor.getText());

            if ((System.currentTimeMillis() - lastSweep) > 1000 * 60 * 60 * 24) {
                // one sweep a day/per session is enough
                cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanup() {
        Runnable runnable = () -> {
            File[] files = FOLDER.listFiles();
            if (files == null) return;
            
            for (int i = files.length - 1; i >= 0 ; i--) {
                long dateDiff = System.currentTimeMillis() - files[i].lastModified();
                if (dateDiff > 1000*60*60*24*Settings.getPropertyInt("localHistory.daysToKeep")) {
                    files[i].delete();
                }
            }
        };
        if (!isSweeping) {
            isSweeping = true;
            RunnableUtil.runInNewThread(runnable, "LocalHistorySweep");
            isSweeping = false;
        }
    }

    public static void compare(File local, File remote) throws MatlabInvocationException {
        String command = Settings.getProperty("localHistory.command");
        try {
            if (command.contains("$LOCALJAVA") && command.contains("$REMOTEJAVA")) {
                Matlab.assignObjectToMatlab("MEP_localhistory_local_java", local);
                command = command.replace("$LOCALJAVA", "MEP_localhistory_local_java");

                Matlab.assignObjectToMatlab("MEP_localhistory_remote_java", remote);
                command = command.replace("$REMOTEJAVA", "MEP_localhistory_remote_java");

                Matlab.eval(command);
                Matlab.eval("clear MEP_localhistory_local_java");
                Matlab.eval("clear MEP_localhistory_remote_java");
            } else {
                command = command.replace("$LOCAL", local.getAbsolutePath());
                command = command.replace("$REMOTE", remote.getAbsolutePath());
                command = command.replace("\"", "\"\"");
                command = "system(\"" + command + " &\");";
                Matlab.eval(command);
            }
        } catch (MatlabInvocationException e) {
            System.out.println("LocalHistory Command: \n\t" + command);
            throw e;
        }
    }

    public static String getHistoryFileNameForEditor(Editor editor) {
        return EditorWrapper.getFullQualifiedClass(editor).replace(".","_");
    }

    public static File getFolder() {
        return FOLDER;
    }
}
