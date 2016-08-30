package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.util.ColorUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-29.
 */
public class BookmarkCellRenderer extends DefaultListCellRenderer {
    Color fg;
    Color bg;
    Color fgLine;
    Color fgLink;

    public BookmarkCellRenderer() {
        fg = getForeground();
        bg = getBackground();
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLine = bg.darker().darker();
            fgLink = bg.darker();
        } else {
            fgLine = bg.brighter().brighter();
            fgLink = bg.brighter();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Bookmark bookmark = (Bookmark) value;
        setText("<HTML>"
                + "<font color=" + ColorUtils.colorToHex(fg) + "><b>" + bookmark.getName() + "</b></font> "
                + "<font color=" + ColorUtils.colorToHex(fg) + ">" + bookmark.getShortName() + "</font>:"
                + "<font color=" + ColorUtils.colorToHex(fgLine) + ">" + bookmark.getLine() + "</font>"
                + "<font color=" + ColorUtils.colorToHex(fgLink) + "> (" + bookmark.getLongName() + ")</font>"
                + "</HTML>");
        return c;
    }
}
