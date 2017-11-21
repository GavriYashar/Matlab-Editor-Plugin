package at.mep.workspace;

import at.mep.gui.ContextMenu;
import at.mep.util.ComponentUtil;
import com.mathworks.comparisons.main.ComparisonUtilities;
import com.mathworks.comparisons.source.impl.VariableSource;
import com.mathworks.mlwidgets.workspace.WorkspaceTable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Created by Andreas Justin on 2017-11-20. */
public class WorkspaceWrapper {

    /** adds specific MEP callbacks for Matlab's workspace */
    public static void setCallbacks() {
        final WorkspaceTable wst = ComponentUtil.getWorkspaceTable();

        if (wst == null) {
            return;
        }

        wst.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (e.getButton()) {
                    case 1:
                        // left
                        break;
                    case 2:
                        // middle
                        break;
                    case 3:
                        // right
                        ContextMenu.contribute(wst);
                        break;
                    case 4:
                        // backward
                        break;
                    case 5:
                        // forward
                        break;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    /** returns a list of string of selected variables in workspace */
    public static List<String> getSelectedVariables() {
        List<String> vars = new ArrayList<>(0);
        WorkspaceTable wst = ComponentUtil.getWorkspaceTable();
        if (wst == null) {
            return vars;
        }
        int[] ints = wst.getSelectedRowsChron();
        if (ints == null) {
            return vars;
        }
        return Arrays.asList(wst.getVariableNames(ints));
    }

    /** compares two variables in workspace using Matlab's comparison feature */
    public static void vardiff(String var1, String var2) {
        // http://de.mathworks.com/matlabcentral/fileexchange/64897-vardiff
        // does work with M2014a
        VariableSource vs1 = new VariableSource(var1, "evalin('base','" + var1 + "')");
        VariableSource vs2 = new VariableSource(var2, "evalin('base','" + var2 + "')");

        ComparisonUtilities.startComparison(vs1,vs2);
    }

    public static JPopupMenu getContextMenu() {
        return ComponentUtil.getWorkspaceTable().getSelectionPopupMenu();
    }
}
