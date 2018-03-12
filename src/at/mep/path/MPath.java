package at.mep.path;

import at.mep.Matlab;
import at.mep.debug.Debug;
import at.mep.prefs.Settings;
import at.mep.util.FileUtils;
import at.mep.util.RunnableUtil;
import com.mathworks.fileutils.MatlabPath;
import matlabcontrol.MatlabInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class should remove the necessity to call matlabs "which" function.
 *
 * Builds an index on first creation of .m files depending on {@link EIndexingType}.
 * This index will be stored in Settings.getUserDirectory().
 *
 * update cannot be done on startup, MatlabPath is not initialized at this state.
 *
 * "which" will return files found in index.
 * if a file is not found in index matlab's "which" will be called and the return value will be stored in this index.
 * files not existing anymore on disk will be removed from index. Matlab's "which" will be called instead.
 */
public class MPath {
    private static final int INITIAL_CAPACITY = 100000;
    private static final MPath INSTANCE = new MPath();
    private static EIndexingType indexingType;
    private static File indexStoredFile;

    /** all files that are visible in matlab */
    private List<File> indexFiles = new ArrayList<>(INITIAL_CAPACITY);
    private Thread threadIndex = null;

    /**
     * all functions and classes visible in matlab as fully qualified string
     * e.g. num2str
     *      package.package.Class
     */
    private List<String> mStringShort = new ArrayList<>(INITIAL_CAPACITY);

    private MPath() {
        if (getIndexingType() == EIndexingType.NONE) {
            return;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static File getIndexStoredFile() {
        if (indexStoredFile == null) {
            File folder = Settings.getUserDirectory();
            indexStoredFile = new File(folder + "/MPFile.index");
        }
        return indexStoredFile;
    }

    @SuppressWarnings("WeakerAccess")
    public static EIndexingType getIndexingType() {
        if (indexingType == null) {
            indexingType = Settings.getFileIndexingType();
        }
        return indexingType;
    }

    @SuppressWarnings("unused")
    public static void setIndexingType(EIndexingType indexingType) {
        MPath.indexingType = indexingType;
    }

    /** "which" will return files found in index.
     * if a file is not found in index matlab's "which" will be called instead and the return value will be stored in this index.
     * files not existing anymore will be removed from index. Matlab's "which" will be called instead.
     */
    public List<File> which(String name) throws MatlabInvocationException {
        if (indexingType == EIndexingType.NONE) {
            return Matlab.which_EVAL(name);
        }
        if (indexFiles.size() == 0) {
            reindexInBackground();
        }
        List<File> files = which_INDEX(name);
        if (Debug.isDebugEnabled()) {
            System.out.println("found " + name + " in index");
        }
        if (files.size() == 0) {
            files.addAll(which_EVAL(name));
        }
        return files;
    }

    /** Calls matlabs which function and will update index if necessary */
    private List<File> which_EVAL(String name) throws MatlabInvocationException {
        List<File> files = new ArrayList<>(1);
        List<String> which = Matlab.whichString_EVAL(name);
        for (String string : which) {
            File file = new File(string);
            files.add(file);
            if (!isIndexing()) {
                add(file);
            }
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
        List<File> files4Removal = new ArrayList<>(0);
        for (File file : files) {
            if (file.exists()) continue;
            files4Removal.add(file);
        }
        remove(files4Removal);
        files.removeAll(files4Removal);
        return files;
    }

    @SuppressWarnings("WeakerAccess")
    public void clearIndex() {
        if (indexingType == EIndexingType.NONE) return;

        ifIsIndexingThrowError();
        indexFiles = new ArrayList<>(INITIAL_CAPACITY);
        mStringShort = new ArrayList<>(INITIAL_CAPACITY);
        store();
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isIndexing() {
        return threadIndex != null && threadIndex.isAlive();
    }

    private void ifIsIndexingThrowError() {
        if (isIndexing()) {
            throw new IllegalStateException("MPath is currently indexing");
        }
    }

    /** does a complete reindex of matlabs search paths*/
    @SuppressWarnings("WeakerAccess")
    public void reindexInBackground() {
        if (indexingType == EIndexingType.NONE) return;
        if (isIndexing()) return;

        clearIndex();
        if (indexingType == EIndexingType.DYNAMIC) {
            return;
        }
        threadIndex = RunnableUtil.runInNewThread(this::update);
    }

    /** adds only not added files from matlab search path to index */
    private void update() {
        List<MatlabPath.PathEntry> pathEntries = MatlabPath.getPathEntries();
        if (pathEntries.size() == 0) {
            Matlab.getInstance().setStatusMessage("nothing to index");
        }
        for (MatlabPath.PathEntry pathEntry: pathEntries) {
            Matlab.getInstance().setStatusMessage(
                    "indexing folders in background [" + pathEntries.indexOf(pathEntry) + "/" + pathEntries.size() + "]: "
                            + pathEntry.getDisplayValue());

            recursive(pathEntry.getCurrentlyResolvedPath());
        }
        store();
        Matlab.getInstance().setStatusMessage("");
    }

    private void recursive(File file) {
        // add class files
        if (indexingType == EIndexingType.FULL
        || (indexingType == EIndexingType.CLASSES && file.getAbsolutePath().contains("+"))) {
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

    private void remove(List<File> files) {
        for (File file : files) {
            remove(file);
        }
    }

    private void remove(int i) {
        indexFiles.remove(i);
        mStringShort.remove(i);
    }

    public static MPath getInstance() {
        return INSTANCE;
    }

    /** stores index in ascii format in Settings.getUserDirectory */
    @SuppressWarnings("WeakerAccess")
    public void store() {
        if (isIndexing()) return;
        if (indexingType == EIndexingType.NONE) return;
        StringBuilder sbFiles = new StringBuilder(indexFiles.size() * 50);
        for (File file : indexFiles) {
            sbFiles.append(file.getAbsolutePath());
            sbFiles.append("\n");
        }
        try {
            FileUtils.writeFileText(getIndexStoredFile(), sbFiles.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * it'll load an index if file exists
     * make sure that adding paths dynamically at startup are done before, otherwise it'll not recognize all paths
     */
    public void load() throws IllegalStateException {
        if (indexingType == EIndexingType.NONE) return;
        ifIsIndexingThrowError();
        if (!isIndexing() && !getIndexStoredFile().exists()) {
            reindexInBackground();
            return;
        }

        Matlab.getInstance().setStatusMessage("loading index...");
        List<String> strings = new ArrayList<>(INITIAL_CAPACITY);
        try {
            strings = FileUtils.readFileToStringList(getIndexStoredFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String string : strings) {
            File file = new File(string);
            indexFiles.add(file);
            mStringShort.add(FileUtils.fullyQualifiedName(file));
        }
    }

    /**
     * dynamic: index gets updated as needed.
     * classes: does a scan for classes in matlab's search path, index also gets updated as needed
     * full: does a full scan for .m files in matlabs search path, index also gets updated as needed
     */
    public enum EIndexingType {
        NONE(-1),
        DYNAMIC(0),
        CLASSES(1),
        FULL(2);

        private int indexingType;

        EIndexingType(int indexingType) {
            this.indexingType = indexingType;
        }
    }
}
