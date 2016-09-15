package at.justin.matlab.gui.fileStructure;

import at.justin.matlab.gui.Icons;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.IconDecoratorE;
import at.justin.matlab.util.IconUtil;
import com.mathworks.common.icons.FileTypeIcon;
import com.mathworks.common.icons.ProjectIcon;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/** Created by Andreas Justin on 2016 - 02 - 25. */
class TreeRenderer extends DefaultTreeCellRenderer {
    // private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    TreeRenderer() {
        // Color backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        // Color backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }

    private static Icon decoratePublicPrivate(Node node, Icon icon, String setting) {
        Color hiddenColor = new Color(180, 99, 115);
        switch (setting) {
            case "intellij":
                if (node.isPrivate()) {
                    Icon decorator = Icons.DECORATOR_PRIVATE_INTELLIJ.getIcon();
                    if (node.isHidden()) decorator = IconUtil.color(decorator, hiddenColor);
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                } else {
                    Icon decorator = Icons.DECORATOR_PUBLIC_INTELLIJ.getIcon();
                    if (node.isHidden()) decorator = IconUtil.color(decorator, hiddenColor);
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                }
                break;
            case "matlab":
                if (node.isPrivate()) {
                    Icon decorator = ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon();
                    if (node.isHidden()) decorator = IconUtil.color(decorator, hiddenColor);
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                } else {
                    Icon decorator = ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon();
                    if (node.isHidden()) decorator = IconUtil.color(decorator, hiddenColor);
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                }
                break;
            case "eclipse":
                if (node.isPrivate()) {
                    icon = Icons.METHOD_PRIVATE_ECLIPSE.getIcon();
                } else {
                    icon = Icons.METHOD_ECLIPSE.getIcon();
                }
                if (node.isHidden()) icon = IconUtil.color(icon, hiddenColor);
        }
        return icon;
    }

    private static Icon decorateStatic(Node node, Icon icon, String setting) {
        switch (setting) {
            case "intellij":
                if (node.isStatic()) {
                    Icon decorator = Icons.DECORATOR_STATIC_INTELLIJ.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
                break;
            case "matlab":
                if (node.isStatic()) {
                    Icon decorator = ProjectIcon.STATIC_OVERLAY_11x11.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
                break;
            case "eclipse":
                if (node.isStatic()) {
                    Icon decorator = Icons.DECORATOR_STATIC_ECLIPSE.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
            default:
        }
        return icon;
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);
        Node node = (Node) value;
        String nodeStringU = node.nodeText();
        String nodeStringL = nodeStringU.toLowerCase();

        if (row == 0 && nodeStringL.endsWith(".m")) {
            setIcon(FileTypeIcon.M.getIcon());
        } else if (node.getMetaNodeType() == MetaNodeType.META_CLASS) {
            // CLASS
            setClassDefIcon(node);
        } else if (node.getMetaNodeType() == MetaNodeType.MATLAB && node.getType() == MTree.NodeType.FUNCTION
                || node.getMetaNodeType() == MetaNodeType.META_METHOD) {
            // METHOD
            setFunctionIcon(node);
        } else if (node.getMetaNodeType() == MetaNodeType.META_PROPERTY) {
            // PROPERTY
            setPropertyIcon(node);
        } else if (node.getMetaNodeType() == MetaNodeType.MATLAB && node.getType() == MTree.NodeType.CELL_TITLE) {
            // SECTION
            setIcon(ProjectIcon.CELL.getIcon());
        }
        setText(nodeStringU);
        setFont(new Font("Courier New", Font.PLAIN, 11));
        return c;
    }

    private void setClassDefIcon(Node node) {
        switch (Settings.getProperty("fs.iconSet")) {
            case "intellij":
                setIcon(Icons.CLASS_INTELLIJ.getIcon());
                break;
            case "matlab":
                setIcon(FileTypeIcon.M_CLASS.getIcon());
                break;
            case "eclipse":
                setIcon(Icons.CLASS_ECLIPSE.getIcon());
                break;
            default:
                setIcon(FileTypeIcon.M_CLASS.getIcon());
        }
    }

    private void setFunctionIcon(Node node) {
        String setting = Settings.getProperty("fs.iconSet");
        switch (setting) {
            case "intellij":
                Icon iconIJ = Icons.METHOD_INTELLIJ.getIcon();
                iconIJ = decoratePublicPrivate(node, iconIJ, setting);
                iconIJ = decorateStatic(node, iconIJ, setting);
                setIcon(iconIJ);
                break;
            case "matlab":
                Icon iconM = ProjectIcon.FUNCTION.getIcon();
                iconM = decoratePublicPrivate(node, iconM, setting);
                iconM = decorateStatic(node, iconM, setting);
                setIcon(iconM);
                break;
            case "eclipse":
                Icon iconE = decoratePublicPrivate(node, null, setting);
                iconE = decorateStatic(node, iconE, setting);
                setIcon(iconE);
                break;
            default:
                setIcon(ProjectIcon.FUNCTION.getIcon());
        }
    }

    private void setPropertyIcon(Node node) {
        String setting = Settings.getProperty("fs.iconSet");
        switch (setting) {
            case "intellij":
                Icon iconIJ = Icons.PROPERTY_INTELLIJ.getIcon();
                iconIJ = decoratePublicPrivate(node, iconIJ, setting);
                iconIJ = decorateStatic(node, iconIJ, setting);
                setIcon(iconIJ);
                break;
            case "matlab":
                setIcon(ProjectIcon.FUNCTION.getIcon());
                break;
            case "eclipse":
                Icon iconE = decoratePublicPrivate(node, null, setting);
                iconE = decorateStatic(node, iconE, setting);
                setIcon(iconE);
                break;
            default:
                setIcon(ProjectIcon.PROPERTY.getIcon());
        }
    }
}
