package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.EditorApp;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.ExecutionArrowDisplay;

import java.util.ArrayList;
import java.util.List;

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
            System.out.println("removed " + bookmark);
            return;
        }
        bookmarkList.add(bookmark);
        System.out.println("added " + bookmark);
    }

    /**
     * removes all bookmarks for editor
     *
     * @param editor
     */
    public void removeBookmarksForEditor(Editor editor) {
        List<Integer> lines = getList(editor);
        for (int line : lines) {
            removeBookmarkForEditor(editor, line);
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
}
