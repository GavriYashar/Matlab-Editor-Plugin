package at.justin.matlab.util;

import java.awt.*;
import java.awt.List;
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
}
