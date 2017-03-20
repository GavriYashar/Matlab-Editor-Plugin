package at.mep.gui.mepr;

import at.mep.util.ColorUtils;

import javax.swing.*;
import java.awt.*;

/** Created by Andreas Justin on 2016-09-20. */
public class MEPRCellRenderer extends DefaultListCellRenderer {
    private static Color bg;
    private static Color fgLineNS;
    private static Color fgLineS;

    public MEPRCellRenderer() {
        bg = getBackground();
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineNS = bg.darker().darker();
        } else {
            fgLineNS = bg.brighter().brighter();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (fgLineS == null && isSelected) setSelectionColors(getBackground());
        MEPREntry meprEntry = (MEPREntry) value;
        String s = "";
        s += "<HTML>";
        s += "<b>" + meprEntry.getAction() + "</b></font>:";
        s += "<font color=" + (isSelected ? ColorUtils.colorToHex(fgLineS) : ColorUtils.colorToHex(fgLineNS)) + ">"
                + "    " + meprEntry.getComment() + "</font>";
        s += "</HTML>";
        setText(s);
        return c;
    }

    private void setSelectionColors(Color bg) {
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineS = bg.darker().darker();
        } else {
            fgLineS = bg.brighter().brighter();
        }
    }
}
