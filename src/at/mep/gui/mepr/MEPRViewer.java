package at.mep.gui.mepr;

import at.mep.editor.EditorApp;
import at.mep.editor.EditorWrapper;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.gui.components.UndecoratedFrame;
import at.mep.mepr.EMEPRAction;
import at.mep.mepr.MEPR;
import at.mep.prefs.Settings;
import at.mep.util.*;
import com.mathworks.matlab.api.editor.Editor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2016-09-20. */
public class MEPRViewer extends UndecoratedFrame {
    private static Dimension dimension;
    private static MEPRViewer INSTANCE;
    private static JList<Object> jList;
    private static JTextFieldSearch jtfs;
    private static java.util.List<MEPREntry> mepEntries;
    private static JComboBox<Object> jComboBox;
    private AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            String action = "%" + ((MEPREntry) jList.getSelectedValue()).getAction();
            MEPR.prepareReplace(action, EMEPRAction.VIEWER);
            MEPR.doReplace();
        }
    };

    private AbstractAction updateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setJComboBoxAll();
            findPattern(jtfs.getText());
            if (jList.getModel().getSize() > 1) {
                jList.setSelectedIndex(0);
            }
            jList.ensureIndexIsVisible(jList.getSelectedIndex());
        }
    };

    private MEPRViewer() {
        dimension = Settings.getPropertyDimension("dim.MEPRViewer");
        Runnable runnable = this::setLayout;
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.MEPRViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MEPRViewer getInstance() {
        if (INSTANCE == null) INSTANCE = new MEPRViewer();
        return INSTANCE;
    }

    private void setLayout() {
        setTitle("MEPR Viewer");
        setResizable(true);
        setSize(dimension);
        setPreferredSize(dimension);
        rootPane.setLayout(new GridBagLayout());

        // searrch bar
        GridBagConstraints gbcSB = new GridBagConstraints();
        gbcSB.gridy = 0;
        gbcSB.gridx = 0;
        gbcSB.weightx = 1;
        gbcSB.gridwidth = 3;
        gbcSB.fill = GridBagConstraints.HORIZONTAL;
        gbcSB.insets = new Insets(15, 5, 0, 5);
        addSearchBar(gbcSB);

        // action list
        GridBagConstraints gbcSP = new GridBagConstraints();
        gbcSP.gridy = 2;
        gbcSP.gridx = 0;
        gbcSP.weightx = 1;
        gbcSP.weighty = 1;
        gbcSP.gridwidth = 3;
        gbcSP.fill = GridBagConstraints.BOTH;
        gbcSP.insets = new Insets(5, 5, 5, 5);
        addScrollPane(gbcSP);

        // dropdown menu for tags
        GridBagConstraints gbcDD = new GridBagConstraints();
        gbcDD.gridy = 1;
        gbcDD.gridx = 0;
        gbcDD.weightx = 1;
        gbcDD.gridwidth = 3;
        gbcDD.fill = GridBagConstraints.HORIZONTAL;
        gbcDD.insets = new Insets(5, 5, 5, 5);
        addTagDropDown(gbcDD);

        // insert button
        GridBagConstraints gbcIB = new GridBagConstraints();
        gbcIB.gridy = 3;
        gbcIB.gridx = 0;
        gbcIB.weightx = 1;
        gbcIB.fill = GridBagConstraints.HORIZONTAL;
        gbcIB.insets = new Insets(5, 5, 5, 5);
        addInsertButton(gbcIB);

        // edit button
        GridBagConstraints gbcEB = new GridBagConstraints();
        gbcEB.gridy = 3;
        gbcEB.gridx = 1;
        gbcEB.weightx = 1;
        gbcEB.fill = GridBagConstraints.HORIZONTAL;
        gbcEB.insets = new Insets(5, 5, 5, 5);
        addEditButton(gbcEB);

        // new button
        GridBagConstraints gbcNB = new GridBagConstraints();
        gbcNB.gridy = 3;
        gbcNB.gridx = 2;
        gbcNB.weightx = 1;
        gbcNB.fill = GridBagConstraints.HORIZONTAL;
        gbcNB.insets = new Insets(5, 5, 5, 5);
        addNewButton(gbcNB);
    }

    private void addNewButton(GridBagConstraints gbc) {
        JButton insert = new JButton("New Action");
        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String p = Settings.getProperty("path.mepr.rep");

                String name = JOptionPane.showInputDialog(
                        new JFrame(),
                        "Enter LiveTemplate name",
                        "new Template",
                        JOptionPane.QUESTION_MESSAGE);
                if (name == null) return;
                File file = new File(p + "\\MEPR_" + name + ".m");

                InputStream stream = MEPRViewer.class.getResourceAsStream("/template.txt");
                String template = FileUtils.readInputStreamToString(stream);

                Editor editor = EditorWrapper.getMatlabEditorApplication().newEditor(template);
                try {
                    editor.saveAs(file.getPath());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(
                            new JFrame(""),
                            e1.getMessage() + "\ncouldn't save file: " + file.getPath(),
                            "invalid file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        rootPane.add(insert, gbc);
    }

    private void addEditButton(GridBagConstraints gbc) {
        JButton insert = new JButton("Edit");
        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String p = Settings.getProperty("path.mepr.rep");
                String action = ((MEPREntry) jList.getSelectedValue()).getAction();

                File file = new File(p + "\\MEPR_" + action + ".m");
                if (file.exists()) {
                    EditorApp.getInstance().openEditor(file);
                } else {
                    JOptionPane.showMessageDialog(
                            new JFrame(""),
                            "File does not exist: " + file.getPath(),
                            "invalid file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        rootPane.add(insert, gbc);
    }

    private void addInsertButton(GridBagConstraints gbc) {
        JButton insert = new JButton("Insert");
        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterAction.actionPerformed(new ActionEvent(e, 0, null));
            }
        });
        rootPane.add(insert, gbc);
    }

    private void addSearchBar(GridBagConstraints gbc) {
        jtfs = new JTextFieldSearch(20);
        jtfs.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAction.actionPerformed(null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAction.actionPerformed(null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        KeyStroke ksU = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_UP);
        jtfs.getInputMap(JComponent.WHEN_FOCUSED).put(ksU, "UP");
        jtfs.getActionMap().put("UP", new AbstractAction("UP") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jList.getSelectedIndex(); // single selection
                if (row < 0) {
                    jList.setSelectedIndex(0);
                }
                jList.setSelectedIndex(row - 1); // zero is top, so up means -1
            }
        });

        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_DOWN);
        jtfs.getInputMap(JComponent.WHEN_FOCUSED).put(ksD, "DOWN");
        jtfs.getActionMap().put("DOWN", new AbstractAction("DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jList.getSelectedIndex(); // single selection
                if (row < 0) {
                    jList.setSelectedIndex(0);
                }
                if (jList.getModel().getSize() - 1 > row) {
                    jList.setSelectedIndex(row + 1); // zero is top, so down means +1
                }
            }
        });

        KeyStroke ksE = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        jtfs.getInputMap(JComponent.WHEN_FOCUSED).put(ksE, "ENTER");
        jtfs.getActionMap().put("ENTER", new AbstractAction("ENTER") {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterAction.actionPerformed(new ActionEvent(e, 0, null));
            }
        });

        rootPane.add(jtfs, gbc);
    }

    private void addScrollPane(GridBagConstraints gbc) {
        jList = new JList<>();
        updateList();
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new MEPRCellRenderer());

        JScrollPane jsp = new JScrollPane(jList);
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        jsp.getHorizontalScrollBar().setUnitIncrement(20);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jList.addMouseListener(mlClick);
        jList.addMouseMotionListener(mlMove);
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1) {
                    enterAction.actionPerformed(new ActionEvent(e, 0, null));
                }
            }
        });

        rootPane.add(jsp, gbc);
    }

    private void setJComboBoxAll() {
        int index = 0;
        for (int i = 0; i < jComboBox.getItemCount(); i++) {
            if (jComboBox.getItemAt(i).equals("all")) {
                index = i;
            }
        }
        jComboBox.setSelectedIndex(index);
    }

    private void addTagDropDown(GridBagConstraints gbc) {
        List<String> entries = MEPREntries.getAllTags();
        entries.add(0, "all");
        jComboBox = new JComboBox<>(entries.toArray());
        setJComboBoxAll();
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findTag((String) jComboBox.getSelectedItem());
            }
        });
        rootPane.add(jComboBox, gbc);
    }

    private void findPattern(String pattern) {
        java.util.List<MEPREntry> copied = new ArrayList<>(mepEntries.size());
        for (MEPREntry entry : mepEntries) {
            if (entry.getAction().toLowerCase().contains(pattern.toLowerCase())
                    || entry.getComment().toLowerCase().contains(pattern.toLowerCase())) {
                copied.add(entry);
            }
        }
        jList.setListData(copied.toArray());
    }

    private void findTag(String tag) {
        if (tag.equals("all")) {
            jList.setListData(mepEntries.toArray());
            return;
        }
        java.util.List<MEPREntry> copied = new ArrayList<>(mepEntries.size());
        for (MEPREntry entry : mepEntries) {
            String[] tags = entry.getTags();
            for (String tagEntry : tags) {
                if (tagEntry.toLowerCase().equals(tag.toLowerCase())) {
                    copied.add(entry);
                }
            }
        }
        jList.setListData(copied.toArray());
    }

    public void updateList() {
        mepEntries = MEPREntries.getAllEntries();
        jList.setListData(mepEntries.toArray());
    }

    public void showDialog() {
        this.setVisible(true);
        this.setLocation(ScreenSize.getCenter(this.getSize()));
        updateList();
        // ISSUE: #36
        // findPattern(jtfs.getText()); // show last search (if editor has not been changed @populate)
        jtfs.setText("");
    }

    public void quickSearch() {
        updateList();
        String action = MEPR.getAction();
        if (!action.startsWith("%")) return;

        List<MEPREntry> foundEntries = new ArrayList<>(mepEntries.size());

        for (MEPREntry entry : mepEntries) {
            if (entry.getAction().toLowerCase().contains(action.toLowerCase().substring(1)))
                foundEntries.add(entry);
        }

        if (foundEntries.size() == 1) {
            int end = EditorWrapper.getSelectionPositionStart();
            MEPR.prepareReplace("%" + foundEntries.get(0).getAction(), EMEPRAction.QUICKSEARCH);
            // MEPR.setRepText("%" + foundEntries.get(0).getAction());
            MEPR.setSelectionSE(new int[]{end - action.length(), end});
            MEPR.doReplace();
            return;
        }

        System.out.println("======== found templates ========");
        for (MEPREntry entry : foundEntries) {
            System.out.println("\t" + entry.getAction());
        }
        System.out.println("=================================");

    }

}
