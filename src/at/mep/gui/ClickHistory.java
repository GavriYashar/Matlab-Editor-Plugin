package at.mep.gui;

import at.mep.editor.EditorWrapper;
import at.mep.prefs.Settings;
import com.mathworks.matlab.api.editor.Editor;

import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2017-10-18. */
public class ClickHistory {
    private static ClickHistory INSTANCE;
    private List<CHPair> history = new ArrayList<>(Settings.getPropertyInt("ch.sizeMax"));

    /** index for history user is currently visiting */
    private int currentlyOn = -1;

    private ClickHistory() {

    }

    public static ClickHistory getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ClickHistory();
        }
        return INSTANCE;
    }

    public void add(Editor editor) {
        int pos = EditorWrapper.getCaretPosition(editor);
        CHPair chPair = new CHPair(editor, pos);

        // don't add if the location currently, is the same as the one trying to be added
        if (currentlyOn >= 0 && history.get(currentlyOn).eq(chPair)) {
            return;
        }

        // if currently on is the last index, add new CHPair
        if (currentlyOn + 1 == history.size()) {
            history.add(chPair);
            currentlyOn = history.size() - 1;
            return;
        }

        // if currently on is somewhere in history, remove all history after currently on, then add CHPair
        if (currentlyOn + 1 < history.size()) {
            history = history.subList(0, currentlyOn);
            history.add(chPair);
            currentlyOn = history.size() - 1;
            return;
        }

        // trim list if
        trimList();
    }

    public void locationPrevious() {
        currentlyOn -= 1;
        if (currentlyOn < 0) {
            currentlyOn = 0;
        }
        if (history.size() < 1) {
            return;
        }
        CHPair chPair = history.get(currentlyOn);
        EditorWrapper.bringToFront(chPair.editor);
        Editor editor = EditorWrapper.goToPositionAndHighlight(chPair.editor, chPair.pos, chPair.pos);
        chPair.setEditor(editor);
    }

    public void locationNext() {
        currentlyOn += 1;
        if (currentlyOn >= history.size()) {
            currentlyOn = history.size() - 1;
        }
        CHPair chPair = history.get(currentlyOn);
        EditorWrapper.bringToFront(chPair.editor);
        Editor editor = EditorWrapper.goToPositionAndHighlight(chPair.editor, chPair.pos, chPair.pos);
        chPair.setEditor(editor);
    }

    private void trimList() {
        if (history.size() >= Settings.getPropertyInt("ch.sizeMax")) {
            history.remove(0);
            currentlyOn -= 1;
        }
    }

    public List<CHPair> getHistory() {
        return history;
    }

    private class CHPair {
        private Editor editor = null;
        private int pos = -1;

        public CHPair(Editor editor, int pos) {
            this.editor = editor;
            this.pos = pos;
        }

        public void setEditor(Editor editor) {
            this.editor = editor;
        }

        public Editor getEditor() {
            return editor;
        }

        public int getPos() {
            return pos;
        }

        public boolean eq(CHPair chPair) {
            return this.editor == chPair.editor && this.pos == chPair.pos;
        }
    }
}
