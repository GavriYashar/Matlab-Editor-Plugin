package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.EditorApp;
import at.justin.matlab.EditorWrapper;
import at.justin.matlab.installer.Install;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.ExecutionArrowDisplay;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class Bookmarks {
    private static final Bookmarks INSTANCE = new Bookmarks();
    private List<Bookmark> bookmarkList = new ArrayList<>(10);

    private Bookmarks() {
    }

    public static Bookmarks getInstance() {
        return INSTANCE;
    }

    private static List<Integer> getList(Editor editor) {
        return ((ExecutionArrowDisplay) editor.getExecutionArrowMargin()).getBookmarks();
    }

    public List<Bookmark> getBookmarkList() {
        return bookmarkList;
    }

    public void clearBookmarks() {
        List<Editor> editors = EditorApp.getInstance().getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : editors) {
            ((ExecutionArrowDisplay) editor.getExecutionArrowMargin()).clearBookmarks();
        }
        bookmarkList = new ArrayList<>(10);
    }

    public void toggleBookmarksForEditor(Editor editor) {
        List<Integer> lines = getList(editor);
        for (int line : lines) {
            toggleBookmark(new Bookmark(editor, line));
        }
    }

    public void toggleBookmark(Bookmark bookmark) {
        if (bookmarkList.contains(bookmark)) {
            bookmarkList.remove(bookmark);
            // System.out.println("removed " + bookmark);
            return;
        }
        bookmarkList.add(bookmark);
        // System.out.println("added " + bookmark);
    }

    /**
     * removes all bookmarks for editor
     *
     * @param editor
     */
    public void removeBookmarksForEditor(Editor editor) {
        for (int i = bookmarkList.size() - 1; i >= 0; i--) {
            if (bookmarkList.get(i).equalLongName(editor.getLongName())) {
                bookmarkList.remove(bookmarkList.get(i));
            }
        }
    }

    /**
     * removes specific bookmark for editor
     *
     * @param editor
     * @param line
     */
    public void removeBookmarkForEditor(Editor editor, int line) {
        Bookmark bookmark = new Bookmark(editor, line);
        removeBookmark(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        bookmarkList.remove(bookmark);
    }

    public void addBookmark(Bookmark bookmark) {
        bookmarkList.add(bookmark);
    }

    /**
     * add all bookmarks for editor
     *
     * @param editor
     */
    public void addBookmarksForEditor(Editor editor) {
        addBookmarksForEditor(editor, getList(editor));
    }

    /**
     * add specific bookmarks for editor
     *
     * @param editor
     * @param lines
     */
    public void addBookmarksForEditor(Editor editor, List<Integer> lines) {
        for (int bookmark : lines) {
            addBookmark(new Bookmark(editor, bookmark));
        }
    }

    public void addBookmarksForAllOpenEditors() {
        List<Editor> editors = EditorApp.getInstance().getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : editors) {
            addBookmarksForEditor(editor);
        }
    }

    public void setBookmarks(EditorWrapper ew) {
        removeBookmarksForEditor(ew.gae());
        toggleBookmarksForEditor(ew.gae());
    }

    /**
     * toggles bookmark in matlabs editor (if editor is open)
     *
     * @param editor
     */
    public void setEditorBookmarks(Editor editor) {
        if (!editor.isOpen()) return;
        List<Integer> lines = new ArrayList<>(10);
        for (Bookmark bookmark : bookmarkList) {
            if (bookmark.equalLongName(editor.getLongName())) lines.add(bookmark.getLineIndex());
        }

        ((ExecutionArrowDisplay) editor.getExecutionArrowMargin()).setBookmarks(lines);
    }

    public void save() {
        Writer writer = null;
        try {
            writer = new FileWriter(Install.getBookmarks(), false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Properties bookmarks = new Properties();
        bookmarks.setProperty("bookmarksCount", Integer.toString(bookmarkList.size()));
        for (int i = 0; i < bookmarkList.size(); i++) {
            Bookmark b = bookmarkList.get(i);
            String prop = "bookmark_" + i;
            bookmarks.setProperty(prop + ".Name", b.getName());
            bookmarks.setProperty(prop + ".ShortName", b.getShortName());
            bookmarks.setProperty(prop + ".LongName", b.getLongName());
            bookmarks.setProperty(prop + ".Line", Integer.toString(b.getLine()));
        }
        try {
            bookmarks.store(writer, "Stored Bookmarks");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Properties bookmarks = new Properties();
        try {
            InputStream in = new FileInputStream(Install.getBookmarks());
            bookmarks.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // reading props
        int count = Integer.parseInt(bookmarks.getProperty("bookmarksCount"));
        for (int i = 0; i < count; i++) {
            String prop = "bookmark_" + i;
            try {
                bookmarkList.add(new Bookmark(
                        Integer.parseInt(bookmarks.getProperty(prop + ".Line")),
                        bookmarks.getProperty(prop + ".Name"),
                        bookmarks.getProperty(prop + ".ShortName"),
                        bookmarks.getProperty(prop + ".LongName")
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // setting editor bookmarks
        List<Editor> editors = EditorApp.getInstance().getMatlabEditorApplication().getOpenEditors();
        for (Editor editor : editors) {
            setEditorBookmarks(editor);
        }
    }

}
