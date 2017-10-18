package com.mathworks.widgets.desk;

import at.mep.Matlab;
import at.mep.editor.EditorWrapper;
import at.mep.util.ComponentUtil;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/** Created by Andreas Justin on 2017-10-17. */
public class DTDocumentAccessor {
    private static boolean messageAlreadyShown = false;

    public static void addListener() {
        DTDocumentContainer dt = getDocumentContainer();
        dt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println("item state changed " + EditorWrapper.getActiveEditor().getShortName());
                System.out.println("isFloating: " + EditorWrapper.isFloating());
                // once an editor is undocked the item state changed get's called and isFloating is true
            }
        });

        dt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("focus gained");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("focus lost");
            }
        });
    }

    private static DTDocumentContainer getDocumentContainer() {
        java.util.List<Component> components = ComponentUtil.searchForComponentsRecursive(Matlab.getInstance().getMlDesktop().getMainFrame(), "DTDocumentContainer");
        if (!messageAlreadyShown && components.size() > 1) {
            messageAlreadyShown = true;
            System.out.println("multiple DTDocumentContainers found");
        }
        if (!messageAlreadyShown && components.size() < 1) {
            messageAlreadyShown = true;
            System.out.println("No DTDocumentContainers found");
            return null;
        }
        return (DTDocumentContainer) components.get(0);
    }
}
