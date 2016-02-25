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
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(ProjectIcon.STATIC_OVERLAY_11x11.getIcon());
                renderer.add(iconLabel);
            }
            if (nodeL.contains("public") || !nodeL.contains("private")) {
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon());
                renderer.add(iconLabel);
            }
            if (nodeL.contains("private")) {
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon());
                renderer.add(iconLabel);
            }
            JLabel stringLabel = new JLabel();
            stringLabel.setText(node);
            renderer.add(stringLabel);

            if (selected) {
                renderer.setBackground(backgroundSelectionColor);
            } else {
                renderer.setBackground(backgroundNonSelectionColor);
            }

            returnValue = renderer;
            renderer.setEnabled(jTree.isEnabled());
        }

        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(jTree, value, selected, exp, leaf, row, hasFocus);
            returnValue.setFont(new Font("Courier New", Font.PLAIN, 10));
        }
        return returnValue;
    }
}
