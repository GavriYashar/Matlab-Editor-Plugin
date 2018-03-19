package at.mep.editor;

import at.mep.prefs.Settings;

import javax.swing.*;

public class CustomShortCutKey {
    private static KeyStroke DEBUG;

    private static KeyStroke FILE_STRUCTURE;
    private static KeyStroke COPY_SELECTED_TEXT;
    private static KeyStroke CUT_SELECTED_TEXT;
    private static KeyStroke CLIPBOARD_STACK;
    private static KeyStroke DUPLICATE_LINE;
    private static KeyStroke MOVE_LINE_UP;
    private static KeyStroke MOVE_LINE_DOWN;
    private static KeyStroke DELETE_LINES;
    private static KeyStroke LIVE_TEMPLATE_VIEWER;
    private static KeyStroke EXECUTE_CURRENT_LINES;
    private static KeyStroke TOGGLE_BOOKMARK;
    private static KeyStroke BOOKMARK_VIEWER;
    private static KeyStroke QUICK_SEARCH_MEPR;
    private static KeyStroke RECENTLY_CLOSED;

    public static void reload() {
        DEBUG = Settings.getPropertyKeyStroke("kb.DEBUG");

        FILE_STRUCTURE = Settings.getPropertyKeyStroke("kb.fileStructure");
        COPY_SELECTED_TEXT = Settings.getPropertyKeyStroke("kb.copySelectedText");
        CUT_SELECTED_TEXT = Settings.getPropertyKeyStroke("kb.cutSelectedText");
        CLIPBOARD_STACK = Settings.getPropertyKeyStroke("kb.clipboardStack");
        DUPLICATE_LINE = Settings.getPropertyKeyStroke("kb.duplicateLine");
        MOVE_LINE_UP = Settings.getPropertyKeyStroke("kb.moveLineUp");
        MOVE_LINE_DOWN = Settings.getPropertyKeyStroke("kb.moveLineDown");
        DELETE_LINES = Settings.getPropertyKeyStroke("kb.deleteLines");
        LIVE_TEMPLATE_VIEWER = Settings.getPropertyKeyStroke("kb.liveTemplateViewer");
        EXECUTE_CURRENT_LINES = Settings.getPropertyKeyStroke("kb.executeCurrentLine");
        TOGGLE_BOOKMARK = Settings.getPropertyKeyStroke("kb.toggleBookmark");
        BOOKMARK_VIEWER = Settings.getPropertyKeyStroke("kb.bookmarkViewer");
        QUICK_SEARCH_MEPR = Settings.getPropertyKeyStroke("kb.quickSearch");
        RECENTLY_CLOSED = Settings.getPropertyKeyStroke("kb.recentlyClosed");
    }

    public static KeyStroke getFileStructure() {
        return FILE_STRUCTURE;
    }

    public static KeyStroke getCopySelectedText() {
        return COPY_SELECTED_TEXT;
    }

    public static KeyStroke getCutSelectedText() {
        return CUT_SELECTED_TEXT;
    }

    public static KeyStroke getClipboardStack() {
        return CLIPBOARD_STACK;
    }

    public static KeyStroke getDuplicateLine() {
        return DUPLICATE_LINE;
    }

    public static KeyStroke getMoveLineUp() {
        return MOVE_LINE_UP;
    }

    public static KeyStroke getMoveLineDown() {
        return MOVE_LINE_DOWN;
    }

    public static KeyStroke getDeleteLines() {
        return DELETE_LINES;
    }

    public static KeyStroke getLiveTemplateViewer() {
        return LIVE_TEMPLATE_VIEWER;
    }

    public static KeyStroke getExecuteCurrentLines() {
        return EXECUTE_CURRENT_LINES;
    }

    public static KeyStroke getToggleBookmark() {
        return TOGGLE_BOOKMARK;
    }

    public static KeyStroke getBookmarkViewer() {
        return BOOKMARK_VIEWER;
    }

    public static KeyStroke getDEBUG() {
        return DEBUG;
    }

    public static KeyStroke getQuickSearchMepr() {
        return QUICK_SEARCH_MEPR;
    }

    public static KeyStroke getRecentlyClosed() {
        return RECENTLY_CLOSED;
    }
}
