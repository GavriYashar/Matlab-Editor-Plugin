package at.justin.matlab.util;

import at.justin.matlab.editor.EditorWrapper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/** Created by Andreas Justin on 2016-09-28. */
public class ClipboardUtil {
    public static void addToClipboard(String string) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(string), null);
    }
}
