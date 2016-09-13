package at.justin.matlab.gui.fileStructure;

import at.justin.matlab.meta.Meta;
import at.justin.matlab.meta.MetaClass;
import at.justin.matlab.meta.MetaMethod;
import at.justin.matlab.meta.MetaProperty;
import at.justin.matlab.util.NodeUtils;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by z0032f1t on 02.08.2016.
 * <p>
 * Represents a Node in JTree and also an Matlab MTree.Node class.
 */
public class Node extends DefaultMutableTreeNode {
    private static final Pattern staticFalse = Pattern.compile("(Static|Constant)\\s*=\\s*false");
    private static final Pattern accessPrivate = Pattern.compile("Access\\s*=\\s*private");
    private static final Pattern setAccessPrivate = Pattern.compile("SetAccess\\s*=\\s*private");
    private static final Pattern getAccessPrivate = Pattern.compile("GetAccess\\s*=\\s*private");

    private MTree.Node node; // might not always be set, e.g.: First node is just the string of the filename
    private String nodeText = "DEFAULT NODE TEXT";
    private MTree.NodeType nodeType = MTree.NodeType.JAVA_NULL_NODE;
    private MetaNodeType metaNodeType = MetaNodeType.INVALID;
    private Meta meta = null;

    // custom Node properties for metaClass
    private boolean isStatic = false;  // same as constant
    private boolean isSealed = false;
    private boolean isAbstract = false;
    private boolean isConstructOnlOad = false;
    private boolean isHandleCompatible = false;
    private boolean isEnumeration = false;
    private boolean isDependent = false;
    private boolean isTransient = false;
    private boolean isImmutable = false;
    private boolean isPrivate = false;
    private boolean isHidden = false;
    private boolean isGetAccessPrivate = false;
    private boolean isSetAccessPrivate = false;
    private boolean hasDefaults = false;
    private String documentation = "";
    private String detailedDocumentation = "";

    public Node(MTree.Node node) {
        super(node);
        this.node = node;
        this.nodeText = NodeUtils.getTextFormattedForNode(node);
        this.nodeType = node.getType();
        metaNodeType = MetaNodeType.MATLAB;
    }

    public Node(MetaClass c, MTree.Node mtNode) {
        metaNodeType = MetaNodeType.META_CLASS;
        meta = c;
        node = mtNode;

        isSealed = c.isSealed();
        isAbstract = c.isAbstract();
        isHidden = c.isHidden();
        isConstructOnlOad = c.isConstructOnLoad();
        isHandleCompatible = c.isHandleCompatible();
        isEnumeration = c.isEnumeration();

        nodeText = c.getName();
        documentation = c.getDescription();
        detailedDocumentation = c.getDetailedDescription();
    }

    public Node(MetaProperty p, MTree.Node mtNode) {
        metaNodeType = MetaNodeType.META_PROPERTY;
        meta = p;
        node = mtNode;

        isGetAccessPrivate = p.getGetAccess().equals("private");
        isSetAccessPrivate = p.getSetAccess().equals("private");
        isAbstract = p.isAbstract();
        isHidden = p.isHidden();
        isDependent = p.isDependent();
        isStatic = p.isConstant();
        isTransient = p.isTransient();
        hasDefaults = p.isHasDefaults();

        nodeText = p.getName();
        documentation = p.getDescription();
        detailedDocumentation = p.getDetailedDescription();
    }

    public Node(MetaMethod m, MTree.Node mtNode) {
        metaNodeType = MetaNodeType.META_METHOD;
        meta = m;
        node = mtNode;

        isPrivate = m.getAccess().equals("private");
        isStatic = m.isStatic();
        isAbstract = m.isAbstract();
        isHidden = m.isHidden();
        isSealed = m.isSealed();

        String s = "";
        if (m.getOutputNames().size() > 0) {
            s = m.getOutputNames().toString() + " = ";
        }
        s += m.getName() + "(";
        if (m.getInputNames().size() > 0) {
            String ins = m.getInputNames().toString();
            s += ins.substring(1, ins.length() - 1);
        }
        s += ")";
        nodeText = s;
        documentation = m.getDescription();
        detailedDocumentation = m.getDetailedDescription();
    }

    public Node(String nodeText) {
        metaNodeType = MetaNodeType.STRING;
        this.nodeText = nodeText;
    }

    public MTree.Node node() {
        return node;
    }

    public boolean hasNode() {
        return node != null;
    }

    public MetaNodeType getMetaNodeType() {
        return metaNodeType;
    }

    public String nodeText() {
        return nodeText;
    }


    public MTree.NodeType getType() {
        return nodeType;
    }

    public String getDocumentation() {
        if (documentation == null || documentation.length() < 1) {
           documentation = nodeDocumentation();
        }
        return documentation;
    }

    public String getDetailedDocumentation() {
        return detailedDocumentation;
    }

    public boolean isProperty() {
        if (metaNodeType == MetaNodeType.MATLAB && nodeType == MTree.NodeType.ID) {
            return true;
        }
        return metaNodeType == MetaNodeType.META_PROPERTY;
    }

    public boolean isStatic() {
        // (metaNodeType == MetaNodeType.META_PROPERTY || metaNodeType == MetaNodeType.META_METHOD) &&
        // isStatic is only set in Meta<Class,Property,Method> constructors, otherwise it's always false
        // so it is safe to just return isStatic
        return isStatic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Meta getMeta() {
        return meta;
    }

    public boolean isHidden() {
        return isHidden;
    }

    private String nodeDocumentation() {
        if (!(getType() == MTree.NodeType.FUNCTION || getType() == MTree.NodeType.CLASSDEF
                || getMetaNodeType() == MetaNodeType.META_CLASS || getMetaNodeType() == MetaNodeType.META_METHOD)) {
            return "";
        }
        List<MTree.Node> nodeList = NodeUtils.getDocumentationNodesForNode(node);
        String s = "";
        for (MTree.Node node : nodeList) {
            s += NodeUtils.getTextForNode(node).trim() + "\n";
        }
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
