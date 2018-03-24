package at.mep.gui.breakpointviewer;

import at.mep.util.ColorUtils;
import at.mep.util.FileUtils;
import com.mathworks.matlab.api.debug.Breakpoint;
import com.mathworks.mde.editor.breakpoints.MatlabBreakpoint;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2018-24-03.
 */
public class BreakPointCellRenderer extends DefaultListCellRenderer {
    static Color fg;
    static Color bg;
    static Color fgLineNS;
    static Color fgConditionNS;
    static Color fgLineS;
    static Color fgConditionS;

    public BreakPointCellRenderer() {
        fg = getForeground();
        bg = getBackground();
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineNS = bg.darker().darker();
            fgConditionNS = bg.darker();
        } else {
            fgLineNS = bg.brighter().brighter();
            fgConditionNS = bg.brighter();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (fgLineS == null && isSelected) setSelectionColors(getBackground());
        MatlabBreakpoint breakpoint = (MatlabBreakpoint) value;
        String s = "";
        s += "<HTML>";
        s += FileUtils.fullyQualifiedName(breakpoint.getFile());
        s += "<font color=" + (isSelected ? ColorUtils.colorToHex(fgLineS) : ColorUtils.colorToHex(fgLineNS)) + ">"
            + ": " + breakpoint.getOneBasedLineNumber() + "</font>";
        s += "<font color=" + (isSelected ? ColorUtils.colorToHex(fgConditionS) : ColorUtils.colorToHex(fgConditionNS)) + ">"
                + " (" + breakpoint.getExpression() + ") </font > ";
        s += "</HTML>";

        setText(s);
        return c;
    }

    private void setSelectionColors(Color bg) {
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineS = bg.darker().darker();
            fgConditionS = bg.darker();
        } else {
            fgLineS = bg.brighter().brighter();
            fgConditionS = bg.brighter();
        }
    }
}
