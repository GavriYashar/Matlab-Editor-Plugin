package at.justin.matlab.fileStructure;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */

import at.justin.matlab.EditorWrapper;
import at.justin.matlab.gui.components.JTextFieldSearch;
import at.justin.matlab.util.NodeUtils;
import at.justin.matlab.util.ScreenSize;
import at.justin.matlab.gui.components.UndecoratedFrame;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileStructure extends UndecoratedFrame {
    private static FileStructure INSTANCE;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    private JTree jTree;
    private ArrayList<MTree.Node> positionNodes;

    private String searchExpr = "";
    private List<String> populatedStrings = new ArrayList<>(30);

    private EditorWrapper ew;

    private JTextFieldSearch jtfs;
    private JRadioButton functions = new JRadioButton("Functions", true);
    private JRadioButton cells = new JRadioButton("Cells", false);
    private JCheckBox regex = new JCheckBox("<html>regex <font color=#8F8F8F>(CTRL + F12)</font></html>");

    public FileStructure() {
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
        getRootPane().add(jtfs, cjtfs);

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
        jtfs = new JTextFieldSearch(60);
        jtfs.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findPattern(jtfs.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findPattern(jtfs.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void findPattern(String pattern) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ew.getShortName());
        Pattern p = Pattern.compile(".*");
        if (regex.isSelected()) {
            try {
                p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
                jtfs.setForeground(null);
            } catch (PatternSyntaxException e) {
                jtfs.setForeground(Color.RED);
                return;
            }
        }
        for (String str : populatedStrings) {
            if (!regex.isSelected() && str.toLowerCase().contains(pattern.toLowerCase())) {
                root.add(new DefaultMutableTreeNode(str));
            } else if (regex.isSelected() && p.matcher(str).find()) {
                root.add(new DefaultMutableTreeNode(str));
            }
        }
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
        expandAll();
    }

    private JPanel createSettingsPanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(cells);
        bg.add(functions);
        JPanel panelSettings = new JPanel();
        panelSettings.setBorder(BorderFactory.createTitledBorder("Type"));
        panelSettings.setLayout(new FlowLayout());
        panelSettings.add(cells);
        panelSettings.add(functions);
        panelSettings.add(regex);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ew != null) populate();
                // System.out.println("active: " + ((AbstractButton) e.getSource()).getText());
            }
        };
        cells.addActionListener(actionListener);
        functions.addActionListener(actionListener);

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
        jTree = new JTree();
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
                int index = jTree.getMaxSelectionRow() - 1;
                System.out.println("index = " + index);
                if (index < 0) index = 0;
                if (index > positionNodes.size() - 1) {
                    System.out.println("Selected Index " + index + 1 + " is greater than size of positions " + positionNodes.size());
                    return;
                }
                System.out.println(positionNodes.get(index));
                System.out.println("going to line: " + positionNodes.get(index).getStartLine() + " index = " + index);
                EditorWrapper.getInstance().goToLine(positionNodes.get(index).getStartLine(), false);
            }
        });
        return scrollPaneTree;
    }

    /**
     * for radio buttons
     */
    private void populate() {
        positionNodes = new ArrayList<>(10);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ew.getShortName());
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());
        Tree<MTree.Node> classdef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        if (classdef.getChildCount(classdef.getRoot()) > 0) {
            root.add(forClass(mTree, classdef.getChild(classdef.getRoot(), 0)));
        }

        MTree.NodeType nodeType;
        if (cells.isSelected()) {
            nodeType = MTree.NodeType.CELL_TITLE;
        } else if (functions.isSelected()) {
            nodeType = MTree.NodeType.FUNCTION;
        } else {
            nodeType = MTree.NodeType.CELL_TITLE;
            cells.setSelected(true);
        }

        Tree<MTree.Node> section = mTree.findAsTree(nodeType);
        if (section.getChildCount(section.getRoot()) > 0) {
            for (int i = 0; i < section.getChildCount(section.getRoot()); i++) {
                MTree.Node node = section.getChild(section.getRoot(), i);
                if (nodeType.equals(MTree.NodeType.CELL_TITLE)) {
                    populatedStrings.add(i, NodeUtils.getCellName(node));
                } else if (nodeType.equals(MTree.NodeType.FUNCTION)) {
                    populatedStrings.add(i, NodeUtils.getFunctionHeader(node, true));
                }
                root.add(new DefaultMutableTreeNode(populatedStrings.get(i)));
                positionNodes.add(node);
            }
        }
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
        expandAll();
    }

    public void populate(final EditorWrapper ew) {
        this.ew = ew;
        populate();
    }

    private DefaultMutableTreeNode forClass(MTree mTree, MTree.Node classdef) {
        DefaultMutableTreeNode classdefNode = new DefaultMutableTreeNode(NodeUtils.getClassdef(classdef));
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);

        positionNodes.add(classdef);
        for (int i = 0; i < methodsTree.getChildCount(methodsTree.getRoot()); i++) {
            MTree.Node method = methodsTree.getChild(methodsTree.getRoot(), i);
            DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(NodeUtils.getMethodHeader(method));

            positionNodes.add(method);
            List<MTree.Node> methodsSub = method.getSubtree();
            for (MTree.Node methodSub : methodsSub) {
                if (methodSub.getType() == MTree.NodeType.FUNCTION) {
                    methodNode.add(new DefaultMutableTreeNode(NodeUtils.getFunctionHeader(methodSub, true)));
                    positionNodes.add(methodSub);
                }
            }
            classdefNode.add(methodNode);
        }
        return classdefNode;
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

    public static FileStructure getINSTANCE() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new FileStructure();
        return INSTANCE;
    }

    public ArrayList<MTree.Node> getPositionNodes() {
        return positionNodes;
    }

    void dummy() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("classdef TEST");
        DefaultMutableTreeNode sub1 = new DefaultMutableTreeNode("methods (static = true)");
        DefaultMutableTreeNode sub2 = new DefaultMutableTreeNode("methods (Static = true, private)");

        DefaultMutableTreeNode subsub1 = new DefaultMutableTreeNode("[asdf] = test(obj)");

        sub1.add(subsub1);
        sub2.add((MutableTreeNode) subsub1.clone());
        sub2.add((MutableTreeNode) subsub1.clone());

        root.add(sub1);
        root.add(sub2);
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
    }
}