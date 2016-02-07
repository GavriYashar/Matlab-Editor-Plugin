package at.justin.matlab;

import com.mathworks.matlab.api.editor.Editor;

import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016 - 02 - 07.
 */
public class EditorWrapper {
    private static EditorWrapper INSTANCE;
    private static Pattern lineIsSection = Pattern.compile("^\\s*%%[\\s\\n\\r]");

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

    public String getShortName() {
        return gae().getShortName();
    }

    public String getLongName() {
        return gae().getLongName();
    }

    public int[] pos2lc(int pos) {
        return gae().positionToLineAndColumn(pos);
    }

    public int lc2pos(int line, int col) {
        line -= 1; // line 1 is actually 0;
        String[] strings = getTxtArray();
        int p = 0;
        for (int i = 0; i < line; i++) {
            p += getLineLength(i+1);
        }
        col = col > strings[line].length() ? strings[line].length() : col;
        return p + col - 1;
    }

    public int getLineLength(int line) {
        String[] strings = getTxtArray();
        return strings[line-1].length()+1; // for \n
    }

    public String getText() {
        return gae().getText();
    }

    public String getText(int start, int end) {
        String txt = getText();
        if (start < 0); start = 0;
        if (end > txt.length() || end < 0) end = txt.length()-1;
        return txt.substring(start,end);
    }

    public String getTextOffsetLenngth(int offset, int length) {
        try {
            return gae().getTextComponent().getText(offset,length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String[] getTxtArray() {
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

    public int[] getSelectionPosition() {
        return new int[] {getSelectionPositionStart(), getSelectionPositionEnd()};
    }

    public void setSelectionPosition(int s, int e) {
        gae().setSelection(s,e);
    }

    public int getSelectionPositionEnd() {
        return gae().getTextComponent().getSelectionEnd();
    }

    public int getSelectionPositionStart() {
        return gae().getTextComponent().getSelectionStart();
    }

    public void goToLine(int line) {
        gae().goToLine(line,false);
    }

    public void goToLineCol(int line, int col) {
        if (line < 1) line = 1;
        if (line > getTxtArray().length) line = getTxtArray().length;
        if (col < 1) col = 1;
        if (col > getLineLength(line)) col = getLineLength(line);
        gae().setCaretPosition(lc2pos(line, col));
    }

    public void selectLine(int line) {
        gae().goToLine(line,true);
    }

    public void isertTextAtPos(String txt, int i) {
        setCaretPosition(i);
        gae().insertTextAtCaret(txt);
    }

    public String getTextByLine(int line) {
        String[] strings = getTxtArray();
        return strings[line-1];
    }

    public String[] getTextByLines(int[] lines) {
        String[] strings = getTxtArray();
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
    public ArrayList<Integer> getAllLinesOfSections() {
        ArrayList<Integer> lines = new ArrayList<>(10);
        lines.add(1);
        String[] strings = getTxtArray();

        for (int i = 1; i < strings.length; i++) {
            Matcher m = lineIsSection.matcher(strings[i]);
            if (m.find()) {
                lines.add(i+1);
            }
        }

        lines.trimToSize();
        return lines;
    }

    public Component sandbox() {
        return gae().getComponent();
    }

}
