package at.mep.gui.bookmarks;

import at.mep.util.ColorUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-29.
 */
public class BookmarkCellRenderer extends DefaultListCellRenderer {
    static Color fg;
    static Color bg;
    static Color fgLineNS;
    static Color fgLinkNS;
    static Color fgLineS;
    static Color fgLinkS;

    public BookmarkCellRenderer() {
        fg = getForeground();
        bg = getBackground();
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineNS = bg.darker().darker();
            fgLinkNS = bg.darker();
        } else {
            fgLineNS = bg.brighter().brighter();
            fgLinkNS = bg.brighter();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (fgLineS == null && isSelected) setSelectionColors(getBackground());
        Bookmark bookmark = (Bookmark) value;
        String s = "";
        s += "<HTML>";
        s += "<b>" + bookmark.getName() + "</b></font> ";
        s += bookmark.getShortName() + ":";
        s += "<font color=" + (isSelected ? ColorUtils.colorToHex(fgLineS) : ColorUtils.colorToHex(fgLineNS)) + ">"
                + bookmark.getLine() + "</font>";
        s += "<font color=" + (isSelected ? ColorUtils.colorToHex(fgLinkS) : ColorUtils.colorToHex(fgLinkNS)) + ">"
                + " (" + bookmark.getLongName() + ") </font > ";
        s += "</HTML>";

        setText(s);
        return c;
    }

    private void setSelectionColors(Color bg) {
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineS = bg.darker().darker();
            fgLinkS = bg.darker();
        } else {
            fgLineS = bg.brighter().brighter();
            fgLinkS = bg.brighter();
        }
    }
}
