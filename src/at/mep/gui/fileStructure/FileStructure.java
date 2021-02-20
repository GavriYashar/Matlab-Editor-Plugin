package at.mep.gui.fileStructure;


import at.mep.editor.EditorWrapper;
import at.mep.gui.components.DockableFrame;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.prefs.Settings;
import at.mep.util.KeyStrokeUtil;
import at.mep.util.TreeUtilsV2;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Created by Andreas Justin on 2016 - 02 - 24. */
public class FileStructure extends DockableFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static FileStructure instance;

    /** should prevent on changing the state of inherited when opening file structure */
    private static boolean wasHidden = true;
    private static Editor activeEditor;
    private static JTextFieldSearch jTFS;
    private static JRadioButton functions = new JRadioButton("Functions", true);
    private static JRadioButton sections = new JRadioButton("Sections", false);
    private static JRadioButton classes = new JRadioButton("Class", false);
    private static JCheckBox regex = new JCheckBox("<html>regex <font color=#8F8F8F>(CTRL + R)</font></html>", true);
    private static JCheckBox inherited = new JCheckBox("<html>inherited <font color=#8F8F8F>($KEY)</font></html>");
    private static CMGenerate contextMenu = new CMGenerate();
    private JTreeFilter jTree;
    private AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTree.getMaxSelectionRow() < 0) return;
            NodeFS nodeFS = (NodeFS) jTree.getSelectionPath().getLastPathComponent();
            if (nodeFS.hasNode()) {
                if (nodeFS.isInherited()) {
                    EditorWrapper.openEditor(nodeFS.getFile());
                }
                EditorWrapper.goToLine(nodeFS.node().getStartLine(), false);
                escAction.actionPerformed(new ActionEvent(e, 0, null));
            }
            jTFS.setText("");
        }
    };

    private AbstractAction updateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            findPattern(jTFS.getText());
            if (jTree.getRowCount() > 1) {
                jTree.setSelectionRow(1);
            }
        }
    };

    @SuppressWarnings("WeakerAccess")
    private FileStructure() {
        super(EViewer.FILE_STRUCTURE);
        setLayout();
        populateTree();
        addFocusListener(jTFS);
    }

    public static FileStructure getInstance() {
        if (instance == null) instance = new FileStructure();
        return instance;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            wasHidden = true;
        }
    }

    public void showDialog() {
        setVisible(true);
        jTFS.setText("");
        jTFS.requestFocus();
        // Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        // component.setBackground(ColorUtils.complementary(component.getBackground()));
    }


    @SuppressWarnings("WeakerAccess")
    public void expandAll() {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    @SuppressWarnings("unused")
    public void collapseAll() {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.collapseRow(i);
        }
    }

    private void setLayout() {
        setName("FileStructureViewer");
        setLayout(new GridBagLayout());

        KeyStroke ksENTER = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getInputMap(IFW).put(ksENTER, "ENTER");
        getActionMap().put("ENTER", enterAction);

        // creating search box
        createSearchField();
        GridBagConstraints cjtfs = new GridBagConstraints();
        cjtfs.gridy = 0;
        cjtfs.gridx = 0;
        cjtfs.weightx = 1;
        cjtfs.fill = GridBagConstraints.BOTH;
        add(jTFS, cjtfs);

        //creating radio buttons for selecting category
        JPanel panelSettings = createSettingsPanel();
        GridBagConstraints cSet = new GridBagConstraints();
        cSet.gridy = 1;
        cSet.gridx = 0;
        cSet.weighty = 0.15;
        cSet.weightx = 1;
        cSet.fill = GridBagConstraints.BOTH;
        add(panelSettings, cSet);

        //create the jTree by passing in the root node
        JScrollPane scrollPaneTree = createTree();
        GridBagConstraints cSP = new GridBagConstraints();
        cSP.gridy = 2;
        cSP.gridx = 0;
        cSP.weighty = 1;
        cSP.weightx = cSet.weightx;
        cSP.fill = GridBagConstraints.BOTH;
        add(scrollPaneTree, cSP);
    }

    private void createSearchField() {
        jTFS = new JTextFieldSearch(30);
        jTFS.getDocument().addDocumentListener(new DocumentListener() {
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
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksU, "UP");
        jTFS.getActionMap().put("UP", new AbstractAction("UP") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTree.getMaxSelectionRow(); // single selection
                if (row < 0) {
                    jTree.setSelectionRow(0);
                }
                jTree.setSelectionRow(row - 1); // zero is top, so up means -1
                jTree.scrollRowToVisible(jTree.getMaxSelectionRow());
            }
        });

        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_DOWN);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksD, "DOWN");
        //noinspection Duplicates
        jTFS.getActionMap().put("DOWN", new AbstractAction("DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTree.getMaxSelectionRow(); // single selection
                if (row < 0) {
                    jTree.setSelectionRow(0);
                }
                if (jTree.getRowCount() - 1 > row) {
                    jTree.setSelectionRow(row + 1); // zero is top, so down means +1
                }
                jTree.scrollRowToVisible(jTree.getMaxSelectionRow());
            }
        });

        addEnterAction(jTFS, enterAction);
    }

    private JPanel createSettingsPanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(sections);
        bg.add(functions);
        bg.add(classes);
        final JPanel panelSettings = new JPanel();
        panelSettings.setBorder(BorderFactory.createTitledBorder("Type"));
        panelSettings.setLayout(new FlowLayout());
        panelSettings.add(sections);
        panelSettings.add(functions);
        panelSettings.add(classes);
        panelSettings.add(inherited);
        panelSettings.add(regex);

        ActionListener actionListener = e -> populate();
        sections.addActionListener(actionListener);
        functions.addActionListener(actionListener);
        classes.addActionListener(actionListener);

        String txt = inherited.getText().replace("$KEY",Settings.getProperty("kb.fileStructure"));
        txt = txt.replace("CONTROL", "CTRL");
        inherited.setText(txt);

        KeyStroke ksR = KeyStroke.getKeyStroke("control released R");
        getInputMap(IFW).put(ksR, "CTRL + R");
        getActionMap().put("CTRL + R", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regex.setSelected(!regex.isSelected());
            }
        });

        KeyStroke ksFS = Settings.getPropertyKeyStroke("kb.fileStructure");
        getInputMap(IFW).put(ksFS, Settings.getProperty("kb.fileStructure"));
        getActionMap().put(Settings.getProperty("kb.fileStructure"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inherited.isEnabled()) return;
                if (wasHidden) {
                    wasHidden = false;
                    return;
                }
                inherited.setSelected(!inherited.isSelected());
                populate();
            }
        });
        inherited.addItemListener(e -> populate());

        return panelSettings;
    }

    private JScrollPane createTree() {
        jTree = new JTreeFilter();
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPaneTree = new JScrollPane(jTree);
        scrollPaneTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneTree.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1 && e.getButton() == 1) {
                    enterAction.actionPerformed(new ActionEvent(e, 0, null));
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // right click

                    if (jTree.getSelectionPath() == null) {
                        jTree.setSelectionRow(jTree.getRowForLocation(e.getX(), e.getY()));
                    }

                    NodeFS nodeFS = (NodeFS) jTree.getSelectionPath().getLastPathComponent();
                    if (nodeFS.hasNode()) {
                        if (nodeFS.isInherited() || !nodeFS.isProperty()) {
                            return;
                        }

                        // TODO: nodeFS should have a method to just get the node itself so finding space is not necessary
                        String property = nodeFS.nodeText();
                        property = property.substring(0,property.indexOf(' '));
                        contextMenu.modifyProperty(property);
                        contextMenu.show(jTree, e.getX(), e.getY());
                    }
                }
            }
        });
        return scrollPaneTree;
    }

    private void findPattern(String pattern) {
        //noinspection Duplicates
        if (regex.isSelected()) {
            try {
                Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                jTFS.setForeground(null);
                setTreeRoot(jTree.filter(p), true);
            } catch (PatternSyntaxException e) {
                jTFS.setForeground(Color.RED);
            }
        } else {
            setTreeRoot(jTree.filter(pattern), true);
        }
    }

    /** for radio buttons */
    private void populate() {
        String shortName;
        try {
            shortName = EditorWrapper.getShortName();
        } catch (Exception e) {
            e.getStackTrace();
            return;
        }
        NodeFS root = new NodeFS(shortName);

        MTree.NodeType nodeType;
        //noinspection Duplicates
        if (sections.isSelected()) {
            nodeType = MTree.NodeType.CELL_TITLE;
            root = NodeFS.constructForCellTitle(activeEditor);
        } else if (functions.isSelected()) {
            nodeType = MTree.NodeType.FUNCTION;
            root = NodeFS.constructForFunctions(activeEditor);
        } else if (classes.isSelected()) {
            nodeType = MTree.NodeType.CLASSDEF;
            root = NodeFS.constructForClassDef(activeEditor, inherited.isSelected());
        } else {
            nodeType = MTree.NodeType.CELL_TITLE;
            sections.setSelected(true);
        }

        inherited.setEnabled(classes.isSelected());
        // inherited.setEnabled(false);

        MTree mTree = EditorWrapper.getMTreeFast(activeEditor);
        Tree<MTree.Node> nodeTree = mTree.findAsTree(nodeType);
        if (nodeType.equals(MTree.NodeType.CLASSDEF) & nodeTree.getChildCount(nodeTree.getRoot()) < 1) {
            // class was selected, but file is no class
            root = EditorWrapper.getFunctionNodeFast(activeEditor);
        }
        setTreeRoot(root, false);
    }

    public void populateTree() {
        if (!Settings.getPropertyBoolean("feature.enableFileStructure")) {
            return;
        }
        if (EditorWrapper.getActiveEditor() == null || instance == null) return;
        if (activeEditor != EditorWrapper.getActiveEditor()) {
            jTFS.setText(""); // resetting search if activeEditor has been changed
            activeEditor = EditorWrapper.getActiveEditor();
            setDefaultSettings();
        }
        populate();
    }

    @SuppressWarnings("WeakerAccess")
    public void setDefaultSettings() {
        MTree mTree = EditorWrapper.getMTree();
        //noinspection Duplicates
        switch (TreeUtilsV2.getFileType(mTree)) {
            case ScriptFile:
                classes.setEnabled(false);
                functions.setEnabled(false);
                sections.setEnabled(true);

                sections.setSelected(true);
                break;
            case FunctionFile:
                classes.setEnabled(false);
                functions.setEnabled(true);
                sections.setEnabled(true);

                functions.setSelected(true);
                break;
            case ClassDefinitionFile:
                classes.setEnabled(true);
                functions.setEnabled(true);
                sections.setEnabled(true);

                classes.setSelected(true);
                break;
            case Unknown:
                classes.setEnabled(true);
                functions.setEnabled(true);
                sections.setEnabled(true);
                break;
        }
    }

    private void setTreeRoot(NodeFS root, boolean filtered) {
        if (!filtered) {
            jTree.setOriginalRoot(root);
        }
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
        expandAll();
    }

}