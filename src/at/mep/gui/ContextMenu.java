package at.mep.gui;

import at.mep.editor.EditorWrapper;
import at.mep.editor.tree.MFile;
import at.mep.mepr.MEPR;
import at.mep.prefs.Settings;
import at.mep.util.TreeUtilsV2;
import at.mep.workspace.WorkspaceWrapper;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mlwidgets.workspace.WorkspaceTable;
import com.mathworks.mwswing.MJMenu;
import com.mathworks.mwswing.MJMenuItem;
import com.mathworks.mwswing.MJPopupMenu;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/** Created by Andreas Justin on 2017-11-20. */
public class ContextMenu {

    public static void contribute(Editor editor) {
        if (true || !Settings.getPropertyBoolean("feature.enableReplacements")) {
             return;
        }

        MJPopupMenu cm = EditorWrapper.getContextMenu(editor);
        Component[] components = cm.getComponents();
        // setVisible(false) will lose the "fix" functionality e.g.: replace sth with ~
        cm.setVisible(false);

        MJMenu mjmMEP = new MJMenu("MEP Insert");
        {
            MJMenuItem mjMenuItem = new MJMenuItem(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EditorWrapper.insertTextAtPos("%rev%", EditorWrapper.getCaretPosition());
                    MEPR.doYourThing();
                }
            });
            mjMenuItem.setText("Revision Line");
            mjmMEP.add(mjMenuItem);
        }
        contributeEditorClass(mjmMEP);

        cm.add(mjmMEP);
        cm.setVisible(true);
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
        List<String> vars = WorkspaceWrapper.getSelectedVariables();
        if (vars.size() != 2) {
            jMenuItem.setText("VarDiff needs 2 variables");
            jMenuItem.setEnabled(false);
        } else {
            jMenuItem.setText("VarDiff " + vars.get(0) + " <-> " + vars.get(1));
        }
        cm.add(jMenuItem);

        cm.setVisible(true);
    }

    private static void contributeEditorClass(MJMenu mjMenu) {
        MTree mTree = EditorWrapper.getMTreeFast(EditorWrapper.getActiveEditor());
        if (TreeUtilsV2.getFileType(mTree) != TreeUtilsV2.FileType.ClassDefinitionFile) {
            // only add if active editor is a ClassDefinitionFile
            return;
        }

        {
            MJMenuItem mjMenuItem = new MJMenuItem(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MFile mFile = MFile.construct(EditorWrapper.getActiveEditor());
                    MFile.ClassDef classDef = mFile.getClassDefs().get(0);
                    List<MFile.ClassDef.Properties> propertiesList = classDef.getProperties();
                    
                    JOptionPane.showMessageDialog(
                            new JFrame(""),
                            "Test message: " + mFile.getName(),
                            "test title",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            mjMenuItem.setText("Getter / Setter");
            mjMenu.add(mjMenuItem);
        }
    }
}
