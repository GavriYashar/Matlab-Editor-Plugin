package at.mep.gui.clipboardStack;

import at.mep.util.StringUtils;

import javax.swing.*;
import java.awt.*;

/** Created by Andreas Justin on 2016-09-28. */
public class ClipboardCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String s = (String) value;

        if (s == null) {
            return c;
        }
        if (s.length() > 50) {
            setText(s.substring(0, 49));
        }

        String st = StringUtils.trimStart(s);
        if (st.startsWith("%")) {
            setForeground(new Color(0,128,0));
        }

        return c;
    }
}
