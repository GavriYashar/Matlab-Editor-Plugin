package at.justin.matlab.gui.fileStructure;

import at.justin.matlab.gui.Icons;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.IconDecoratorE;
import at.justin.matlab.util.IconUtil;
import com.mathworks.common.icons.FileTypeIcon;
import com.mathworks.common.icons.ProjectIcon;

import javax.swing.*;
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
            JTree jTree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);
        Node node = (Node) value;
        String nodeStringU = node.nodeText();
        String nodeStringL = nodeStringU.toLowerCase();

        if (row == 0 && nodeStringL.endsWith(".m")) {
            setIcon(FileTypeIcon.M.getIcon());
        } else if (row > 0) {
            switch (node.getType()) {
                case FUNCTION:
                    setFunctionIcon(node);
                    break;
                case CLASSDEF:
                    setClassDefIcon(node);
                    break;
                case PROPERTIES:
                    setIcon(ProjectIcon.PROPERTY.getIcon());
                    break;
                case ID:
                    if (node.isProperty()) setPropertyIcon(node);
            break;
                case METHODS:
                    if (nodeStringL.contains("static")) {
                        setIcon(ProjectIcon.STATIC_OVERLAY_11x11.getIcon());
                    }
                    if (nodeStringL.contains("public") || !nodeStringL.contains("private")) {
                        setIcon(ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon());
                    }
                    if (nodeStringL.contains("private")) {
                        setIcon(ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon());
                    }
                    break;
                case CELL_TITLE:
                    setIcon(ProjectIcon.CELL.getIcon());
                    break;
            }
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
                if (node.isStatic()) {
                    Icon decorator = Icons.DECORATOR_STATIC_INTELLIJ.getIcon();
                    // decorator = IconUtil.color(decorator, new Color(180, 180, 180));
                    iconIJ = IconUtil.decorateIcon(iconIJ, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
                setIcon(iconIJ);
                break;
            case "matlab":
                setIcon(ProjectIcon.FUNCTION.getIcon());
                break;
            case "eclipse":
                Icon iconE;
                if (!node.isAccessPrivate()) {
                    iconE = Icons.METHOD_PRIVATE_ECLIPSE.getIcon();
                } else {
                    iconE = Icons.METHOD_ECLIPSE.getIcon();
                }
                if (node.isStatic()) {
                    Icon decorator = Icons.DECORATOR_STATIC_ECLIPSE.getIcon();
                    iconE = IconUtil.decorateIcon(iconE, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
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
                decoratePublicPrivate(node, iconIJ, setting);
                decorateStatic(node, iconIJ, setting);
                setIcon(iconIJ);
                break;
            case "matlab":
                setIcon(ProjectIcon.FUNCTION.getIcon());
                break;
            case "eclipse":
                Icon iconE = decoratePublicPrivate(node, null, setting);
                decorateStatic(node, iconE, setting);
                setIcon(iconE);
                break;
            default:
                setIcon(ProjectIcon.PROPERTY.getIcon());
        }
    }

    private static Icon decoratePublicPrivate(Node node, Icon icon, String setting) {
        switch (setting) {
            case "intellij":
                if (node.isAccessPrivate()) {
                    Icon decorator = Icons.DECORATOR_PRIVATE_INTELLIJ.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                } else {
                    Icon decorator = Icons.DECORATOR_PUBLIC_INTELLIJ.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.EAST_OUTSIDE);
                }
                break;
            case "matlab":
                break;
            case "eclipse":
                if (!node.isAccessPrivate()) {
                    return Icons.METHOD_PRIVATE_ECLIPSE.getIcon();
                } else {
                    return Icons.METHOD_ECLIPSE.getIcon();
                }
            default:
        }
        return icon;
    }

    private static Icon decorateStatic(Node node, Icon icon, String setting) {
        switch (setting) {
            case "intellij":
                if (node.isStatic()) {
                    Icon decorator = Icons.DECORATOR_STATIC_INTELLIJ.getIcon();
                    // decorator = IconUtil.color(decorator, new Color(180, 180, 180));
                    icon = IconUtil.decorateIcon(icon, decorator, IconDecoratorE.SOUTH_WEST_INSIDE);
                }
                break;
            case "matlab":
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
}
