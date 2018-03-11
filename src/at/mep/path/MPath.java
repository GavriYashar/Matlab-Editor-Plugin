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
 * update will be called on startup (after load).
 *
 * which will return files found in index.
 * if a file is not found in index matlab's "which" will be called and the return value will be stored in this index.
 * if a file is not existing anymore in index, it will be removed from index and matlab's which will be called instead.
 *
 */
public class MPath {
    private static final int INITIAL_CAPACITY = 100000;
    private static final MPath INSTANCE = new MPath();
    private static EIndexingType indexingType = EIndexingType.CLASSES;
    private File mpIndexFile;

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
        indexingType = Settings.getFileIndexingType();
        if (indexingType == EIndexingType.NONE) {
            return;
        }
        File folder = Settings.getUserDirectory();
        mpIndexFile = new File(folder + "/MPFile.index");
        load();
    }

    @SuppressWarnings("unused")
    public static EIndexingType getIndexingType() {
        return indexingType;
    }

    @SuppressWarnings("unused")
    public static void setIndexingType(EIndexingType indexingType) {
        MPath.indexingType = indexingType;
    }

    /** which will return files found in index.
     * if a file is not found in index matlab's "which" will be called and teh return value will be stored in this index.
     * if a file is not existing anymore in index, it will be removed from index and matlab's which will be called instead.
     */
    public List<File> which(String name) throws MatlabInvocationException {
        if (indexingType == EIndexingType.NONE) {
            return Matlab.which_EVAL(name);
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
    @SuppressWarnings("unused")
    public void reIndexForeground() throws IllegalStateException {
        if (indexingType == EIndexingType.NONE) return;

        ifIsIndexingThrowError();
        clearIndex();
        if (indexingType == EIndexingType.DYNAMIC) {
            return;
        }
        update();
    }

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
                    "busy indexing folders [" + pathEntries.indexOf(pathEntry) + "/" + pathEntries.size() + "]: "
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
        if (indexingType == EIndexingType.NONE) return;
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
    public void load() throws IllegalStateException {
        if (indexingType == EIndexingType.NONE) return;
        if (!isIndexing() && !mpIndexFile.exists()) {
            reindexInBackground();
            return;
        }
        ifIsIndexingThrowError();

        Matlab.getInstance().setStatusMessage("loading index...");
        List<String> strings = new ArrayList<>(INITIAL_CAPACITY);
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

}
