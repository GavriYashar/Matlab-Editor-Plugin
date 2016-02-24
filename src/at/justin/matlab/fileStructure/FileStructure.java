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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class FileStructure extends UndecoratedFrame
{
    private JTree tree;
    private static FileStructure INSTANCE;

    public FileStructure()
    {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        setUndecorated(true);
        setSize(300, 600);
        setLocation(width/2 - getWidth()/2,height/2 - getHeight()/2);

        //create the tree by passing in the root node
        tree = new JTree();
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
        addListeners();
    }

    private void addListeners() {
        tree.addKeyListener(closeListener);
        tree.addMouseListener(mlClick);
        tree.addMouseMotionListener(mlMove);
    }


    public void populate(final EditorWrapper ew) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ew.getShortName());
        MTree mTree = MTree.parse(EditorWrapper.getInstance().gae().getText());
        Tree<MTree.Node> classdef = mTree.findAsTree(MTree.NodeType.CLASSDEF);
        if (classdef != null) {
            root.add(forClass(mTree, classdef.getChild(classdef.getRoot(),0)));
        }
        tree.setModel(new DefaultTreeModel(root));
    }

    private DefaultMutableTreeNode forClass(MTree mTree, MTree.Node classdef) {
        DefaultMutableTreeNode classdefNode = new DefaultMutableTreeNode(NodeUtils.getClassdef(classdef));
        Tree<MTree.Node> methodsTree = mTree.findAsTree(MTree.NodeType.METHODS);

        for (int i = 0; i < methodsTree.getChildCount(methodsTree.getRoot()); i++) {
            MTree.Node method = methodsTree.getChild(methodsTree.getRoot(), i);
            DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(NodeUtils.getMethodHeader(method));

            List<MTree.Node> methodsSub = method.getSubtree();
            for (int j = 0; j < methodsSub.size(); j++) {
                if (methodsSub.get(j).getType() == MTree.NodeType.FUNCTION) {
                    methodNode.add(new DefaultMutableTreeNode(NodeUtils.getFunctionHeader(methodsSub.get(j),true)));
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

    public static FileStructure getINSTANCE() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new FileStructure();
        return INSTANCE;
    }
}
