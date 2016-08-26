package at.justin.matlab;

import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.widgets.text.mcode.cell.CellUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016 - 02 - 07.
 */
public class EditorWrapper {
    private static EditorWrapper INSTANCE;

    public static EditorWrapper getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new EditorWrapper();
        return INSTANCE;
    }

    /**
     * i'm lazy and i know it
     * @return returns the active Editor Object
     */
    public Editor gae() {
        return EditorApp.getInstance().getActiveEditor();
    }

    public InputMap getInputMap() {
        return gae().getTextComponent().getInputMap();
    }

    public String getShortName() {
        return gae().getShortName();
    }

    public String getLongName() {
        return gae().getLongName();
    }

    public File getFile() {
        return new File(getLongName());
    }

    /**
     * converts position in line and column
     * @param pos
     * @return [line, column]
     */
    public int[] pos2lc(int pos) {
        return gae().positionToLineAndColumn(pos);
    }

    /**
     * converts line an column to position
     * @param line line number
     * @param col column
     * @return position of line and column
     */
    public int lc2pos(int line, int col) {
        int[] lc = fixLineCol(line, col);
        line = lc[0]-1;// line 1 is actually 0;
        col = lc[1];
        String[] strings = getTextArray();
        int p = 0;
        for (int i = 0; i < line; i++) {
            p += getLineLength(i+1);
        }
        col = col > strings[line].length() ? strings[line].length() : col;
        return p + col - 1;
    }

    public int getLineLength(int line) {
        String[] strings = getTextArray();
        int[] lc = fixLineCol(line,1);
        return strings[lc[0]-1].length()+1; // for \n
    }

    public String getText() {
        return gae().getText();
    }

    public String getText(int start, int end) {
        String txt = getText();
        if (start < 0) start = 0;
        if (end > txt.length() || end < 0) end = txt.length()-1;
        return txt.substring(start,end);
    }

    public String getTextOffsetLength(int offset, int length) {
        try {
            return gae().getTextComponent().getText(offset,length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Splits getText(). lines, at the end w/o any character except new line will be truncated at not returned
     * @return string []
     */
    public String[] getTextArray() {
        String s = gae().getText();
        return s.split("\\n");
    }

    public String getSelectedTxt() {
        return gae().getSelection();
    }

    public void setSelectedTxt(String s) {
        gae().replaceText(s,getSelectionPositionStart(),getSelectionPositionEnd());
    }

    public int getCaretPosition() {
        return gae().getCaretPosition();
    }

    public void setCaretPosition(int i) {
        gae().setCaretPosition(i);
    }

    /**
     * @return [start, end] position
     */
    public int[] getSelectionPosition() {
        return new int[] {getSelectionPositionStart(), getSelectionPositionEnd()};
    }

    public void setSelectionPosition(int s, int e) {
        gae().setSelection(s,e);
    }

    public int getSelectionPositionEnd() {
        return gae().getTextComponent().getSelectionEnd();
    }

    public void setSelectionPositionStart(int s) {
        gae().getTextComponent().setSelectionStart(s);
    }
    public void setSelectionPositionEnd(int e) {
        gae().getTextComponent().setSelectionStart(e);
    }

    public int getSelectionPositionStart() {
        return gae().getTextComponent().getSelectionStart();
    }

    /**
     *
     * @param line   line number
     * @param select boolean
     */
    public void goToLine(int line,boolean select) {
        gae().goToLine(line,select);
    }

    public void goToLineCol(int line, int col) {
        int[] lc = fixLineCol(line,col);
        int pos = lc2pos(lc[0], lc[1]);
        gae().goToPositionAndHighlight(pos,pos);
    }

    public void selectLine(int line) {
        gae().goToLine(line,true);
    }

    public void insertTextAtPos(String txt, int i) {
        setCaretPosition(i);
        gae().insertTextAtCaret(txt);
    }

    public String getTextByLine(int line) {
        String[] strings = getTextArray();
        return strings[line-1];
    }

    public String[] getTextByLines(int[] lines) {
        String[] strings = getTextArray();
        String[] retString = new String[lines.length];

        int j = 0;
        for (int i: lines) {
            retString[j] = strings[i-1];
            j++;
        }
        return retString;
    }

    /**
     * will return all < <start, stop>, <start, stop> ...> positions of text found by expr
     * @param expr  valid regular expression
     * @param startSearch index where to begin
     * @param stopSearch index where to end
     * @return
     */
    public ArrayList<Integer> getTextPosByExpr(String expr, int startSearch, int stopSearch) {
        String text = getText(startSearch,stopSearch);
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

    /**
     * returns all line numbers where a section is found including line 1
     * @return line numbers array
     */
    public int[] getSectionAllLines() {
        return CellUtils.getCellLocations(getText());
    }

    /**
     *
     * @param pos position
     * @return [start, end] position of section surrounding the given position
     * @throws Exception
     */
    public int[] getSectionPosByPos(int pos) throws Exception {
        if (pos < 1) pos = 1;
        int[] sectionLines = getSectionAllLines();
        int[] sectionPos = new int[sectionLines.length / 2];
        for (int i = 0; i < sectionLines.length; i++) {
            sectionPos[i] = lc2pos(sectionLines[i], 1);
        }
        int start = -1;
        int end = -1;
        for (int i = 0; i < sectionLines.length; i++) {
            start = sectionPos[i];
            if (i < sectionLines.length-1)
                end = sectionPos[i+1];
            else
                end = lc2pos(Integer.MAX_VALUE,1);

            if (pos >= start && pos < end) {
                break;
            }
        }
        if (start == -1 || end == -1)
            throw new Exception("no section found - error in plugin code getSectionPosByPos");

        return new int[] {start,end};
    }

    /**
     *
     * @param line line number
     * @return [start, end] position of section surrounding the given line
     * @throws Exception
     */
    public int[] getSectionPosByLine(int line) throws Exception {
        return getSectionPosByPos(lc2pos(line,1));
    }

    public String getTextOfSectionByPos(int pos) throws Exception {
        int[] se = getSectionPosByPos(pos);
        return getText(se[0],se[1]);
    }

    /**
     * fixing line and number to active editor valid values
     * @param line line number
     * @param col column
     * @return fixed [line, column]
     */
    private int[] fixLineCol(int line, int col) {
        String[] strings = getTextArray();
        if (line < 1) line = 1;
        if (line > strings.length) line = strings.length;
        if (col < 1) col = 1;
        if (col > strings[line-1].length()) col = strings[line-1].length()+1;
        return new int[] {line,col};
    }

    public Component sandbox() {
        return gae().getComponent();
    }

    public int getCurrentLine() {
        int[] lc = pos2lc(getCaretPosition());
        return lc[0];
    }
    public String getCurrentLineText() {
        return getTextByLine(getCurrentLine());
    }
}
