package at.mep.util;

import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.EditorSyntaxTextPane;
import com.mathworks.widgets.editor.breakpoints.BreakpointView;

import java.awt.*;
import java.util.*;

/**
 * Created by Andreas Justin on 2016-08-31.
 */
public class ComponentUtil {

    public static java.util.List<Component> getAllComponents(final Container container) {
        Component[] comps = container.getComponents();
        java.util.List<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    public static java.util.List<Component> getComponents(Container container, String classString) {
        java.util.List<Component> list = new ArrayList<>();
        for (Component component : getAllComponents(container)) {
            if (component.getClass().toString().endsWith(classString)) {
                list.add(component);
            }
        }
        return list;
    }

    public static java.util.List<Component> searchForComponentsRecursive(final Component component, String name) {
        java.util.List<Component> foundComponents = new ArrayList<>(0);
        if (!(component instanceof Container)) return foundComponents;
        Component[] components = ((Container) component).getComponents();
        for (Component c : components) {
            if (c.getClass().toString().contains(name)) {
                foundComponents.add(c);
            } else if (c instanceof Container) {
                java.util.List<Component> recFound = searchForComponentsRecursive(c, name);
                foundComponents.addAll(recFound);
            }
        }
        return foundComponents;
    }

    public static BreakpointView.Background getBreakPointViewForEditor(Editor editor) {
        Component component = editor.getTextComponent().getParent().getParent();
        java.util.List<Component> breakPointViews = ComponentUtil.searchForComponentsRecursive(component, "BreakpointView");
        if (breakPointViews.size() > 1) {
            System.out.println("Multiple BreakpointView found for editor \"" + editor.getLongName() + "\"");
        }
        if (breakPointViews.size() < 1) {
            return null;
        }
        return (BreakpointView.Background) breakPointViews.get(0);
    }

    public static EditorSyntaxTextPane getEditorSyntaxTextPaneForEditor(Editor editor) {
        Component[] components = editor.getTextComponent().getParent().getComponents();
        EditorSyntaxTextPane editorSyntaxTextPane = null;
        for (Component component : components) {
            if (component instanceof EditorSyntaxTextPane) {
                editorSyntaxTextPane = (EditorSyntaxTextPane) component;
                break;
            }
        }
        return editorSyntaxTextPane;
    }
}
