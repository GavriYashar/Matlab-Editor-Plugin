package at.mep.gui.breakpointviewer;

import at.mep.util.ColorUtils;
import at.mep.util.FileUtils;
import com.mathworks.mde.editor.breakpoints.MatlabBreakpoint;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2018-24-03.
 */
public class BreakpointCellRenderer extends DefaultListCellRenderer {
    static Color fg;
    static Color bg;
    static Color fgLineNS;
    static Color fgConditionNS;
    static Color fgDisabledNS;
    static Color fgLineS;
    static Color fgConditionS;
    static Color fgDisabledS;

    public BreakpointCellRenderer() {
        fg = getForeground();
        bg = getBackground();
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineNS = bg.darker().darker();
            fgConditionNS = bg.darker();
            fgDisabledNS = ColorUtils.mixColors(bg.darker().darker(), Color.RED, 0.5f);
        } else {
            fgLineNS = bg.brighter().brighter();
            fgConditionNS = bg.brighter();
            fgDisabledNS = ColorUtils.mixColors(bg.brighter().brighter(), Color.RED, 0.5f);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (fgLineS == null && isSelected) setSelectionColors(getBackground());
        MatlabBreakpoint breakpoint = (MatlabBreakpoint) value;

        String s = "<HTML><font color=$COLORFQN>$FQN</font>: <font color=$COLORLINE>$@$LINE</font> <font color=$COLOREXPR>$EXPR</font><HTML>";
        s = s.replace("$COLORFQN", breakpoint.isEnabled() ? ColorUtils.colorToHex(Color.BLACK) : ColorUtils.colorToHex(Color.RED));
        s = s.replace("$FQN", FileUtils.fullyQualifiedName(breakpoint.getFile()));
        s = s.replace("$@", breakpoint.isAnonymous() ? "@" : "");
        s = s.replace("$COLORLINE", isSelected ? ColorUtils.colorToHex(fgLineS) : ColorUtils.colorToHex(fgLineNS));
        s = s.replace("$LINE", "" + breakpoint.getOneBasedLineNumber());
        s = s.replace("$COLOREXPR", isSelected ? ColorUtils.colorToHex(fgConditionS) : ColorUtils.colorToHex(fgConditionNS));
        s = s.replace("$EXPR", breakpoint.hasExpression() ? " (" + breakpoint.getExpression() + ")" : "");
        setText(s);
        return c;
    }

    private void setSelectionColors(Color bg) {
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);

        if (hsb[2] > 0.5) {
            fgLineS = bg.darker().darker();
            fgConditionS = bg.darker();
            fgDisabledS = ColorUtils.mixColors(bg.darker().darker(), Color.RED, 0.5f);
        } else {
            fgLineS = bg.brighter().brighter();
            fgConditionS = bg.brighter();
            fgDisabledS = ColorUtils.mixColors(bg.brighter().brighter(), Color.RED, 0.5f);
        }
    }
}
