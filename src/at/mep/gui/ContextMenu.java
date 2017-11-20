package at.mep.gui;

import at.mep.workspace.WorkspaceWrapper;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mlwidgets.workspace.WorkspaceTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/** Created by Andreas Justin on 2017-11-20. */
public class ContextMenu {

    public static void contribute(Editor editor) {
        return;
        /*
        MJPopupMenu cm = EditorWrapper.getContextMenu(editor);
        cm.setVisible(false);

        MJMenu mjmMEP = new MJMenu("MEP");

        {
            MJMenuItem mjMenuItem = new MJMenuItem(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("yay1");
                }
            });
            mjMenuItem.setText("text1");
            mjmMEP.add(mjMenuItem);
        }
        {
            MJMenuItem mjMenuItem = new MJMenuItem(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("yay2");
                }
            });
            mjMenuItem.setText("text2");
            mjmMEP.add(mjMenuItem);
        }

        cm.add(mjmMEP);
        cm.setVisible(true);
        */
    }

    public static void contribute(WorkspaceTable workspaceTable) {
        JPopupMenu cm = workspaceTable.getSelectionPopupMenu();
        cm.setVisible(false);

        JMenuItem jMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> vars = WorkspaceWrapper.getSelectedVariables();
                if (vars.size() != 2) {
                    return;
                }
                WorkspaceWrapper.vardiff(vars.get(0), vars.get(1));
            }
        });
        jMenuItem.setText("VarDiff V1 <-> V2");
        cm.add(jMenuItem);

        cm.setVisible(true);
    }
}
