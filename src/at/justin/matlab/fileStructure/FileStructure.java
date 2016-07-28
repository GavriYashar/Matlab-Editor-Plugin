package at.justin.matlab.fileStructure;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */
import at.justin.matlab.EditorWrapper;
import at.justin.matlab.util.NodeUtils;
import at.justin.matlab.util.ScreenSize;
import at.justin.matlab.util.UndecoratedFrame;
import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;
import javafx.scene.control.RadioButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
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

public class FileStructure extends UndecoratedFrame
{
    private static FileStructure INSTANCE;
    private JTree jTree;
    private ArrayList<MTree.Node> positionNodes;
    private EditorWrapper ew;
    private JRadioButton functions = new JRadioButton("Functions", true);
    private JRadioButton cells = new JRadioButton("Cells", false);

    public FileStructure()
    {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        setUndecorated(true);
        setSize(500, 600);
        setLocation(width/2 - getWidth()/2,height/2 - getHeight()/2);

        getRootPane().setLayout(new GridBagLayout());
        // getRootPane().setBorder(new LineBorder(new Color(255,0, 255),3));

        //creating radio buttons for selecting category
        JPanel panelRadioButton = createPanelRadioButton();
        GridBagConstraints cRB = new GridBagConstraints();
        cRB.gridy = 0; cRB.gridx = 0; cRB.weightx = 1;
        cRB.fill = GridBagConstraints.BOTH;
        getRootPane().add(panelRadioButton,cRB);

        //create the jTree by passing in the root node
        jTree = new JTree();
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPaneTree = new JScrollPane(jTree);
        scrollPaneTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneTree.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // scrollPaneTree.setBorder(new LineBorder(new Color(255,255,0),3));

        GridBagConstraints cSP = new GridBagConstraints();
        cSP.gridy = 1; cSP.gridx = 0;
        cSP.weighty = 1-cRB.weighty; cSP.weightx = cRB.weightx;
        cSP.fill = GridBagConstraints.BOTH;
        getRootPane().add(scrollPaneTree,cSP);

        addListeners();
    }

    private JPanel createPanelRadioButton() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(cells);
        bg.add(functions);
        JPanel panelRadioButton = new JPanel();
        panelRadioButton.setBorder(BorderFactory.createTitledBorder("Type"));
        panelRadioButton.setLayout(new FlowLayout());
        panelRadioButton.add(cells);
        panelRadioButton.add(functions);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ew != null) populate();
                // System.out.println("active: " + ((AbstractButton) e.getSource()).getText());
            }
        };
        cells.addActionListener(actionListener);
        functions.addActionListener(actionListener);

        return panelRadioButton;
    }

    private void addListeners() {
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
                int index = jTree.getMaxSelectionRow()-1;
                System.out.println("index = " + index);
                if (index < 0) index = 0;
                if (index > positionNodes.size()-1) {
                    System.out.println("Selected Index " + index+1 + " is greater than size of positions " + positionNodes.size());
                    return;
                }
                System.out.println(positionNodes.get(index));
                System.out.println("going to line: " + positionNodes.get(index).getStartLine() + " index = " + index);
                EditorWrapper.getInstance().goToLine(positionNodes.get(index).getStartLine(),false);
            }
        });
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
            root.add(forClass(mTree, classdef.getChild(classdef.getRoot(),0)));
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
                MTree.Node node = section.getChild(section.getRoot(),i);
                if (nodeType.equals(MTree.NodeType.CELL_TITLE)) {
                    root.add(new DefaultMutableTreeNode(NodeUtils.getCellName(node)));
                } else if (nodeType.equals(MTree.NodeType.FUNCTION)) {
                    root.add(new DefaultMutableTreeNode(NodeUtils.getFunctionHeader(node,true)));
                }
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