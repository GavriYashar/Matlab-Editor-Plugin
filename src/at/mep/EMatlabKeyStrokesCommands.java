package at.mep;


import at.mep.editor.EditorWrapper;

import javax.swing.*;

/** Created by Andreas Justin on 2016-08-25. */
public enum EMatlabKeyStrokesCommands {
    ALT_PRESSED_ENTER("alt pressed ENTER", "mlint-fix-action", ""),
    ALT_PRESSED_KP_DOWN("alt pressed KP_DOWN", "next-message", ""),
    ALT_PRESSED_KP_UP("alt pressed KP_UP", "previous-message", ""),
    ALT_PRESSED_UP("alt pressed UP", "previous-message", ""),
    CTRL_PRESSED_A("ctrl pressed A", "select-all", ""),
    CTRL_PRESSED_ADD("ctrl pressed ADD", "codepad-plus", ""),
    CTRL_PRESSED_BACK_SPACE("ctrl pressed BACK_SPACE", "remove-word-previous", ""),
    CTRL_PRESSED_C("ctrl pressed C", "copy-to-clipboard", ""),
    CTRL_PRESSED_CLOSE_BRACKET("ctrl pressed CLOSE_BRACKET", "shift-line-right", ""),
    CTRL_PRESSED_D("ctrl pressed D", "open-selection", ""),
    CTRL_PRESSED_DELETE("ctrl pressed DELETE", "remove-word-next", ""),
    CTRL_PRESSED_DIVIDE("ctrl pressed DIVIDE", "codepad-divide", ""),
    CTRL_PRESSED_DOWN("ctrl pressed DOWN", "next-cell", ""),
    CTRL_PRESSED_END("ctrl pressed END", "caret-end", ""),
    CTRL_PRESSED_ENTER("ctrl pressed ENTER", "eval-cell", ""),
    CTRL_PRESSED_F("ctrl pressed F", "find-and-replace", ""),
    CTRL_PRESSED_F1("ctrl pressed F1", "function-hints-key", ""),
    CTRL_PRESSED_F2("ctrl pressed F2", "toggle-bookmark", ""),
    CTRL_PRESSED_F3("ctrl pressed F3", "find-selection", ""),
    CTRL_PRESSED_F5("ctrl pressed F5", "pause-execution", ""),
    CTRL_PRESSED_G("ctrl pressed G", "goto-line", ""),
    CTRL_PRESSED_H("ctrl pressed H", "find-and-replace", ""),
    CTRL_PRESSED_HOME("ctrl pressed HOME", "caret-begin", ""),
    CTRL_PRESSED_I("ctrl pressed I", "format", ""),
    CTRL_PRESSED_INSERT("ctrl pressed INSERT", "copy-to-clipboard", ""),
    CTRL_PRESSED_J("ctrl pressed J", "merge-comments", ""),
    CTRL_PRESSED_KP_DOWN("ctrl pressed KP_DOWN", "next-cell", ""),
    CTRL_PRESSED_KP_LEFT("ctrl pressed KP_LEFT", "caret-previous-word", ""),
    CTRL_PRESSED_KP_RIGHT("ctrl pressed KP_RIGHT", "caret-next-word", ""),
    CTRL_PRESSED_KP_UP("ctrl pressed KP_UP", "prev-cell", ""),
    CTRL_PRESSED_LEFT("ctrl pressed LEFT", "caret-previous-word", ""),
    CTRL_PRESSED_M("ctrl pressed M", "open-mlint-message", ""),
    CTRL_PRESSED_MULTIPLY("ctrl pressed MULTIPLY", "codepad-multiply", ""),
    CTRL_PRESSED_N("ctrl pressed N", "new-mfile", ""),
    CTRL_PRESSED_O("ctrl pressed O", "open-new", ""),
    CTRL_PRESSED_OPEN_BRACKET("ctrl pressed OPEN_BRACKET", "shift-line-left", ""),
    CTRL_PRESSED_P("ctrl pressed P", "print", ""),
    CTRL_PRESSED_PERIOD("ctrl pressed PERIOD", "collapse-fold", ""),
    CTRL_PRESSED_R("ctrl pressed R", "comment", ""),
    CTRL_PRESSED_RIGHT("ctrl pressed RIGHT", "caret-next-word", ""),
    CTRL_PRESSED_S("ctrl pressed S", "save", ""),
    CTRL_PRESSED_SPACE("ctrl pressed SPACE", "insert-tab", ""),
    CTRL_PRESSED_SUBTRACT("ctrl pressed SUBTRACT", "codepad-minus", ""),
    CTRL_PRESSED_T("ctrl pressed T", "uncomment", ""),
    CTRL_PRESSED_UP("ctrl pressed UP", "prev-cell", ""),
    CTRL_PRESSED_V("ctrl pressed V", "paste-from-clipboard", ""),
    CTRL_PRESSED_X("ctrl pressed X", "cut-to-clipboard", ""),
    CTRL_PRESSED_Y("ctrl pressed Y", "redo", ""),
    CTRL_PRESSED_Z("ctrl pressed Z", "undo", ""),
    META_PRESSED_BACK_SPACE("meta pressed BACK_SPACE", "undo", ""),
    META_PRESSED_DOWN("meta pressed DOWN", "next-message", ""),
    META_PRESSED_ENTER("meta pressed ENTER", "mlint-fix-action", ""),
    META_PRESSED_KP_DOWN("meta pressed KP_DOWN", "next-message", ""),
    META_PRESSED_KP_UP("meta pressed KP_UP", "previous-message", ""),
    META_PRESSED_UP("meta pressed UP", "previous-message", ""),
    PRESSED_BACK_SPACE("pressed BACK_SPACE", "delete-previous", ""),
    PRESSED_DELETE("pressed DELETE", "delete-next", ""),
    PRESSED_DOWN("pressed DOWN", "caret-down", ""),
    PRESSED_END("pressed END", "caret-end-line", ""),
    PRESSED_ENTER("pressed ENTER", "insert-break", ""),
    PRESSED_ESCAPE("pressed ESCAPE", "unselect", ""),
    PRESSED_F1("pressed F1", "help-on-selection", ""),
    PRESSED_F10("pressed F10", "debug-step", ""),
    PRESSED_F11("pressed F11", "debug-step-in", ""),
    PRESSED_F12("pressed F12", "set-clear-breakpoint", ""),
    PRESSED_F2("pressed F2", "next-bookmark", ""),
    PRESSED_F3("pressed F3", "find-next", ""),
    PRESSED_F5("pressed F5", "debug-continue", ""),
    PRESSED_F6("pressed F6", "split-screen-switch-focus", ""),
    PRESSED_F9("pressed F9", "evaluate-selection", ""),
    PRESSED_HOME("pressed HOME", "caret-begin-line", ""),
    PRESSED_KP_DOWN("pressed KP_DOWN", "caret-down", ""),
    PRESSED_KP_LEFT("pressed KP_LEFT", "caret-backward", ""),
    PRESSED_KP_RIGHT("pressed KP_RIGHT", "caret-forward", ""),
    PRESSED_KP_UP("pressed KP_UP", "caret-up", ""),
    PRESSED_LEFT("pressed LEFT", "caret-backward", ""),
    PRESSED_PAGE_DOWN("pressed PAGE_DOWN", "page-down", ""),
    PRESSED_PAGE_UP("pressed PAGE_UP", "page-up", ""),
    PRESSED_RIGHT("pressed RIGHT", "caret-forward", ""),
    PRESSED_TAB("pressed TAB", "insert-tab", ""),
    PRESSED_UP("pressed UP", "caret-up", ""),
    SHIFT_ALT_PRESSED_BACK_SPACE("shift alt pressed BACK_SPACE", "redo", ""),
    SHIFT_CTRL_PRESSED_ADD("shift ctrl pressed ADD", "expand-all-folds", ""),
    SHIFT_CTRL_PRESSED_DIVIDE("shift ctrl pressed DIVIDE", "codepad-decrease-mult-increment", ""),
    SHIFT_CTRL_PRESSED_END("shift ctrl pressed END", "selection-end", ""),
    SHIFT_CTRL_PRESSED_ENTER("shift ctrl pressed ENTER", "eval-cell-and-advance", ""),
    SHIFT_CTRL_PRESSED_F("shift ctrl pressed F", "find-files", ""),
    SHIFT_CTRL_PRESSED_F3("shift ctrl pressed F3", "find-previous-selection", ""),
    SHIFT_CTRL_PRESSED_H("shift ctrl pressed H", "highlight-variable-usages", ""),
    SHIFT_CTRL_PRESSED_HOME("shift ctrl pressed HOME", "selection-begin", ""),
    SHIFT_CTRL_PRESSED_KP_LEFT("shift ctrl pressed KP_LEFT", "selection-previous-word", ""),
    SHIFT_CTRL_PRESSED_KP_RIGHT("shift ctrl pressed KP_RIGHT", "selection-next-word", ""),
    SHIFT_CTRL_PRESSED_LEFT("shift ctrl pressed LEFT", "selection-previous-word", ""),
    SHIFT_CTRL_PRESSED_MULTIPLY("shift ctrl pressed MULTIPLY", "codepad-increase-mult-increment", ""),
    SHIFT_CTRL_PRESSED_PERIOD("shift ctrl pressed PERIOD", "expand-fold", ""),
    SHIFT_CTRL_PRESSED_R("shift ctrl pressed R", "inc-search-backward", ""),
    SHIFT_CTRL_PRESSED_RIGHT("shift ctrl pressed RIGHT", "selection-next-word", ""),
    SHIFT_CTRL_PRESSED_S("shift ctrl pressed S", "inc-search-forward", ""),
    SHIFT_CTRL_PRESSED_SUBTRACT("shift ctrl pressed SUBTRACT", "collapse-all-folds", ""),
    SHIFT_CTRL_PRESSED_Z("shift ctrl pressed Z", "redo", ""),
    SHIFT_META_PRESSED_BACK_SPACE("shift meta pressed BACK_SPACE", "redo", ""),
    SHIFT_PRESSED_BACK_SPACE("shift pressed BACK_SPACE", "delete-previous", ""),
    SHIFT_PRESSED_DELETE("shift pressed DELETE", "cut-to-clipboard", ""),
    SHIFT_PRESSED_DOWN("shift pressed DOWN", "selection-down", ""),
    SHIFT_PRESSED_END("shift pressed END", "selection-end-line", ""),
    SHIFT_PRESSED_ENTER("shift pressed ENTER", "shift-insert-break", ""),
    SHIFT_PRESSED_F1("shift pressed F1", "function-browser-key", ""),
    SHIFT_PRESSED_F11("shift pressed F11", "debug-step-out", ""),
    SHIFT_PRESSED_F2("shift pressed F2", "prev-bookmark", ""),
    SHIFT_PRESSED_F3("shift pressed F3", "find-previous", ""),
    SHIFT_PRESSED_F5("shift pressed F5", "exit-debug", ""),
    SHIFT_PRESSED_HOME("shift pressed HOME", "selection-begin-line", ""),
    SHIFT_PRESSED_INSERT("shift pressed INSERT", "paste-from-clipboard", ""),
    SHIFT_PRESSED_KP_DOWN("shift pressed KP_DOWN", "selection-down", ""),
    SHIFT_PRESSED_KP_LEFT("shift pressed KP_LEFT", "selection-backward", ""),
    SHIFT_PRESSED_KP_RIGHT("shift pressed KP_RIGHT", "selection-forward", ""),
    SHIFT_PRESSED_KP_UP("shift pressed KP_UP", "selection-up", ""),
    SHIFT_PRESSED_LEFT("shift pressed LEFT", "selection-backward", ""),
    SHIFT_PRESSED_PAGE_DOWN("shift pressed PAGE_DOWN", "selection-page-down", ""),
    SHIFT_PRESSED_PAGE_UP("shift pressed PAGE_UP", "selection-page-up", ""),
    SHIFT_PRESSED_RIGHT("shift pressed RIGHT", "selection-forward", ""),
    SHIFT_PRESSED_TAB("shift pressed TAB", "shift-tab-pressed", ""),
    SHIFT_PRESSED_UP("shift pressed UP", "selection-up", "");

