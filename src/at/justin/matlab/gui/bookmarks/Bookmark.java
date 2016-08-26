package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.EditorWrapper;
import com.mathworks.matlab.api.editor.Editor;

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
    private Editor editor;
    private String name;

    public Bookmark(EditorWrapper editorWrapper) {
        this(editorWrapper.gae(), editorWrapper.getCurrentLine() - 1);
    }

    public Bookmark(Editor editor, int lineIndex) {
        this(editor, lineIndex, "");
    }

    public Bookmark(Editor editor, int lineIndex, String name) {
        this.editor = editor;
        this.lineIndex = lineIndex;
        this.line = lineIndex + 1;
        this.name = (name == null) ? "" : name;
    }

    public String getFilename() {
        return editor.getShortName();
    }

    public String getPath() {
        return editor.getLongName();
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
            return equalEditor(other.editor) && this.line == other.line;
        }
        return false;
    }

    public boolean equalEditor(Editor editor) {
        return this.editor == editor;
    }

    @Override
    public String toString() {
        return name + "Bookmark{line=" + line + "(lineIndex=" + lineIndex + ")" + ", editor=" + editor.getShortName() + ", name='" + name + '\'' + '}';
    }
}
