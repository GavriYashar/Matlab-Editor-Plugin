package at.mep.gui.fileStructure;

import at.mep.gui.EIcons;
import at.mep.prefs.Settings;
import at.mep.util.EIconDecorator;
import at.mep.util.IconUtil;
import com.mathworks.common.icons.FileTypeIcon;
import com.mathworks.common.icons.ProjectIcon;
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

    private static Icon decorateAccess(Node node, Icon icon, String setting) {
        switch (node.getEMetaNodeType()) {
            case META_METHOD:
                icon = decorateAccessMethod(node, icon, setting);
                break;
            case META_PROPERTY:
                icon = decorateAccessProperty(node, icon, setting);
                break;
            default:
                break;
        }

        return icon;
    }

    private static Icon decorateAccessProperty(Node node, Icon icon, String setting) {
        return decorateAccessMethod(node, icon, setting);
    }

    private static Icon decorateAccessMethod(Node node, Icon icon, String setting) {
        switch (setting) {
            case "intellij":
                switch (node.getAccess()) {
                    case INVALID: {
                        Icon decorator = EIcons.DECORATOR_PUBLIC_INTELLIJ.getIcon();
                        icon = IconUtil.color(icon, INVALID_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PRIVATE: {
                        Icon decorator = EIcons.DECORATOR_PRIVATE_INTELLIJ.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PROTECTED: {
                        Icon decorator = EIcons.DECORATOR_PROTECTED_INTELLIJ.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PUBLIC: {
                        Icon decorator = EIcons.DECORATOR_PUBLIC_INTELLIJ.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case META: {
                        Icon decorator = EIcons.DECORATOR_PRIVATE_INTELLIJ.getIcon();
                        decorator = IconUtil.color(decorator, META_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                }
                break;
            case "matlab":
                switch (node.getAccess()) {
                    case INVALID: {
                        Icon decorator = ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon();
                        icon = IconUtil.color(icon, INVALID_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PRIVATE: {
                        Icon decorator = ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PROTECTED: {
                        Icon decorator = ProjectIcon.PROTECTED_OVERLAY_11x11.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case PUBLIC: {
                        Icon decorator = ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon();
                        if (node.isHidden()) decorator = IconUtil.color(decorator, HIDDEN_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                    case META: {
                        Icon decorator = ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon();
                        decorator = IconUtil.color(decorator, META_COLOR);
                        icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.EAST_OUTSIDE);
                        break;
                    }
                }
                break;
            case "eclipse":
                switch (node.getAccess()) {
                    case INVALID:
                        icon = EIcons.METHOD_PUBLIC_ECLIPSE.getIcon();
                        icon = IconUtil.color(icon, INVALID_COLOR);
                        break;
                    case PRIVATE:
                        icon = EIcons.METHOD_PRIVATE_ECLIPSE.getIcon();
                        if (node.isHidden()) icon = IconUtil.color(icon, HIDDEN_COLOR);
                        break;
                    case PROTECTED:
                        icon = EIcons.METHOD_PROTECTED_ECLIPSE.getIcon();
                        if (node.isHidden()) icon = IconUtil.color(icon, HIDDEN_COLOR);
                        break;
                    case PUBLIC:
                        icon = EIcons.METHOD_PUBLIC_ECLIPSE.getIcon();
                        if (node.isHidden()) icon = IconUtil.color(icon, HIDDEN_COLOR);
                        break;
                    case META:
                        icon = EIcons.METHOD_PRIVATE_ECLIPSE.getIcon();
                        icon = IconUtil.color(icon, META_COLOR);
                        break;
                }
        }
        return icon;
    }

    private static Icon decorateStatic(Node node, Icon icon, String setting) {
        switch (setting) {
            case "intellij":
                if (node.isStatic()) {
                    Icon decorator = EIcons.DECORATOR_STATIC_INTELLIJ.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.SOUTH_WEST_INSIDE);
                }
                break;
            case "matlab":
                if (node.isStatic()) {
                    Icon decorator = ProjectIcon.STATIC_OVERLAY_11x11.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.SOUTH_WEST_INSIDE);
                }
                break;
            case "eclipse":
                if (node.isStatic()) {
                    Icon decorator = EIcons.DECORATOR_STATIC_ECLIPSE.getIcon();
                    icon = IconUtil.decorateIcon(icon, decorator, EIconDecorator.SOUTH_WEST_INSIDE);
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
        } else if (node.getEMetaNodeType() == EMetaNodeType.META_CLASS) {
            // CLASS
            setClassDefIcon(node);
        } else if (node.getEMetaNodeType() == EMetaNodeType.MATLAB
                && node.getType() == MTree.NodeType.FUNCTION
                || node.getEMetaNodeType() == EMetaNodeType.META_METHOD
                || node.getEMetaNodeType() == EMetaNodeType.META_PROPERTY) {
            // METHOD, FUNCTION, PROPERTY
            setFunctionPropertyIcon(node);
        } else if (node.getEMetaNodeType() == EMetaNodeType.MATLAB && node.getType() == MTree.NodeType.CELL_TITLE) {
            // SECTION
            setIcon(ProjectIcon.CELL.getIcon());
        }
        setText(nodeStringU);
        setFont(new Font("Courier New", Font.PLAIN, 11));
        return c;
    }

    private void setClassDefIcon(Node node) {
        setIcon(EIconsFileStructure.CLASS.getIcon(Settings.getFSIconSet()));
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
            icon = EIconsFileStructure.POPERTY.getIcon(Settings.getFSIconSet(), decorators, colors, positions);
        } else {
            icon = EIconsFileStructure.METHOD.getIcon(Settings.getFSIconSet(), decorators, colors, positions);
        }
        setIcon(icon);
    }
}
