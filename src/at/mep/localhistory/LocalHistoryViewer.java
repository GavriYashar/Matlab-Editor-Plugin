package at.mep.localhistory;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.DockableFrame;
import at.mep.gui.components.ListSelectionModelN;
import at.mep.util.KeyStrokeUtil;
import matlabcontrol.MatlabInvocationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/** Created by Andreas Justin on 2018-12-19. */
public class LocalHistoryViewer extends DockableFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static LocalHistoryViewer instance;
    private static JList<Object> jList;

    private final AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startComparison();
            escAction.actionPerformed(new ActionEvent(e, 0, null));
        }
    };

    public LocalHistoryViewer() {
        super(EViewer.LOCAL_HISTORY);
        setLayout();
    }
    
    private void setLayout() {
        setLayout(new GridBagLayout());

        KeyStroke ksENTER = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getInputMap(IFW).put(ksENTER, "ENTER");
        getActionMap().put("ENTER", enterAction);

        createList();

        jList.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jList.requestFocus();
                updateList();
            }
        });
    }

    private void createList() {
        jList = new JList<>(getLocalHistoryList().toArray());
        jList.setSelectionModel(new ListSelectionModelN(jList, 2));
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new LocalHistoryCellRenderer());
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
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

    private ArrayList<File> getLocalHistoryList() {
        File[] files = LocalHistory.getFolder().listFiles();
        if (files == null) return new ArrayList<>(0);

        String name = LocalHistory.getHistoryFileNameForEditor(EditorWrapper.getActiveEditor());
        ArrayList<File> fileHist = new ArrayList<>(50);
        for (File file : files) {
            if (file.getName().startsWith(name)) {
                fileHist.add(file);
            }
        }
        Collections.reverse(fileHist);
        return fileHist;
    }

    private void startComparison() {
        int[] indices = jList.getSelectedIndices();
        try {
            if (indices.length == 0) {
                return;
            } else if (indices.length == 1) {
                LocalHistory.compare((File) jList.getSelectedValue(), EditorWrapper.getFile());
            } else {
                java.util.List<Object> items = jList.getSelectedValuesList();
                LocalHistory.compare((File) items.get(0), (File) items.get(1));
            }
        } catch (MatlabInvocationException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    "There was a problem comparing files.",
                    "Comparison Problem",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    public void showDialog() {
        updateList();
        jList.requestFocus();
        setVisible(true);
    }

    public void updateList() {
        jList.setListData(getLocalHistoryList().toArray());
    }

    public static LocalHistoryViewer getInstance() {
        if (instance == null) instance = new LocalHistoryViewer();
        return instance;
    }

}

