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

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FileStructure extends UndecoratedFrame
{
    private static FileStructure INSTANCE;
    private JTree jTree;
    private ArrayList<MTree.Node> positionNodes = new ArrayList<>(10);

    public FileStructure()
    {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        setUndecorated(true);
        setSize(500, 600);
        setLocation(width/2 - getWidth()/2,height/2 - getHeight()/2);

        //create the jTree by passing in the root node
        jTree = new JTree();
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(jTree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
        setResizable(true);
        addListeners();
    }

    private void addListeners() {
        jTree.addKeyListener(closeListener);
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


    public void populate(final EditorWrapper ew) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ew.getShortName());
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());
        Tree<MTree.Node> classdef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        if (classdef.getChildCount(classdef.getRoot()) > 0) {
            root.add(forClass(mTree, classdef.getChild(classdef.getRoot(),0)));
        }
        Tree<MTree.Node> section = mTree.findAsTree(MTree.NodeType.CELL_TITLE);
        System.out.println("adding section " + section.getChildCount(section.getRoot()));
        if (section.getChildCount(section.getRoot()) > 0) {
            for (int i = 0; i < section.getChildCount(section.getRoot()); i++) {
                MTree.Node node = section.getChild(section.getRoot(),i);
                root.add(new DefaultMutableTreeNode(NodeUtils.getCellName(node)));
                positionNodes.add(node);
            }
        }
        jTree.setModel(new DefaultTreeModel(root));
        jTree.setCellRenderer(new TreeRenderer());
        expandAll();
    }

    private DefaultMutableTreeNode forClass(MTree mTree, MTree.Node classdef) {
        DefaultMutableTreeNode classdefNode = new DefaultMutableTreeNode(NodeUtils.getClassdef(classdef));
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);

        EditorWrapper ew = EditorWrapper.getInstance();
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
