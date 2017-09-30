package at.mep.gui.fileStructure;

import at.mep.meta.*;
import at.mep.util.NodeUtils;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by z0032f1t on 02.08.2016.
 * <p>
 * Represents a NodeFS in JTree and also an Matlab MTree.NodeFS class.
 */
public class NodeFS extends DefaultMutableTreeNode {
    private static final Pattern staticFalse = Pattern.compile("(Static|Constant)\\s*=\\s*false");
    private static final Pattern accessPrivate = Pattern.compile("Access\\s*=\\s*private");
    private static final Pattern setAccessPrivate = Pattern.compile("SetAccess\\s*=\\s*private");
    private static final Pattern getAccessPrivate = Pattern.compile("GetAccess\\s*=\\s*private");

    private MTree.Node node; // might not always be set, e.g.: First node is just the string of the filename
    private String nodeText = "DEFAULT NODE TEXT";
    private MTree.NodeType nodeType = MTree.NodeType.JAVA_NULL_NODE;
    private EMetaNodeType eMetaNodeType = EMetaNodeType.INVALID;
    private Meta meta = null;

    // custom NodeFS properties for metaClass
    private boolean isStatic = false;  // same as constant
    private boolean isSealed = false;
    private boolean isAbstract = false;
    private boolean isConstructOnlOad = false;
    private boolean isHandleCompatible = false;
    private boolean isEnumeration = false;
    private boolean isDependent = false;
    private boolean isTransient = false;
    private boolean isImmutable = false;
    private EMetaAccess Access = EMetaAccess.PUBLIC;
    private boolean isHidden = false;
    private EMetaAccess GetAccessPrivate = EMetaAccess.PUBLIC;
    private EMetaAccess SetAccessPrivate = EMetaAccess.PUBLIC;
    private boolean hasDefaults = false;
    private String documentation = "";
    private String detailedDocumentation = "";

    public NodeFS(MTree.Node node) {
        super(node);
        this.node = node;
        this.nodeText = NodeUtils.getTextFormattedForNode(node);
        this.nodeType = node.getType();
        eMetaNodeType = EMetaNodeType.MATLAB;
    }

    public NodeFS(MetaClass c, MTree.Node mtNode) {
        eMetaNodeType = EMetaNodeType.META_CLASS;
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

    public NodeFS(MetaProperty p, MTree.Node mtNode) {
        eMetaNodeType = EMetaNodeType.META_PROPERTY;
        meta = p;
        node = mtNode;

        GetAccessPrivate = p.getGetAccess();
        SetAccessPrivate = p.getSetAccess();
        isAbstract = p.isAbstract();
        isHidden = p.isHidden();
        isDependent = p.isDependent();
        isStatic = p.isConstant();
        isTransient = p.isTransient();
        hasDefaults = p.isHasDefaults();

        switch (mtNode.getType()) {
            case PROPTYPEDECL: {
                nodeText = NodeUtils.stringForPrptyDeclNameWithTypeDef(mtNode);
                // nodeText = p.getName();
                break;
            }
            case FUNCTION: {
                nodeText = mtNode.getFunctionName().getText();
                break;
            }
            default: {
                throw new IllegalArgumentException("mtNode has to be either PROPTYPEDECL or FUNCTION");
            }
        }
        documentation = p.getDescription();
        detailedDocumentation = p.getDetailedDescription();
    }

    public NodeFS(MetaMethod m, MTree.Node mtNode) {
        eMetaNodeType = EMetaNodeType.META_METHOD;
        meta = m;
        node = mtNode;

        Access = m.getAccess();
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

    public NodeFS(String nodeText) {
        eMetaNodeType = EMetaNodeType.STRING;
        this.nodeText = nodeText;
    }

    public MTree.Node node() {
        return node;
    }

    public boolean hasNode() {
        return node != null;
    }

    public EMetaNodeType getEMetaNodeType() {
        return eMetaNodeType;
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
        if (eMetaNodeType == EMetaNodeType.MATLAB && nodeType == MTree.NodeType.ID) {
            return true;
        }
        return eMetaNodeType == EMetaNodeType.META_PROPERTY;
    }

    public boolean isStatic() {
        // (eMetaNodeType == eMetaNodeType.META_PROPERTY || eMetaNodeType == eMetaNodeType.META_METHOD) &&
        // isStatic is only set in Meta<Class,Property,Method> constructors, otherwise it's always false
        // so it is safe to just return isStatic
        return isStatic;
    }

    public EMetaAccess getAccess() {
        return Access;
    }

    public Meta getMeta() {
        return meta;
    }

    public boolean isHidden() {
        return isHidden;
    }

    private String nodeDocumentation() {
        if (!(getType() == MTree.NodeType.FUNCTION || getType() == MTree.NodeType.CLASSDEF
                || getEMetaNodeType() == EMetaNodeType.META_CLASS || getEMetaNodeType() == EMetaNodeType.META_METHOD)) {
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
