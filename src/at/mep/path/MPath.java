package at.mep.path;

import at.mep.Matlab;
import at.mep.prefs.Settings;
import at.mep.util.FileUtils;
import com.mathworks.fileutils.MatlabPath;
import matlabcontrol.MatlabInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class should remove the necessity to call matlabs "which" function.
 *
 * Builds an index on first creation of classfiles ("+" in path).
 * This index will be stored in Settings.getUserDirectory().
 *
 * update will be called on startup (after load).
 *
 * which will return files found in index.
 * if a file is not found in index matlab's "which" will be called and teh return value will be stored in this index.
 * if a file is not existing anymore in index, it will be removed from index and matlab's which will be called instead.
 *
 */
public class MPath {
    private static final int INITIAL_CAPACITY = 100000;
    private static final MPath INSTANCE = new MPath();
    private File mpIndexFile;
    private File mpIndexPath;

    /** path as in matlab, only updated if needed */
    private List<String> indexPath = new ArrayList<>(INITIAL_CAPACITY);

    /** all files that are visible in matlab */
    private List<File> indexFiles = new ArrayList<>(INITIAL_CAPACITY);

    /**
     * all functions and classes visible in matlab as fully qualified string
     * e.g. num2str
     *      package.package.Class
     */
    private List<String> mStringShort = new ArrayList<>(INITIAL_CAPACITY);


    private MPath() {
        File folder = Settings.getUserDirectory();
        mpIndexFile = new File(folder + "/MPFile.index");
        mpIndexPath = new File(folder + "/MPPath.index");
        load();
    }

    /** which will return files found in index.
     * if a file is not found in index matlab's "which" will be called and teh return value will be stored in this index.
     * if a file is not existing anymore in index, it will be removed from index and matlab's which will be called instead.
     */
    public List<File> which(String name) throws MatlabInvocationException {
        List<File> files = which_INDEX(name);
        if (files.size() == 0) {
            files.addAll(which_EVAL(name));
        }
        return files;
    }

    /** Calls matlabs which function and will update index if necessary */
    private List<File> which_EVAL(String name) throws MatlabInvocationException {
        List<File> files = new ArrayList<>(1);
        String cmd = "MEP_WHICH = which('" + name + "','-all');";
        Matlab.getInstance().proxyHolder.get().eval(cmd);
        String[] which = (String[]) Matlab.getInstance().proxyHolder.get().getVariable("MEP_WHICH");
        Matlab.getInstance().proxyHolder.get().eval("clear MEP_WHICH");
        for (String string : which) {
            File file = new File(string);
            files.add(file);
            add(file);
        }
        store();
        return files;
    }

    /** returns file if it is in index and valid otherwise it will return an array of length 0, and will remove non existing files */
    private List<File> which_INDEX(String name) {
        List<File> files = new ArrayList<>(1);
        for (int i = 0; i < mStringShort.size(); i++) {
            if (mStringShort.get(i).equals(name)) {
                files.add(indexFiles.get(i));
            }
        }
        for (File file : files) {
            if (file.exists()) continue;
            remove(file);
            files.remove(file);
        }
        return files;
    }

    /** stores index in ascii format in Settings.getUserDirectory */
    public void store() {
        StringBuilder sbPaths = new StringBuilder(indexPath.size() * 50);
        for (String path : indexPath) {
            sbPaths.append(path);
            sbPaths.append("\n");
        }
        try {
            FileUtils.writeFileText(mpIndexPath, sbPaths.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sbFiles = new StringBuilder(indexFiles.size() * 50);
        for (File file : indexFiles) {
            sbFiles.append(file.getAbsolutePath());
            sbFiles.append("\n");
        }
        try {
            FileUtils.writeFileText(mpIndexFile, sbFiles.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * it'll load an index if file exists
     * make sure that adding paths dynamically at startup are done before, otherwise it'll not recognize all paths
     */
    public void load() {
        if (!mpIndexFile.exists()) {
            reIndex();
            return;
        }

        Matlab.getInstance().setStatusMessage("loading index...");
        List<String> strings = new ArrayList<>(INITIAL_CAPACITY);
        try {
            strings = FileUtils.readFileToStringList(mpIndexPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexPath.addAll(strings);

        try {
            strings = FileUtils.readFileToStringList(mpIndexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String string : strings) {
            File file = new File(string);
            indexFiles.add(file);
            mStringShort.add(FileUtils.fullyQualifiedName(file));
        }
    }

    /** does a complete reindex of matlabs search paths*/
    public void reIndex() {
        indexPath = new ArrayList<>(INITIAL_CAPACITY);
        indexFiles = new ArrayList<>(INITIAL_CAPACITY);
        mStringShort = new ArrayList<>(INITIAL_CAPACITY);

        update();
    }

    /** adds only not added files from matlab search path to index */
    public void update() {
        List<MatlabPath.PathEntry> pathEntries = MatlabPath.getPathEntries();

        for (MatlabPath.PathEntry pathEntry: pathEntries) {
            Matlab.getInstance().setStatusMessage(
                    "busy indexing folders [" + pathEntries.indexOf(pathEntry) + "/" + pathEntries.size() + "]: "
                            + pathEntry.getDisplayValue());

            if (indexPath.contains(pathEntry.getDisplayValue())) continue;
            indexPath.add(pathEntry.getDisplayValue());
            recursive(pathEntry.getCurrentlyResolvedPath());
        }
        store();
        Matlab.getInstance().setStatusMessage("");
    }

    private void recursive(File file) {
        // add class files
        if (file.getAbsolutePath().contains("+")) {
            File[] filesM = file.listFiles((dir, name) -> name.endsWith(".m"));
            for (File f : filesM) {
                add(f);
            }
        }
        
        File[] filesD = file.listFiles(File::isDirectory);
        for (File f : filesD) {
            recursive(f);
        }
    }

    private void add(File file) {
        if (indexFiles.contains(file)) return;
        indexFiles.add(file);
        mStringShort.add(FileUtils.fullyQualifiedName(file));
    }

    private void remove(File file) {
        if (!indexFiles.contains(file)) return;
        remove(indexFiles.indexOf(file));
    }

    private void remove(int i) {
        indexFiles.remove(i);
        mStringShort.remove(i);
    }

    public static MPath getInstance() {
        return INSTANCE;
    }
}
