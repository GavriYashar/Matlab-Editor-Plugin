package at.justin.matlab.editor;

import at.justin.matlab.util.ComponentUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import com.mathworks.mde.editor.MatlabEditorApplication;
import com.mathworks.widgets.text.mcode.cell.CellUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-09-21. */
@SuppressWarnings("WeakerAccess")
public class EditorWrapper {
    private static final Pattern spaceStart = Pattern.compile("^\\s*");
    private static Editor lastEditor;
    private static String[] lastEditorTextArray;
    private static String[] activeEditorTextArray;
    /** if true textArray needs to be updated, fixing a huge performance issue */
    private static boolean isActiveEditorDirty = true;
    private static boolean isLastEditorDirty = true;

    public static boolean isActiveEditorDirty() {
        return EditorWrapper.isActiveEditorDirty;
    }

    public static void setLastEditor(Editor editor) {
        lastEditor = editor;
        isLastEditorDirty = true;
    }

    public static void setDirtyIfLastEditorChanged(Editor editor) {
        isLastEditorDirty = lastEditor != editor;
    }

    public static void setIsActiveEditorDirty(boolean isDirty) {
        EditorWrapper.isActiveEditorDirty = isDirty;
    }

    public static String[] getActiveEditorTextArray() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateActiveEditorTextArray();
            setIsActiveEditorDirty(false);
        }
        return EditorWrapper.activeEditorTextArray;
    }

    public static String[] getLastEditorTextArray() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateLastEditorTextArray();
            isLastEditorDirty = false;
        }
        return EditorWrapper.lastEditorTextArray;
    }

    public static void updateActiveEditorTextArray() {
        EditorWrapper.activeEditorTextArray = EditorWrapper.getTextArray(EditorWrapper.getActiveEditor());
    }

    public static void updateLastEditorTextArray() {
        EditorWrapper.lastEditorTextArray = EditorWrapper.getTextArray(lastEditor);
    }

    public static Editor openEditor(File file) {
        return EditorWrapper.getMatlabEditorApplication().openEditor(file);
    }

    public static MatlabEditorApplication getMatlabEditorApplication() {
        return MatlabEditorApplication.getInstance();
    }

    public static List<Editor> getOpenEditors() {
        return getMatlabEditorApplication().getOpenEditors();
    }

    /**
     * i'm lazy and i know it
     *
     * @return returns the active Editor Object
     */
    public static Editor gae() {
        return EditorWrapper.getActiveEditor();
    }

    public static Editor getActiveEditor() {
        return EditorApp.getInstance().getActiveEditor();
    }

    public static String getLongName(Editor editor) {
        return editor.getLongName();
    }

    public static String getShortName(Editor editor) {
        return editor.getShortName();
    }

    public static String getFullQualifiedClass(Editor editor) {
        String lName = EditorWrapper.getLongName(editor);
        lName = lName.replace("\\", ".");
        lName = lName.replace("/", ".");
        int start = lName.indexOf("+");
        if (start < 0) {
            start = lName.indexOf(EditorWrapper.getShortName(editor));
        }
        lName = lName.substring(start);
        lName = lName.replace("+", "");
        return lName.substring(0, lName.length() - 2);
    }

    public static InputMap getInputMap(Editor editor) {
        return editor.getTextComponent().getInputMap();
    }

    public static File getFile(Editor editor) {
        return new File(EditorWrapper.getLongName(editor));
    }

    public static String getText(Editor editor) {
        return editor.getText();
    }

    public static String getText(Editor editor, int start, int end) {
        String txt = EditorWrapper.getText(editor);
        if (start < 0) start = 0;
        if (end > txt.length() || end < 0) end = txt.length() - 1;
        if (end < start) return "";
        return txt.substring(start, end);
    }

    public static String getTextOffsetLength(Editor editor, int offset, int length) {
        try {
            return editor.getTextComponent().getText(offset, length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getTextArray(Editor editor) {
        String[] textArray = EditorWrapper.getText(editor).split("\\n");
        for (int i = 0; i < textArray.length; i++) {
            textArray[i] += "\n";
        }
        return textArray;
    }

    public static String[] getTextArrayFast(Editor editor) {
        String[] strings;
        if (editor == gae()) {
            strings = EditorWrapper.getActiveEditorTextArray();
        } else if (editor == lastEditor) {
            strings = EditorWrapper.getLastEditorTextArray();
        } else {
            EditorWrapper.setLastEditor(editor);
            strings = EditorWrapper.getLastEditorTextArray();
        }
        return strings;
    }

    public static String getSelectedTxt(Editor editor) {
        return editor.getSelection();
    }

    /**
     * converts position in line and column
     *
     * @return [line, column]
     */
    public static int[] pos2lc(Editor editor, int pos) {
        return editor.positionToLineAndColumn(pos);
    }

    public static int lc2pos(Editor editor, int line, int col) {
        int[] lc = EditorWrapper.fixLineCol(editor, line, col);
        line = lc[0] - 1;// line 1 is actually 0;
        col = lc[1];
        String[] strings = EditorWrapper.getTextArrayFast(editor);
        int p = 0;
        for (int i = 0; i < line; i++) {
            p += EditorWrapper.getLineLength(editor, i + 1);
        }
        col = col > strings[line].length() ? strings[line].length() : col;
        return p + col - 1;
    }

    public static void setSelectedTxt(Editor editor, String string) {
        editor.replaceText(string, EditorWrapper.getSelectionPositionStart(editor), EditorWrapper.getSelectionPositionEnd(editor));
    }

    public static int getCaretPosition(Editor editor) {
        return editor.getCaretPosition();
    }

    public static void setCaretPosition(Editor editor, int caretPosition) {
        editor.setCaretPosition(caretPosition);
    }

    /** @return [start, end] position */
    public static int[] getSelectionPosition(Editor editor) {
        return new int[]{EditorWrapper.getSelectionPositionStart(editor), EditorWrapper.getSelectionPositionEnd(editor)};
    }

    public static void setSelectionPosition(Editor editor, int start, int end) {
        editor.setSelection(start, end);
    }

    public static int getSelectionPositionEnd(Editor editor) {
        return editor.getTextComponent().getSelectionEnd();
    }

    public static void setSelectionPositionEnd(Editor editor, int end) {
        editor.getTextComponent().setSelectionEnd(end);
    }

    public static int getSelectionPositionStart(Editor editor) {
        return editor.getTextComponent().getSelectionStart();
    }

    public static void setSelectionPositionStart(Editor editor, int start) {
        editor.getTextComponent().setSelectionStart(start);
    }

    public static EditorSyntaxTextPane getEditorSyntaxTextPane(Editor editor) {
        return ComponentUtil.getEditorSyntaxTextPaneForEditor(editor);
    }

    public static void goToLine(Editor editor, int line, boolean select) {
        editor.goToLine(line, select);
    }

    /**
     * fixing line and number to active editor valid values
     *
     * @return fixed [line, column]
     */
    public static int[] fixLineCol(Editor editor, int line, int col) {
        String[] strings = EditorWrapper.getTextArrayFast(editor);
        if (line < 1) line = 1;
        if (line > strings.length) line = strings.length;
        if (col < 1) col = 1;
        if (col > strings[line - 1].length()) col = strings[line - 1].length() + 1;
        return new int[]{line, col};
    }

    public static int getLineLength(Editor editor, int line) {
        String[] strings = EditorWrapper.getTextArrayFast(editor);
        int[] lc = EditorWrapper.fixLineCol(editor, line, 1);
        return strings[lc[0] - 1].length(); // + 1 for \n (now \n is added at end)
    }

    public static void goToLineCol(Editor editor, int line, int col) {
        int[] lc = EditorWrapper.fixLineCol(editor, line, col);
        int pos = EditorWrapper.lc2pos(editor, lc[0], lc[1]);
        editor.goToPositionAndHighlight(pos, pos);
    }

    public static void selectLine(Editor editor, int line) {
        EditorWrapper.goToLine(editor, line, true);
    }

    public static void inserTextAtPos(Editor editor, String string, int pos) {
        EditorWrapper.setCaretPosition(editor, pos);
        editor.insertTextAtCaret(string);

    }

    /** will return all < <start, stop>, <start, stop> ...> positions of text found by expr */
    public static ArrayList<Integer> getTextPosByExpr(Editor editor, String expr, int startSearch, int stopSearch) {
        String text = EditorWrapper.getText(editor, startSearch, stopSearch);
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(text);

        ArrayList<Integer> idxs = new ArrayList<>(10);
        while (m.find()) {
            idxs.add(startSearch + m.start());
            idxs.add(startSearch + m.end());
        }
        idxs.trimToSize();
        return idxs;
    }

    /** returns all line numbers where a section is found including line 1 */
    public static int[] getSectionAllLines(Editor editor) {
        return CellUtils.getCellLocations(EditorWrapper.getText(editor));
    }

    /** @return [start, end] position of section surrounding the given position */
    public static int[] getSectionPosByPos(Editor editor, int pos) throws Exception {
        if (pos < 1) pos = 1;
        int[] sectionLines = EditorWrapper.getSectionAllLines(editor);
        int[] sectionPos = new int[sectionLines.length / 2];
        for (int i = 0; i < sectionLines.length; i++) {
            sectionPos[i] = EditorWrapper.lc2pos(editor, sectionLines[i], 1);
        }
        int start = -1;
        int end = -1;
        for (int i = 0; i < sectionLines.length; i++) {
            start = sectionPos[i];
            if (i < sectionLines.length - 1)
                end = sectionPos[i + 1];
            else
                end = EditorWrapper.lc2pos(editor, Integer.MAX_VALUE, 1);

            if (pos >= start && pos < end) {
                break;
            }
        }
        if (start == -1 || end == -1)
            throw new Exception("no section found - error in plugin code getSectionPosByPos");

        return new int[]{start, end};

    }

    /** @return [start, end] position of section surrounding the given line */
    public static int[] getSectionPosByLine(Editor editor, int line) throws Exception {
        return EditorWrapper.getSectionPosByPos(editor, EditorWrapper.lc2pos(editor, line, 1));
    }

    public static String getTextOfSectionByPos(Editor editor, int pos) throws Exception {
        int[] se = EditorWrapper.getSectionPosByPos(editor, pos);
        return EditorWrapper.getText(editor, se[0], se[1]);

    }

    public static int getCurrentLine(Editor editor) {
        int[] lc = EditorWrapper.pos2lc(editor, EditorWrapper.getCaretPosition(editor));
        return lc[0];
    }

    public static String getCurrentLineText(Editor editor) {
        return EditorWrapper.getTextByLine(editor, EditorWrapper.getCurrentLine(editor));
    }

    public static void deleteCurrentLine(Editor editor) {
        int line = EditorWrapper.getCurrentLine(editor);
        int[] se = new int[2];
        se[0] = EditorWrapper.lc2pos(editor, line, 0);
        se[1] = EditorWrapper.lc2pos(editor, line + 1, 0) + 1;

        EditorWrapper.setSelectionPosition(editor, se[0], se[1] - 1);
        EditorWrapper.setSelectedTxt(editor, "");
        EditorWrapper.goToLine(editor, line - 1, false);
    }

    public static void duplicateCurrentLine(Editor editor) {
        int line = EditorWrapper.getCurrentLine(editor);
        String lineStr = EditorWrapper.getTextByLine(editor, line);
        Matcher matcher = spaceStart.matcher(lineStr);
        if (matcher.find()) {
            int e = matcher.end();
            lineStr = lineStr.substring(e) + lineStr.substring(0, e);
        }
        EditorWrapper.goToLine(editor, line, false);
        EditorWrapper.setSelectedTxt(editor, lineStr);
        EditorWrapper.goToLine(editor, line + 1, false);
    }

    public static String getTextByLine(Editor editor, int line) {
        String[] strings;
        if (editor == gae()) {
            strings = getActiveEditorTextArray();
        } else {
            strings = EditorWrapper.getTextArray(editor);
        }
        if (line - 1 >= strings.length) return "";
        return strings[line - 1];
    }

    public static String[] getTextByLines(Editor editor, int[] lines) {
        String[] retString = new String[lines.length];

        int j = 0;
        for (int i : lines) {
            retString[j] = EditorWrapper.getTextByLine(editor, i);
            j++;
        }
        return retString;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////// Basically the same methods, but will use active editor automatically /////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static InputMap getInputMap() {
        return EditorWrapper.getInputMap(gae());
    }

    public static void deleteCurrentLine() {
        EditorWrapper.deleteCurrentLine(gae());
    }

    public static void duplicateCurrentLine() {
        EditorWrapper.duplicateCurrentLine(gae());
    }

    public static int lc2pos(int line, int col) {
        return EditorWrapper.lc2pos(gae(), line, col);
    }

    public static File getFile() {
        return EditorWrapper.getFile(gae());
    }

    public static int getCurrentLine() {
        return EditorWrapper.getCurrentLine(gae());
    }

    public static String getCurrentLineText() {
        return EditorWrapper.getCurrentLineText(gae());
    }

    public static void goToLine(int line, boolean select) {
        EditorWrapper.goToLine(gae(), line, select);
    }

    public static void setSelectionPosition(int start, int end) {
        EditorWrapper.setSelectionPosition(gae(), start, end);
    }

    public static String getShortName() {
        return EditorWrapper.getShortName(gae());
    }

    public static void setSelectedTxt(String string) {
        EditorWrapper.setSelectedTxt(gae(), string);
    }

    public static String getSelectedTxt() {
        return EditorWrapper.getSelectedTxt(gae());
    }

    public static String getFullQualifiedClass() {
        return EditorWrapper.getFullQualifiedClass(gae());
    }

    public static String getText() {
        return EditorWrapper.getText(gae());
    }

    public static int getSelectionPositionStart() {
        return EditorWrapper.getSelectionPositionStart(gae());
    }

    public static int[] pos2lc(int pos) {
        return EditorWrapper.pos2lc(gae(), pos);
    }

    public static void goToLineCol(int line, int col) {
        EditorWrapper.goToLineCol(gae(), line, col);
    }

    public static String getLongName() {
        return EditorWrapper.getLongName(gae());
    }

    public static String getTextByLine(int line) {
        return EditorWrapper.getTextByLine(gae(), line);
    }

    public static String getText(int start, int end) {
        return EditorWrapper.getText(gae(), start, end);
    }
}
