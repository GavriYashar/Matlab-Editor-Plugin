package at.mep.gui.clipboardStack;

import javax.swing.*;
import java.awt.*;

/** Created by Andreas Justin on 2016-09-28. */
public class ClipboardCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String s = (String) value;
        if (s != null && s.length() > 50) {
            setText(s.substring(0, 49));
        }
        return c;
    }
}
