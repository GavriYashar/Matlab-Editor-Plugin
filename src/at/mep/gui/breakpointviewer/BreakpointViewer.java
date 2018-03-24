package at.mep.gui.breakpointviewer;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.DockableFrame;
import at.mep.util.KeyStrokeUtil;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.mde.editor.breakpoints.MatlabBreakpoint;
import com.mathworks.mde.editor.breakpoints.MatlabBreakpointUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BreakpointViewer extends DockableFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static BreakpointViewer instance;
    private static JList<Object> jList;

    private final AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectBreakpoint();
            escAction.actionPerformed(new ActionEvent(e, 0, null));
        }
    };

    private BreakpointViewer() {
        super(EViewer.BREAKPOINTS);
        setLayout();
    }

    private void selectBreakpoint() {
        MatlabBreakpoint breakpoint = (MatlabBreakpoint) jList.getSelectedValue();
        if (breakpoint == null) return;
        Editor editor = EditorWrapper.openEditor(breakpoint.getFile());
        EditorWrapper.goToLine(editor, breakpoint.getOneBasedLineNumber(), false);
    }

    public void showDialog() {
        jList.setListData(MatlabBreakpointUtils.getDebugger().getBreakpoints().toArray());
        jList.requestFocus();
        setVisible(true);
    }

    public static BreakpointViewer getInstance() {
        if (instance == null) instance = new BreakpointViewer();
        return instance;
    }

    private void setLayout() {
        setLayout(new GridBagLayout());
        addFocusListener(jList);

        KeyStroke ksENTER = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getInputMap(IFW).put(ksENTER, "ENTER");
        getActionMap().put("ENTER", enterAction);

        createList();
    }

    private void createList() {
        jList = new JList<>(MatlabBreakpointUtils.getDebugger().getBreakpoints().toArray());
        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new BreakpointCellRenderer());
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1 && e.getButton() == 1) {
                    enterAction.actionPerformed(new ActionEvent(e, 0, null));
                }
            }
        });

        JScrollPane jsp = new JScrollPane(jList);
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        jsp.getHorizontalScrollBar().setUnitIncrement(20);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 10, 10);

        add(jsp, gbc);
    }
}
