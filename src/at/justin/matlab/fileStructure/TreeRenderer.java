package at.justin.matlab.fileStructure;

import com.mathworks.common.icons.ProjectIcon;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 25.
 */
public class TreeRenderer extends DefaultTreeCellRenderer {
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    Color backgroundSelectionColor;
    Color backgroundNonSelectionColor;
    public TreeRenderer() {
        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree, Object value,boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);

        Component returnValue = null;
        JPanel renderer = new JPanel();

        String node = (String) ((DefaultMutableTreeNode) value).getUserObject();
        String nodeL = node.toLowerCase();
        if (false && nodeL.startsWith("methods")) {
            if (node.contains("static")) {
                addJLabelIconToRenderer(renderer,ProjectIcon.STATIC_OVERLAY_11x11.getIcon());
            }
            if (nodeL.contains("public") || !nodeL.contains("private")) {
                addJLabelIconToRenderer(renderer,ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon());
            }
            if (nodeL.contains("private")) {
                addJLabelIconToRenderer(renderer,ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon());
            }
            addJLabelStringToRenderer(renderer,node);
            returnValue = renderer;
        } else if (nodeL.startsWith("%%")) {
            addJLabelIconToRenderer(renderer,ProjectIcon.CELL.getIcon());
            addJLabelStringToRenderer(renderer,node);
            returnValue = renderer;
        }

        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(jTree, value, selected, exp, leaf, row, hasFocus);
            returnValue.setFont(new Font("Courier New", Font.PLAIN, 11));
        } else {
            if (selected) {
                renderer.setBackground(backgroundSelectionColor);
            } else {
                renderer.setBackground(backgroundNonSelectionColor);
            }
            renderer.setEnabled(jTree.isEnabled());
        }
        return returnValue;
    }

    private void addJLabelStringToRenderer(JPanel renderer, String string) {
        JLabel stringLabel = new JLabel();
        stringLabel.setText(string);
        renderer.add(stringLabel);
    }

    private void addJLabelIconToRenderer(JPanel renderer, Icon icon) {
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(icon);
        renderer.add(iconLabel);
    }
}