    private final String defaultKeyStroke;
    private final String command;
    private String customKeyStroke;


    EMatlabKeyStrokesCommands(String defaultKeyStroke, String command, String customKeyStroke) {
        this.defaultKeyStroke = defaultKeyStroke;
        this.command = command;
        this.customKeyStroke = customKeyStroke;
    }

    public String getCustomKeyStroke() {
        return customKeyStroke;
    }

    private void setCustomKeyStroke(String customKeyStroke) {
        this.customKeyStroke = customKeyStroke;
    }

    public static void setCustomKeyStrokes() {
        EMatlabKeyStrokesCommands[] list = EMatlabKeyStrokesCommands.values();
        InputMap inputMap = EditorWrapper.getInputMap(EditorWrapper.getFirstNonLiveEditor());
        KeyStroke[] keyStrokes = inputMap.allKeys();

        for (EMatlabKeyStrokesCommands commands : list) {
            for (int i = 0; i < keyStrokes.length; i++) {
                KeyStroke keyStroke = keyStrokes[i];
                if (commands.getCommand().toLowerCase().equals(inputMap.get(keyStroke).toString().toLowerCase())) {
                    commands.setCustomKeyStroke(keyStroke.toString());
                }
            }
        }

    }

    public String getDefaultKeyStroke() {
        return defaultKeyStroke;
    }

    public String getCommand() {
        return this.command;
    }
}
