package at.justin.matlab.fileStructure;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */

import at.justin.matlab.EditorWrapper;
import at.justin.matlab.gui.components.JTextFieldSearch;
import at.justin.matlab.gui.components.JTreeFilter;
import at.justin.matlab.gui.components.UndecoratedFrame;
import at.justin.matlab.util.KeyStrokeUtil;
import at.justin.matlab.util.ScreenSize;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileStructure extends UndecoratedFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static FileStructure INSTANCE;
    private JTreeFilter jTree;

    private EditorWrapper ew;

    private JTextFieldSearch jTFS;
    private JRadioButton functions = new JRadioButton("Functions", true);
    private JRadioButton cells = new JRadioButton("Cells", false);
    private JRadioButton classes = new JRadioButton("Class", false);
    private JCheckBox regex = new JCheckBox("<html>regex <font color=#8F8F8F>(CTRL + F12)</font></html>");

    public FileStructure() {
        setLayout();
    }

    public static FileStructure getINSTANCE() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new FileStructure();
        return INSTANCE;
    }

    private void setLayout() {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        setUndecorated(true);
        setSize(500, 600);
        setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);

        getRootPane().setLayout(new GridBagLayout());

        // creating search box
        createSearchField();
        GridBagConstraints cjtfs = new GridBagConstraints();
        cjtfs.gridy = 0;
        cjtfs.gridx = 0;
        cjtfs.weightx = 1;
        cjtfs.fill = GridBagConstraints.BOTH;
        getRootPane().add(jTFS, cjtfs);

        //creating radio buttons for selecting category
        JPanel panelSettings = createSettingsPanel();
        GridBagConstraints cSet = new GridBagConstraints();
        cSet.gridy = 1;
        cSet.gridx = 0;
        cSet.weightx = 1;
        cSet.fill = GridBagConstraints.BOTH;
        getRootPane().add(panelSettings, cSet);

        //create the jTree by passing in the root node
        JScrollPane scrollPaneTree = createTree();
        GridBagConstraints cSP = new GridBagConstraints();
        cSP.gridy = 2;
        cSP.gridx = 0;
        cSP.weighty = 1;
        cSP.weightx = cSet.weightx;
        cSP.fill = GridBagConstraints.BOTH;
        getRootPane().add(scrollPaneTree, cSP);
    }

    private void createSearchField() {
        jTFS = new JTextFieldSearch(30);
        jTFS.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findPattern(jTFS.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findPattern(jTFS.getText());
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
            }
        });

        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_DOWN);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksD, "DOWN");
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
            }
        });
    }

    private JPanel createSettingsPanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(cells);
        bg.add(functions);
        bg.add(classes);
        JPanel panelSettings = new JPanel();
        panelSettings.setBorder(BorderFactory.createTitledBorder("Type"));
        panelSettings.setLayout(new FlowLayout());
        panelSettings.add(cells);
        panelSettings.add(functions);
        panelSettings.add(classes);
        panelSettings.add(regex);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ew != null) populate();
            }
        };
        cells.addActionListener(actionListener);
        functions.addActionListener(actionListener);
        classes.addActionListener(actionListener);

        KeyStroke ks = KeyStroke.getKeyStroke("control released F12");
        getRootPane().getInputMap(IFW).put(ks, "CTRL + F12");
        getRootPane().getActionMap().put("CTRL + F12", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regex.setSelected(!regex.isSelected());
            }
        });

        return panelSettings;
    }

    private JScrollPane createTree() {
        jTree = new JTreeFilter();
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPaneTree = new JScrollPane(jTree);
        scrollPaneTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneTree.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jTree.addMouseListener(mlClick);
        jTree.addMouseMotionListener(mlMove);
        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1) {
                    setVisible(false);
                }
            }
        });
        jTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                jTFS.requestFocus();
                if (jTree.getMaxSelectionRow() < 0) return;
                Node node = (Node) jTree.getSelectionPath().getLastPathComponent();
                if (node.hasNode()) {
                    EditorWrapper.getInstance().goToLine(node.node().getStartLine(), false);
                }
            }
        });
        return scrollPaneTree;
    }

    private void findPattern(String pattern) {
        if (regex.isSelected()) {
            try {
                Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                jTFS.setForeground(null);
                setTreeRoot(jTree.filter(p),true);
            } catch (PatternSyntaxException e) {
                jTFS.setForeground(Color.RED);
                return;
            }
        } else {
            setTreeRoot(jTree.filter(pattern),true);
        }
    }

    /** for radio buttons */
    private void populate() {
        MTree.NodeType nodeType;
        if (cells.isSelected()) {
            nodeType = MTree.NodeType.CELL_TITLE;
        } else if (functions.isSelected()) {
            nodeType = MTree.NodeType.FUNCTION;
        } else if (classes.isSelected()) {
            nodeType = MTree.NodeType.CLASSDEF;
        } else {
            nodeType = MTree.NodeType.CELL_TITLE;
            cells.setSelected(true);
        }

        Node root = new Node(ew.getShortName());
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());

        Tree<MTree.Node> nodeTree = mTree.findAsTree(nodeType);
        if (nodeType.equals(MTree.NodeType.CLASSDEF) & nodeTree.getChildCount(nodeTree.getRoot()) < 1) {
            // class was selected, but file is no class
            nodeType = MTree.NodeType.FUNCTION;
            nodeTree = mTree.findAsTree(nodeType);
        } else if (nodeType.equals(MTree.NodeType.CLASSDEF) & nodeTree.getChildCount(nodeTree.getRoot()) > 0) {
            root.add(forClass(mTree, nodeTree.getChild(nodeTree.getRoot(), 0)));
            setTreeRoot(root,false);
            return;
        }
        if (nodeTree.getChildCount(nodeTree.getRoot()) > 0) {
            for (int i = 0; i < nodeTree.getChildCount(nodeTree.getRoot()); i++) {
                MTree.Node node = nodeTree.getChild(nodeTree.getRoot(), i);
                root.add(new Node(node));
            }
            setTreeRoot(root,false);
        }
    }

    public void populate(final EditorWrapper ew) {
        this.ew = ew;

        // (disable/enable) class RadioButton if the current file (is no/is) class
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());

        Tree<MTree.Node> nodeTree = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        //classes.setEnabled((nodeTree.getChildCount(nodeTree.getRoot()) > 0));
        classes.setEnabled(false); // performance issue

        nodeTree = mTree.findAsTree(MTree.NodeType.FUNCTION);
        functions.setEnabled((nodeTree.getChildCount(nodeTree.getRoot()) > 0));

        nodeTree = mTree.findAsTree(MTree.NodeType.CELL_TITLE);
        cells.setEnabled((nodeTree.getChildCount(nodeTree.getRoot()) > 0));

        // preferred classes, if only functions or cells are available, radioButtons will be set accordingly
        classes.setSelected(classes.isEnabled());
        functions.setSelected(!classes.isEnabled() & !cells.isEnabled() & functions.isEnabled());
        cells.setSelected(!classes.isEnabled() & cells.isEnabled() & !functions.isEnabled());

        populate();
    }

    private void setTreeRoot(Node root,boolean filtered) {
        if (!filtered) {
            jTree.setOriginalRoot(root);
        }
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
        expandAll();
    }

    private Node forClass(MTree mTree, MTree.Node classDef) {
        Node classDefNode = new Node(classDef);

        Tree<MTree.Node> propertyTree = mTree.findAsTree(MTree.NodeType.PROPERTIES);
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);
        for (int i = 0; i < methodsTree.getChildCount(methodsTree.getRoot()); i++) {
            MTree.Node method = methodsTree.getChild(methodsTree.getRoot(), i);
            Node methodNode = new Node(method);

            List<MTree.Node> methodsSub = method.getSubtree();
            for (MTree.Node methodSub : methodsSub) {
                if (methodSub.getType() == MTree.NodeType.FUNCTION) {
                    methodNode.add(new Node(methodSub));
                }
            }
            classDefNode.add(methodNode);
        }
        return classDefNode;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setAlwaysOnTop(visible);
    }

    public void expandAll() {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    public void collapseAll() {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.collapseRow(i);
        }
    }
}
