package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.EditorApp;
import at.justin.matlab.EditorWrapper;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.EditorAction;
import com.mathworks.mde.editor.EditorViewClient;

import java.io.File;

/**
 * Created by Andreas Justin on 2016-08-25.
 * Single bookmark with link to editor and line
 */
public class Bookmark {
    /**
     * lineIndex (0...X-1) lineIndex + 1;
     */
    private int line;
    /**
     * line (1...X) - 1;
     */
    private int lineIndex;
    private transient Editor editor;
    private String name;
    private String shortName;
    private String longName;

    public Bookmark(EditorWrapper editorWrapper) {
        this(editorWrapper.gae(), editorWrapper.getCurrentLine() - 1);
    }

    public Bookmark(Editor editor, int lineIndex) {
        this(editor, lineIndex, "");
    }

    public Bookmark(Editor editor, int lineIndex, String name) {
        this.editor = editor;
        this.shortName = editor.getShortName();
        this.longName = editor.getLongName();
        this.lineIndex = lineIndex;
        this.line = lineIndex + 1;
        this.name = (name == null) ? "" : name;
    }

    public Bookmark(int lineIndex, String name, String shortName, String longName) {
        this.line = lineIndex + 1;
        this.lineIndex = lineIndex;
        this.name = name;
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public Editor getEditor() {
        return editor;
    }

    public Editor reopen() {
        // if (editor == null || !editor.isOpen()) {
        // doesn't jump for some reason if editor isn't opaened again.
        editor = EditorApp.getInstance().openEditor(new File(longName));
        EditorViewClient editorViewClient = (EditorViewClient) editor.getComponent();
        editorViewClient.getEditorView().getActionManager().getAction(EditorAction.NEXT_BOOKMARK).setEnabled(true);
        editorViewClient.getEditorView().getActionManager().getAction(EditorAction.PREVIOUS_BOOKMARK).setEnabled(true);
        // }
        return editor;
    }

    /**
     * returns true if editor and line of both objects are the same
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bookmark) {
            Bookmark other = (Bookmark) obj;
            return equalLongName(other.longName) && this.line == other.line;
        }
        return false;
    }

    public boolean equalLongName(String longName) {
        return this.longName.equals(longName);
    }

    public void goTo() {
        reopen().goToLine(line, false);
    }
}
