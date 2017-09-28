package at.mep.gui.fileStructure;

import at.mep.util.EIconDecorator;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;

/** Created by Andreas Justin on 2016 - 02 - 25. */
class TreeRenderer extends DefaultTreeCellRenderer {
    private static final Color HIDDEN_COLOR = new Color(180, 100, 115);
    private static final Color META_COLOR = new Color(100, 115, 180);
    private static final Color INVALID_COLOR = new Color(133, 133, 133);
    // private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    TreeRenderer() {
        // Color backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        // Color backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }
    
    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);
        Node node = (Node) value;
        String nodeStringU = node.nodeText();
        String nodeStringL = nodeStringU.toLowerCase();

        if (row == 0 && nodeStringL.endsWith(".m")) {
            setIcon(EIconsFileStructure.MFILE.getIcon());
        } else if (node.getEMetaNodeType() == EMetaNodeType.META_CLASS) {
            // CLASS
            setIcon(EIconsFileStructure.CLASS.getIcon());
        } else if (node.getEMetaNodeType() == EMetaNodeType.MATLAB
                && node.getType() == MTree.NodeType.FUNCTION
                || node.getEMetaNodeType() == EMetaNodeType.META_METHOD
                || node.getEMetaNodeType() == EMetaNodeType.META_PROPERTY) {
            // METHOD, FUNCTION, PROPERTY
            setFunctionPropertyIcon(node);
        } else if (node.getEMetaNodeType() == EMetaNodeType.MATLAB && node.getType() == MTree.NodeType.CELL_TITLE) {
            // SECTION
            setIcon(EIconsFileStructure.CELL.getIcon());
        }
        setText(nodeStringU);
        setFont(new Font("Courier New", Font.PLAIN, 11));
        return c;
    }

    private void setFunctionPropertyIcon(Node node) {
        java.util.List<Icon> decorators = new ArrayList<>(2);
        java.util.List<Color> colors = new ArrayList<>(2);
        java.util.List<EIconDecorator> positions = new ArrayList<>(2);

        switch (node.getAccess()) {
            case INVALID: {
                decorators.add(EIconsFileStructure.DECORATOR_INVALID.getIcon());
                colors.add(INVALID_COLOR);
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PRIVATE: {
                decorators.add(EIconsFileStructure.DECORATOR_PRIVATE.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PROTECTED: {
                decorators.add(EIconsFileStructure.DECORATOR_PROTECTED.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PUBLIC: {
                decorators.add(EIconsFileStructure.DECORATOR_PUBLIC.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case META: {
                decorators.add(EIconsFileStructure.DECORATOR_META.getIcon());
                colors.add(META_COLOR);
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
        }

        if (node.isHidden()) {
            colors.add(HIDDEN_COLOR);
        } else {
            if (colors.size() < decorators.size()) {
                colors.add(null);
            }
        }
        if (node.isStatic()) {
            decorators.add(EIconsFileStructure.DECORATOR_STATIC.getIcon());
            colors.add(null);
            positions.add(EIconDecorator.SOUTH_WEST_INSIDE);
        }

        if (decorators.size() != colors.size() || decorators.size() != positions.size()) {
            throw new IllegalStateException("size for icon does not match");
        }

        Icon icon;
        if (node.isProperty()) {
            icon = EIconsFileStructure.PROPERTY.getIcon(decorators, colors, positions);
        } else {
            icon = EIconsFileStructure.METHOD.getIcon(decorators, colors, positions);
        }
        setIcon(icon);
    }
}
