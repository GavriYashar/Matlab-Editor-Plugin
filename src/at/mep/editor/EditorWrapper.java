package at.mep.editor;

import at.mep.gui.fileStructure.NodeFS;
import at.mep.util.ComponentUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import com.mathworks.mde.editor.EditorView;
import com.mathworks.mde.editor.EditorViewClient;
import com.mathworks.mde.editor.MatlabEditorApplication;
import com.mathworks.mwswing.MJPopupMenu;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.desk.DTSingleClientFrame;
import com.mathworks.widgets.editor.breakpoints.BreakpointView;
import com.mathworks.widgets.text.mcode.MTree;
import com.mathworks.widgets.text.mcode.cell.CellUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
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

    private static MTree lastEditorMTree;
    private static MTree activeEditorMTree;

    private static NodeFS lastEditorClassNodeFS;
    private static NodeFS activeEditorClassNodeFS;

    private static NodeFS lastEditorFunctionNodeFS;
    private static NodeFS activeEditorFunctionNodeFS;

    private static NodeFS lastEditorSectionNodeFS;
    private static NodeFS activeEditorSectionNodeFS;


    /** if true textArray needs to be updated, fixing a huge performance issue */
    private static boolean isActiveEditorDirty = true;
    private static boolean isLastEditorDirty = true;

    // ////////////////////////////////////////////////////////////////////////////
    // //////////////// some performance optimization things /////////////////////
    // //////////////////////////////////////////////////////////////////////////
    public static boolean isActiveEditorDirty() {
        return EditorWrapper.isActiveEditorDirty;
    }

    public static void setLastEditor(Editor editor) {
        lastEditor = editor;
        isLastEditorDirty = true;
    }

    public static void setDirtyIfLastEditorChanged(Editor editor) {
        setIsLastEditorDirty(lastEditor != editor);
    }

    public static void setIsActiveEditorDirty(boolean isDirty) {
        EditorWrapper.isActiveEditorDirty = isDirty;
    }

    public static void setIsLastEditorDirty(boolean isDirty) {
        EditorWrapper.isLastEditorDirty = isDirty;
    }

    public static String[] getActiveEditorTextArray() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateFieldsForActiveEditor();
        }
        return EditorWrapper.activeEditorTextArray;
    }

    public static String[] getLastEditorTextArray() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateFieldsForLastEditor();
        }
        return EditorWrapper.lastEditorTextArray;
    }

    public static MTree getActiveEditorMTree() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateFieldsForActiveEditor();
        }
        return EditorWrapper.activeEditorMTree;
    }

    public static MTree getLastEditorMTree() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateFieldsForLastEditor();
        }
        return EditorWrapper.lastEditorMTree;
    }

    public static NodeFS getActiveEditorClassNodeFS() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateFieldsForActiveEditor();
        }
        return EditorWrapper.activeEditorClassNodeFS;
    }

    public static NodeFS getLastEditorClassNodeFS() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateFieldsForLastEditor();
        }
        return EditorWrapper.lastEditorClassNodeFS;
    }

    public static NodeFS getActiveEditorFunctionNodeFS() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateFieldsForActiveEditor();
        }
        return EditorWrapper.activeEditorFunctionNodeFS;
    }

    public static NodeFS getLastEditorFunctionNodeFS() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateFieldsForLastEditor();
        }
        return EditorWrapper.lastEditorFunctionNodeFS;
    }

    public static NodeFS getActiveEditorSectionNodeFS() {
        if (EditorWrapper.isActiveEditorDirty) {
            EditorWrapper.updateFieldsForActiveEditor();
        }
        return EditorWrapper.activeEditorSectionNodeFS;
    }

    public static NodeFS getLastEditorSectionNodeFS() {
        if (EditorWrapper.isLastEditorDirty) {
            EditorWrapper.updateFieldsForLastEditor();
        }
        return EditorWrapper.lastEditorSectionNodeFS;
    }

    public static void updateFieldsForActiveEditor() {
        setIsActiveEditorDirty(false); // if not in first line, it will enter an endless loop
        EditorWrapper.activeEditorTextArray = EditorWrapper.getTextArray(EditorWrapper.getActiveEditor());
        EditorWrapper.activeEditorMTree = EditorWrapper.getMTree(EditorWrapper.getActiveEditor());
        EditorWrapper.activeEditorClassNodeFS = EditorWrapper.getNodeClass(EditorWrapper.getActiveEditor());
        EditorWrapper.activeEditorFunctionNodeFS = EditorWrapper.getNodeFunction(EditorWrapper.getActiveEditor());
        EditorWrapper.activeEditorSectionNodeFS = EditorWrapper.getNodeSection(EditorWrapper.getActiveEditor());
    }

    public static void updateFieldsForLastEditor() {
        setIsLastEditorDirty(false); // if not in first line, it will enter an endless loop
        EditorWrapper.lastEditorTextArray = EditorWrapper.getTextArray(lastEditor);
        EditorWrapper.lastEditorMTree = EditorWrapper.getMTree(lastEditor);
        EditorWrapper.lastEditorClassNodeFS = EditorWrapper.getNodeClass(lastEditor);
        EditorWrapper.lastEditorFunctionNodeFS = EditorWrapper.getNodeFunction(lastEditor);
        EditorWrapper.lastEditorSectionNodeFS = EditorWrapper.getNodeSection(lastEditor);
    }

    // ////////////////////////////////////////////////////////////////////////////

    /**
     * returns true if editor is floating (DTSingelClientFrame)
     */
    public static boolean isFloating(Editor editor) {
        Component evc = editor.getComponent();
        if (evc instanceof EditorViewClient) {
            Component dtscf = ((EditorViewClient) evc).getTopLevelAncestor();
            return dtscf instanceof DTSingleClientFrame;
        }
        return false;
    }

    /** open editor of give java.io.File */
    public static Editor openEditor(File file) {
        return EditorWrapper.getMatlabEditorApplication().openEditor(file);
    }

    /** reopen an editor */
    public static Editor reopenEditor(Editor editor) {
        return EditorWrapper.openEditor(EditorWrapper.getFile(editor));
    }

    /** returns com.mathworks.mde.editor.MatlabEditorApplication */
    public static MatlabEditorApplication getMatlabEditorApplication() {
        return MatlabEditorApplication.getInstance();
    }

    /** returns java.util.List[Editor] of all opened editors */
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

    /** returns true if current editor is an LiveEditor */
    public static boolean isActiveEditorLive() {
        return LiveEditorWrapper.isActiveEditorLive();
    }

    /**
     * returns currently active Editor in Matlab.
     * if no editor is open or an LiveScript is active, active editor would be null.
     * tho prevent unwanted null pointer exceptions a new editor will be opened and returned instead
     */
    public static Editor getActiveEditorSafe() {
        Editor editor = EditorWrapper.getActiveEditor();
        if (editor == null) {
            editor = EditorWrapper.getMatlabEditorApplication().newEditor(
                    "MEP: Sorry!"
            );
        }
        return editor;
    }
    /** returns currently active Editor in Matlab */
    public static Editor getActiveEditor() {
        return EditorWrapper.getMatlabEditorApplication().getActiveEditor();
    }

    /** returns first non live editor, opens one if none exists */
    public static Editor getFirstNonLiveEditor() {
        List<Editor> editors = EditorWrapper.getOpenEditors();
        if (editors.size() == 0) {
            return EditorWrapper.getActiveEditorSafe();
        }
        return editors.get(0);
    }

    /** returns full qualified name of given editor */
    public static String getLongName(Editor editor) {
        return editor.getLongName();
    }

    /** returns file name of given editor */
    public static String getShortName(Editor editor) {
        return editor.getShortName();
    }

    /** returns full class name of given editor, if MFile is a class, otherwise just the filename will be returned */
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

    /** returns java .io.File of given editor */
    public static File getFile(Editor editor) {
        return new File(EditorWrapper.getLongName(editor));
    }

    /** returns containing text of given editor */
    public static String getText(Editor editor) {
        return editor.getText();
    }

    /** returns containing text of given editor for start and end position */
    public static String getText(Editor editor, int start, int end) {
        String txt = EditorWrapper.getText(editor);
        if (start < 0) start = 0;
        if (end > txt.length() || end < 0) end = txt.length() - 1;
        if (end < start) return "";
        return txt.substring(start, end);
    }

    /** returns List of Strings of found expr */
    public static List<String> getTextByExpr(Editor editor, String expr) {
        List<Integer> pos = EditorWrapper.getTextPosByExpr(editor, expr, 0, Integer.MAX_VALUE);
        List<String> strings = new ArrayList<>(pos.size() / 2);
        String string = EditorWrapper.getText(editor);
        for (int i = 0; i < pos.size(); i += 2) {
            strings.add(string.substring(pos.get(i), pos.get(i + 1)));
        }
        return strings;
    }

    /** returns containing text of given editor for an offset and length */
    public static String getTextOffsetLength(Editor editor, int offset, int length) {
        try {
            return editor.getTextComponent().getText(offset, length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** returns String[] of containing text for given editor, where each entry is represented by a line. !!reloads text on every call */
    public static String[] getTextArray(Editor editor) {
        String[] textArray = EditorWrapper.getText(editor).split("\\n", -1); // limit -1 to include trailing empty lines
        for (int i = 0; i < textArray.length; i++) {
            textArray[i] += "\n";
        }
        return textArray;
    }

    /** returns String[] of containing text for given editor, where each entry is represented by a line. !!reloads text only when changed */
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

    /** returns currently selected text of given editor */
    public static String getSelectedTxt(Editor editor) {
        return editor.getSelection();
    }

    /**
     * converts position to line and column for given editor. see position as an index of a 1D-Array of characters.
     *
     * @return [line, column]
     */
    public static int[] pos2lc(Editor editor, int pos) {
        return editor.positionToLineAndColumn(pos);
    }

    /** converts line and column to position for given editor. see position as an index of a 1D-Array of characters. */
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

    /** replaces selected text with given string for given editor */
    public static void setSelectedTxt(Editor editor, String string) {
        editor.replaceText(string, EditorWrapper.getSelectionPositionStart(editor), EditorWrapper.getSelectionPositionEnd(editor));
    }

    /** get position of caret of given editor */
    public static int getCaretPosition(Editor editor) {
        return editor.getCaretPosition();
    }

    /** sets the position of caret for given editor */
    public static void setCaretPosition(Editor editor, int caretPosition) {
        editor.setCaretPosition(caretPosition);
    }

    /** @return [start, end] position of given editor */
    public static int[] getSelectionPosition(Editor editor) {
        return new int[]{EditorWrapper.getSelectionPositionStart(editor), EditorWrapper.getSelectionPositionEnd(editor)};
    }

    /** sets selection position of given editor */
    public static void setSelectionPosition(Editor editor, int start, int end) {
        editor.setSelection(start, end);
    }

    /** returns the end position of current selection of given editor */
    public static int getSelectionPositionEnd(Editor editor) {
        return editor.getTextComponent().getSelectionEnd();
    }

    /** sets the end position of selection of given editor */
    public static void setSelectionPositionEnd(Editor editor, int end) {
        editor.getTextComponent().setSelectionEnd(end);
    }

    /** returns the start position of current selection of given editor */
    public static int getSelectionPositionStart(Editor editor) {
        return editor.getTextComponent().getSelectionStart();
    }

    /** sets the start position of current selection of given editor */
    public static void setSelectionPositionStart(Editor editor, int start) {
        editor.getTextComponent().setSelectionStart(start);
    }

    /** returns com.mathworks.mde.editor.EditorSyntaxTextPane */
    public static EditorSyntaxTextPane getEditorSyntaxTextPane(Editor editor) {
        return ComponentUtil.getEditorSyntaxTextPaneForEditor(editor);
    }

    public static BreakpointView.Background getBreakPointView(Editor editor) {
        return ComponentUtil.getBreakPointViewForEditor(editor);
    }

    public static EditorViewClient getEditorViewClient(Editor editor) {
        return (EditorViewClient) editor.getComponent();
    }

    public static EditorView getEditorView(Editor editor) {
        return EditorWrapper.getEditorViewClient(editor).getEditorView();
    }

    /** get's the context menu (popup menu / right click menu) of given editor */
    public static MJPopupMenu getContextMenu(Editor editor) {
        return EditorWrapper.getEditorView(editor).getSyntaxTextPane().getContextMenu();
    }

    /** sets given editor as active editor */
    public static void bringToFront(Editor editor) {
        editor.bringToFront();
    }

    /** moves caret to given line of given editor. if select flag is true, the current line will be selected */
    public static void goToLine(Editor editor, int line, boolean select) {
        editor.goToLine(line, select);
    }

    /**
     *  selects in given editor from start to end position, if editor is not open it will open the editor.
     *  returns same editor, or newly opened editor if editor was not open.
     */
    public static Editor goToPositionAndHighlight(Editor editor, int start, int end) {
        Editor editorOpen = editor;
        if (!EditorWrapper.isopen(editor)) {
            editorOpen = EditorWrapper.reopenEditor(editor);
        }
        editorOpen.goToPositionAndHighlight(start, end);
        return editorOpen;
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

    /** returns the length of given line of given editor */
    public static int getLineLength(Editor editor, int line) {
        String[] strings = EditorWrapper.getTextArrayFast(editor);
        int[] lc = EditorWrapper.fixLineCol(editor, line, 1);
        return strings[lc[0] - 1].length(); // + 1 for \n (now \n is added at end)
    }

    /** moves cursor to line and column of given editor */
    public static void goToLineCol(Editor editor, int line, int col) {
        int[] lc = EditorWrapper.fixLineCol(editor, line, col);
        int pos = EditorWrapper.lc2pos(editor, lc[0], lc[1]);
        editor.goToPositionAndHighlight(pos, pos);
    }

    /** selects given line of given editor */
    public static void selectLine(Editor editor, int line) {
        EditorWrapper.goToLine(editor, line, true);
    }

    /** string will be inserted at given position of given editor */
    public static void insertTextAtPos(Editor editor, String string, int pos) {
        EditorWrapper.setCaretPosition(editor, pos);
        editor.insertTextAtCaret(string);
    }

    /** will return all [ [start, stop], [start, stop] ...] positions of text found by expr */
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

    /** returns String of an entire section surrounding given position of given editor */
    public static String getTextOfSectionByPos(Editor editor, int pos) throws Exception {
        int[] se = EditorWrapper.getSectionPosByPos(editor, pos);
        return EditorWrapper.getText(editor, se[0], se[1]);

    }

    /** returns current line index of caret of given editor */
    public static int getCurrentLine(Editor editor) {
        int[] lc = EditorWrapper.pos2lc(editor, EditorWrapper.getCaretPosition(editor));
        return lc[0];
    }

    /** returns current line index of caret of given editor */
    public static int[] getCurrentLinesStartEnd(Editor editor) {
        int[] lcS = EditorWrapper.pos2lc(editor, EditorWrapper.getSelectionPositionStart(editor));
        int[] lcE = EditorWrapper.pos2lc(editor, EditorWrapper.getSelectionPositionEnd(editor));
        return new int[] {lcS[0], lcE[0]};
    }

    /** returns text of current line of given editor */
    public static String getCurrentLineText(Editor editor) {
        return EditorWrapper.getTextByLine(editor, EditorWrapper.getCurrentLine(editor));
    }

    /** deletes current line if no text is selected or all the lines of selection is some text is selected */
    public static void deleteCurrentLines(Editor editor) {
        int[] se = getSelectionPosition(editor);
        int[] lcStart = EditorWrapper.pos2lc(editor, se[0]);
        int[] lcEnd = EditorWrapper.pos2lc(editor, se[1]);

        if (lcEnd[1] == 1 && lcEnd[0] != lcStart[0]) { // don't delete last line of selection if no characters are selected on it
            lcEnd[0]--;
        }

        se[0] = EditorWrapper.lc2pos(editor, lcStart[0], 0);
        se[1] = EditorWrapper.lc2pos(editor, lcEnd[0] + 1, 0) + 1;

        EditorWrapper.setSelectionPosition(editor, se[0], se[1] - 1);
        EditorWrapper.setSelectedTxt(editor, "");
    }

    /** deletes current line of given editor */
    public static void deleteCurrentLine(Editor editor) {
        int line = EditorWrapper.getCurrentLine(editor);
        int[] se = new int[2];
        se[0] = EditorWrapper.lc2pos(editor, line, 0);
        se[1] = EditorWrapper.lc2pos(editor, line + 1, 0) + 1;

        EditorWrapper.setSelectionPosition(editor, se[0], se[1] - 1);
        EditorWrapper.setSelectedTxt(editor, "");
    }

    /** duplicates
     *  - current line if no text is selected
     *  - selection content if some text is selected
     */
    public static void duplicateCurrentLineOrSelection(Editor editor) {
        int[] se = getSelectionPosition(editor);
        if (se[0] == se[1]) {
            duplicateCurrentLine(editor);
        } else {
            String selectedText = EditorWrapper.getSelectedTxt(editor);
            selectedText += selectedText;
            EditorWrapper.setSelectedTxt(editor, selectedText);
            EditorWrapper.setSelectionPosition(editor, se[1], se[1] + se[1] - se[0]);
        }

    }

    /** duplicates current line of given editor */
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

    /** returns text of entire line of given editor */
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

    /** returns String[] of lines as int[] of given editor */
    public static String[] getTextByLines(Editor editor, int[] lines) {
        String[] retString = new String[lines.length];

        int j = 0;
        for (int i : lines) {
            retString[j] = EditorWrapper.getTextByLine(editor, i);
            j++;
        }
        return retString;
    }

    public static MTree getMTree(Editor editor) {
        return MTree.parse(editor.getText());
    }

    public static NodeFS getNodeClass(Editor editor) {
        NodeFS nodeFS = new NodeFS("No Class NodeFS: Or No Class");
        if (EditorWrapper.getFile(editor).exists()) {
            nodeFS = NodeFS.constructForClassDef(editor, false);
        }
        return nodeFS;
    }

    public static NodeFS getNodeFunction(Editor editor) {
        return NodeFS.constructForFunctions(editor);
    }

    public static NodeFS getNodeSection(Editor editor) {
        return NodeFS.constructForCellTitle(editor);
    }

    public static Tree<MTree.Node> getTreeFunction(Editor editor) {
        MTree mTree = EditorWrapper.getMTreeFast(editor);
        return mTree.findAsTree(MTree.NodeType.FUNCTION);
    }

    public static Tree<MTree.Node> getTreeSection(Editor editor) {
        MTree mTree = EditorWrapper.getMTreeFast(editor);
        return mTree.findAsTree(MTree.NodeType.CELL_TITLE);
    }

    public static MTree getMTreeFast(Editor editor) {
        MTree mTree;
        if (editor == gae()) {
            mTree = EditorWrapper.getActiveEditorMTree();
        } else if (editor == lastEditor) {
            mTree = EditorWrapper.getLastEditorMTree();
        } else {
            EditorWrapper.setLastEditor(editor);
            mTree = EditorWrapper.getLastEditorMTree();
        }
        return mTree;
    }

    public static NodeFS getClassNodeFast(Editor editor) {
        NodeFS nodeFS;
        if (editor == gae()) {
            nodeFS = EditorWrapper.getActiveEditorClassNodeFS();
        } else if (editor == lastEditor) {
            nodeFS = EditorWrapper.getLastEditorClassNodeFS();
        } else {
            EditorWrapper.setLastEditor(editor);
            nodeFS = EditorWrapper.getLastEditorClassNodeFS();
        }
        return nodeFS;
    }

    public static NodeFS getFunctionNodeFast(Editor editor) {
        NodeFS nodeFS;
        if (editor == gae()) {
            nodeFS = EditorWrapper.getActiveEditorFunctionNodeFS();
        } else if (editor == lastEditor) {
            nodeFS = EditorWrapper.getLastEditorFunctionNodeFS();
        } else {
            EditorWrapper.setLastEditor(editor);
            nodeFS = EditorWrapper.getLastEditorFunctionNodeFS();
        }
        return nodeFS;
    }

    public static NodeFS getSectionNodeFast(Editor editor) {
        NodeFS nodeFS;
        if (editor == gae()) {
            nodeFS = EditorWrapper.getActiveEditorSectionNodeFS();
        } else if (editor == lastEditor) {
            nodeFS = EditorWrapper.getLastEditorSectionNodeFS();
        } else {
            EditorWrapper.setLastEditor(editor);
            nodeFS = EditorWrapper.getLastEditorSectionNodeFS();
        }
        return nodeFS;
    }

    public static boolean isopen(Editor editor) {
        return editor.isOpen();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////// Basically the same methods, but will use active editor automatically /////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static MTree getMTree() {
        return EditorWrapper.getMTree(gae());
    }

    public static InputMap getInputMap() {
        if (EditorWrapper.isActiveEditorLive()) {
            return LiveEditorWrapper.getInputMap();
        }
        return EditorWrapper.getInputMap(gae());
    }

    public static void deleteCurrentLines() {
        EditorWrapper.deleteCurrentLines(gae());
    }

    @Deprecated
    public static void deleteCurrentLine() {
        EditorWrapper.deleteCurrentLine(gae());
    }

    public static void duplicateCurrentLineOrSelection() {
        EditorWrapper.duplicateCurrentLineOrSelection(gae());
    }

    @Deprecated
    public static void duplicateCurrentLine() {
        EditorWrapper.duplicateCurrentLine(gae());
    }

    public static int lc2pos(int line, int col) {
        return EditorWrapper.lc2pos(gae(), line, col);
    }

    public static File getFile() {
        if (EditorWrapper.isActiveEditorLive()) {
            return new File(LiveEditorWrapper.getLongName());
        }
        return EditorWrapper.getFile(gae());
    }

    public static int getCurrentLine() {
        return EditorWrapper.getCurrentLine(gae());
    }

    public static int[] getCurrentLinesStartEnd() {
        return EditorWrapper.getCurrentLinesStartEnd(gae());
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
        if (EditorWrapper.isActiveEditorLive()) {
            return LiveEditorWrapper.getShortName();
        }
        return EditorWrapper.getShortName(gae());
    }

    public static int getCaretPosition() {
        return EditorWrapper.getCaretPosition(gae());
    }

    public static void insertTextAtPos(String string, int pos) {
        Editor editor = gae();
        EditorWrapper.setCaretPosition(gae(), pos);
        editor.insertTextAtCaret(string);
    }

    public static String getSelectedTxt() {
        return EditorWrapper.getSelectedTxt(gae());
    }

    public static void setSelectedTxt(String string) {
        EditorWrapper.setSelectedTxt(gae(), string);
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
        if (EditorWrapper.isActiveEditorLive()) {
            return LiveEditorWrapper.getLongName();
        }
        return EditorWrapper.getLongName(gae());
    }

    public static String getTextByLine(int line) {
        return EditorWrapper.getTextByLine(gae(), line);
    }

    public static String getText(int start, int end) {
        return EditorWrapper.getText(gae(), start, end);
    }

    public static int getSelectionPositionEnd() {
        return EditorWrapper.getSelectionPositionEnd(gae());
    }

    public static EditorSyntaxTextPane getEditorSyntaxTextPane() {
        return EditorWrapper.getEditorSyntaxTextPane(gae());
    }

    public static EditorViewClient getEditorViewClient() {
        return EditorWrapper.getEditorViewClient(gae());
    }

    public static EditorView getEditorView() {
        return EditorWrapper.getEditorView(gae());
    }

    public static MJPopupMenu getContextMenu() {
        return EditorWrapper.getContextMenu(gae());
    }

    public static boolean isFloating() {
        return EditorWrapper.isFloating(gae());
    }
}
