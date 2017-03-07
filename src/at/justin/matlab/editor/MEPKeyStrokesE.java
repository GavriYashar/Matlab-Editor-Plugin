package at.justin.matlab.editor;

import at.justin.matlab.MatlabKeyStrokesCommands;
import at.justin.matlab.util.KeyStrokeUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/** Created by Andreas Justin on 2016-10-12. */
public enum MEPKeyStrokesE {
    KS_MEP_DEBUG(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_E, true, true, false, false)),

    KS_MEP_EXECUTE_CURRENT_LINE(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_F9, false, true, false, false)),

    KS_MEP_SHOW_FILE_STRUCTURE(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_F12, true, false, false, false)),

    KS_MEP_SHOW_COPY_CLIP_BOARD(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_V, true, true, false, false)),
    KS_MEP_COPY_CLIP_BOARD(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_C, true, false, false, false)),

    KS_MEP_DELETE_CURRENT_LINES(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_Y, true, true, false, false)),
    KS_MEP_DUPLICATE_CURRENT_LINE(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_D, true, true, false, false)),

    KS_MEP_BOOKMARK(KeyStrokeUtil.getMatlabKeyStroke(MatlabKeyStrokesCommands.CTRL_PRESSED_F2)),
    KS_MEP_SHOW_BOOKMARKS(KeyStrokeUtil.getKeyStroke(
            KS_MEP_BOOKMARK.getKeyStroke().getKeyCode(),
            (KS_MEP_BOOKMARK.getKeyStroke().getModifiers() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK,
            (KS_MEP_BOOKMARK.getKeyStroke().getModifiers() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK,
            false,
            false)),
    KS_MEP_MEPR_INSERT(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_INSERT, false, false, true, false)),
    KS_MEP_MEPR_QUICK_SEARCH(KeyStrokeUtil.getKeyStroke(KeyEvent.VK_SPACE, true, false, true, false)),
    ;

    private final KeyStroke keyStroke;

    MEPKeyStrokesE(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

}
