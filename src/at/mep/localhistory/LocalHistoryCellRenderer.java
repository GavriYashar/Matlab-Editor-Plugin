package at.mep.localhistory;


import at.mep.util.ColorUtils;
import at.mep.util.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Created by Andreas Justin on 2018-12-19. */


public class LocalHistoryCellRenderer extends DefaultListCellRenderer {
    private static Color fg;
    private static Color bg;
    private static Color fgLineNS;
    private static Color fgLineS;

    public LocalHistoryCellRenderer() {
        fg = getForeground();
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
        File file = (File) value;

        String s = "<HTML><font color=$COLORFQN>$FQN</font>: <font color=$COLORMODDATE>$MODDATE</font><HTML>";
        s = s.replace("$COLORFQN", ColorUtils.colorToHex(Color.BLACK));
        s = s.replace("$FQN", file.getName());
        s = s.replace("$MODDATE", DateUtil.getDate("YYYY-MM-dd  HH:mm:ss:SSS", file.lastModified()));
        s = s.replace("$COLORMODDATE", ColorUtils.colorToHex(Color.DARK_GRAY));
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
