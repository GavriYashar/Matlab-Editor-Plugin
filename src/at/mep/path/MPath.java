package at.mep.path;

import at.mep.Matlab;
import at.mep.debug.Debug;
import at.mep.prefs.Settings;
import at.mep.util.FileUtils;
import at.mep.util.RunnableUtil;
import com.mathworks.fileutils.MatlabPath;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


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
    private static EIndexingType indexingType;
    private static File indexStoredFile;

    /** all files that are visible in matlab */
    private static List<File> indexFiles = new ArrayList<>(INITIAL_CAPACITY);
    /**
     * all functions and classes visible in matlab as fully qualified string
     * e.g. num2str
     *      package.package.Class
     */
    private static List<String> indexFQN = new ArrayList<>(INITIAL_CAPACITY);

    private static WorkerIndex workerIndex = new WorkerIndex();
    private static WorkerStore workerStore = new WorkerStore();

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

    public static List<File> getIndexFiles() {
        return indexFiles;
    }

    public static List<String> getIndexFQN() {
        return indexFQN;
    }

    /** "which" will return files found in index.
     * if a file is not found in index matlab's "which" will be called instead and the return value will be stored in this index.
     * files not existing anymore will be removed from index. Matlab's "which" will be called instead.
     */
    public static List<File> which(String name) throws MatlabInvocationException {
        if (getIndexingType() == EIndexingType.NONE) {
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
    private static List<File> which_EVAL(String name) throws MatlabInvocationException {
        List<File> files = new ArrayList<>(1);
        List<String> which = Matlab.whichString_EVAL(name);
        for (String string : which) {
            File file = new File(string);
            files.add(file);
            if (workerIndex.canDoStuff()) {
                add(file);
            }
        }
        store();
        return files;
    }

    /** returns file if it is in index and valid otherwise it will return an array of length 0, and will remove non existing files */
    private static List<File> which_INDEX(String name) {
        List<File> files = new ArrayList<>(1);
        for (int i = 0; i < indexFQN.size(); i++) {
            if (indexFQN.get(i).equals(name)) {
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
    public static void clearIndex() {
        if (getIndexingType() == EIndexingType.NONE) return;

        if (workerIndex.canDoStuff()) {
            indexFiles = new ArrayList<>(INITIAL_CAPACITY);
            indexFQN = new ArrayList<>(INITIAL_CAPACITY);
            store();
        }
    }

    /** does a complete reindex of matlabs search paths*/
    @SuppressWarnings("WeakerAccess")
    private static void reindexInBackground() {
        if (getIndexingType() == EIndexingType.NONE) return;
        if (!workerIndex.canDoStuff()) return;
        
        clearIndex();
        if (getIndexingType() == EIndexingType.DYNAMIC) {
            return;
        }
        workerIndex = new WorkerIndex();
        workerIndex.execute();
    }

    private static void add(File file) {
        if (indexFiles.contains(file)) return;
        indexFiles.add(file);
        indexFQN.add(FileUtils.fullyQualifiedName(file));
    }

    private static void remove(File file) {
        if (!indexFiles.contains(file)) return;
        remove(indexFiles.indexOf(file));
    }

    private static void remove(List<File> files) {
        for (File file : files) {
            remove(file);
        }
    }

    private static void remove(int i) {
        indexFiles.remove(i);
        indexFQN.remove(i);
    }

    /** stores index in ascii format in Settings.getUserDirectory */
    @SuppressWarnings("WeakerAccess")
    public static void store() {
        if (getIndexingType() == EIndexingType.NONE) return;
        if (!workerIndex.canDoStuff()) return;
        if (!workerStore.canDoStuff()) return;
        WorkerStore workerStore = new WorkerStore();
        workerStore.execute();
    }

    /**
     * it'll load an index if file exists
     * make sure that adding paths dynamically at startup are done before, otherwise it'll not recognize all paths
     */
    public static void load() throws IllegalStateException {
        if (getIndexingType() == EIndexingType.NONE) return;
        if (workerIndex.canDoStuff() && !getIndexStoredFile().exists()) {
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
            indexFQN.add(FileUtils.fullyQualifiedName(file));
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

    private static class WorkerStore extends SwingWorker<Void, Void> {
        private boolean wasExecuted = false;

        public boolean wasExecuted() {
            return wasExecuted;
        }

        public boolean canDoStuff() {
            return !wasExecuted() || (wasExecuted() && isDone());
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            StringBuilder sbFiles = new StringBuilder(indexFiles.size() * 50);
            for (File file : indexFiles) {
                sbFiles.append(file.getAbsolutePath());
                sbFiles.append("\n");
            }
            // TODO: if index is updated while storing runs, added files will get lost if store is not called again
            RunnableUtil.runInNewThread(() -> {
                try {
                    FileUtils.writeFileText(getIndexStoredFile(), sbFiles.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "MPath:store");
            return null;
        }
    }

    /** List[] -> {List<File> indexFiles, List<String> indexFQN} */
    private static class WorkerIndex extends SwingWorker<List[], List[]> {
        private boolean wasExecuted = false;

        public boolean wasExecuted() {
            return wasExecuted;
        }

        public boolean canDoStuff() {
            return !wasExecuted() || (wasExecuted() && isDone());
        }

        @Override
        protected List[] doInBackground() throws Exception {
            wasExecuted = true;
            
            List<File> indexFiles = new ArrayList<>(INITIAL_CAPACITY);
            List<String> indexFQN = new ArrayList<>(INITIAL_CAPACITY);

            List<MatlabPath.PathEntry> pathEntries = MatlabPath.getPathEntries();
            if (pathEntries.size() == 0) {
                Matlab.getInstance().setStatusMessage("nothing to index");
            }
            for (MatlabPath.PathEntry pathEntry: pathEntries) {
                String string = "indexing folders in background [" + pathEntries.indexOf(pathEntry) + "/" + pathEntries.size() + "]: "
                        + pathEntry.getDisplayValue();
                Matlab.getInstance().setStatusMessage(string);

                recursive(pathEntry.getCurrentlyResolvedPath(), indexFiles, indexFQN);
                publish(new List[]{indexFiles, indexFQN});
            }
            store();
            Matlab.getInstance().setStatusMessage("");

            return new List[]{indexFiles, indexFQN};
        }

        @Override
        protected void done() {
            super.done();
            List[] lists;
            try {
                lists = get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }

            //noinspection unchecked
            indexFiles = lists[0];
            //noinspection unchecked
            indexFQN = lists[1];

            store();
        }

        @Override
        protected void process(List<List[]> chunks) {
            super.process(chunks);
            //noinspection unchecked
            indexFiles = chunks.get(0)[0];
            //noinspection unchecked
            indexFQN = chunks.get(0)[1];
        }

        private static void recursive(File file, List<File> listIndexFile, List<String> listIndexFQN) {
            // add class files
            if (getIndexingType() == EIndexingType.FULL
                    || (getIndexingType() == EIndexingType.CLASSES && file.getAbsolutePath().contains("+"))) {
                File[] filesM = file.listFiles((dir, name) -> name.endsWith(".m"));
                for (File f : filesM) {
                    if (listIndexFile.contains(f)) continue;
                    listIndexFile.add(f);
                    listIndexFQN.add(FileUtils.fullyQualifiedName(f));
                }
            }

            File[] filesD = file.listFiles(File::isDirectory);
            for (File f : filesD) {
                recursive(f, listIndexFile, listIndexFQN);
            }
        }
    }
}
