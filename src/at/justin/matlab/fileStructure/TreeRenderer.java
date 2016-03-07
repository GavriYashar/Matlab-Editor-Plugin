package at.justin.matlab.fileStructure;

import com.mathworks.common.icons.FileTypeIcon;
import com.mathworks.common.icons.ProjectIcon;
import com.mathworks.widgets.text.mcode.MTree;

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
        // TODO: row is not the correct index to select from FileStructure PositionNodes, once sth. is collapsed the index is sth other than the row
        Component c = super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);

        String node = (String) ((DefaultMutableTreeNode) value).getUserObject();
        String nodeL = node.toLowerCase();

        if (row == 0 && nodeL.startsWith("Script")) {
            setIcon(FileTypeIcon.M.getIcon());
        } else if (row > 0 && row - 1 < FileStructure.getINSTANCE().getPositionNodes().size()) {
            MTree.Node mTreeNode = FileStructure.getINSTANCE().getPositionNodes().get(row-1);
            switch (mTreeNode.getType()) {
            case FUNCTION:
                setIcon(ProjectIcon.FUNCTION.getIcon());
                break;
            case CLASSDEF:
                setIcon(FileTypeIcon.M_CLASS.getIcon());
                break;
            case PROPERTIES:
                setIcon(ProjectIcon.PROPERTY.getIcon());
                break;
            case METHODS:
                if (node.contains("static")) {
                    setIcon(ProjectIcon.STATIC_OVERLAY_11x11.getIcon());
                }
                if (nodeL.contains("public") || !nodeL.contains("private")) {
                    setIcon(ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon());
                }
                if (nodeL.contains("private")) {
                    setIcon(ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon());
                }
                break;
            case CELL_TITLE:
                setIcon(ProjectIcon.CELL.getIcon());
                break;
            }
        }
        setFont(new Font("Courier New", Font.PLAIN, 11));
        return c;
    }

}
