package at.mep.gui.fileStructure;

import at.mep.editor.tree.EAttributes;
import at.mep.editor.tree.MFile;
import at.mep.meta.*;
import at.mep.util.NodeUtils;
import com.mathworks.matlab.api.editor.Editor;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by z0032f1t on 02.08.2016.
 * <p>
 * Represents a NodeFS in JTree and also an Matlab MTree.NodeFS class.
 */
public class NodeFS extends DefaultMutableTreeNode {
    /* if a property is added make sure it is also set in the copy constructor */
    
    private File file = null;

    private MTree.Node node; // might not always be set, e.g.: First node is just the string of the filename
    private String nodeText = "DEFAULT NODE TEXT";
    private MTree.NodeType nodeType = MTree.NodeType.JAVA_NULL_NODE;
    private List<EAttributes> attributes = new ArrayList<>(10);
    private List<EAccess> accesses = new ArrayList<>(10);

    private String documentation = "";
    private String detailedDocumentation = "";

    private EAccess access = EAttributes.ACCESS.getDefaultAccess();
    private EAccess getAccess = EAttributes.GETACCESS.getDefaultAccess();
    private EAccess setAccess = EAttributes.SETACCESS.getDefaultAccess();

    private boolean isStatic = EAttributes.STATIC.getDefaultAccess().convertBoolean();  // same as constant
    private boolean isSealed = EAttributes.SEALED.getDefaultAccess().convertBoolean();
    private boolean isAbstract = EAttributes.ABSTRACT.getDefaultAccess().convertBoolean();
    private boolean isConstructOnlOad = EAttributes.CONSTRUCTONLOAD.getDefaultAccess().convertBoolean();
    private boolean isHandleCompatible = EAttributes.HANDLECOMPATIBLE.getDefaultAccess().convertBoolean();
    private boolean isEnumeration = false;
    private boolean isDependent = EAttributes.DEPENDENT.getDefaultAccess().convertBoolean();
    private boolean isTransient = EAttributes.TRANSIENT.getDefaultAccess().convertBoolean();
    private boolean isImmutable = false; // SetAccess = Immutable
    private boolean isHidden = EAttributes.HIDDEN.getDefaultAccess().convertBoolean();

    // custom NodeFS properties for metaClass
    private boolean hasDefaults = false;

    private NodeFS() {

    }

    public NodeFS(NodeFS nodeFS) {
        file = nodeFS.file;
        
        node = nodeFS.node;
        nodeText = nodeFS.nodeText;
        nodeType = nodeFS.nodeType;
        attributes = nodeFS.attributes;
        accesses = nodeFS.accesses;

        documentation = nodeFS.documentation;
        detailedDocumentation = nodeFS.detailedDocumentation;

        access = nodeFS.access;
        getAccess = nodeFS.getAccess;
        setAccess = nodeFS.setAccess;

        isStatic = nodeFS.isStatic;
        isSealed = nodeFS.isSealed;
        isAbstract = nodeFS.isAbstract;
        isConstructOnlOad = nodeFS.isConstructOnlOad;
        isHandleCompatible = nodeFS.isHandleCompatible;
        isEnumeration = nodeFS.isEnumeration;
        isDependent = nodeFS.isDependent;
        isTransient = nodeFS.isTransient;
        isImmutable = nodeFS.isImmutable;
        isHidden = nodeFS.isHidden;

        hasDefaults = nodeFS.hasDefaults;
    }

    public NodeFS(String nodeText) {
        this.nodeText = nodeText;
        this.nodeType = MTree.NodeType.JAVA_NULL_NODE;
    }

    public File getFile() {
        return file;
    }

    public boolean isInherited() {
        return file != null;
    }

    public void setAttributes(List<MFile.Attributes> attributesList) {
        for (MFile.Attributes attributes1 : attributesList) {
            for (MFile.Attributes.Attribute attribute : attributes1.getAttributeList()) {
                attributes.add(attribute.getAttributeAsEAttribute());
                accesses.add(attribute.getAccessAsEAccess());
            }
        }

        for (int i = 0; i < attributes.size(); i++) {
            switch (attributes.get(i)) {
                case INVALID:
                    break;
                case ABORTSET:
                    break;
                case ABSTRACT:
                    isAbstract = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case ACCESS:
                    access = accesses.get(i);
                    if (access == EAccess.INVALID) {
                        access = EAccess.PUBLIC;
                    }
                    break;
                case CONSTANT:
                    isStatic = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case DEPENDENT:
                    isDependent = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case GETACCESS:
                    getAccess = accesses.get(i);
                    if (access == EAccess.INVALID) {
                        access = EAccess.PUBLIC;
                    }
                    break;
                case GETOBSERVABLE:
                    break;
                case HIDDEN:
                    isHidden = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case NONCOPYABLE:
                    break;
                case SETACCESS:
                    setAccess = accesses.get(i);
                    isImmutable = setAccess == EAccess.IMMUTABLE;
                    if (access == EAccess.INVALID) {
                        access = EAccess.PUBLIC;
                    }
                    break;
                case SETOBSERVABLE:
                    break;
                case TRANSIENT:
                    isTransient = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case SEALED:
                    isSealed = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case STATIC:
                    isStatic = EnumSet.of(EAccess.TRUE, EAccess.INVALID).contains(accesses.get(i));
                    break;
                case ALLOWEDSUBCLASSES:
                    break;
                case CONSTRUCTONLOAD:
                    break;
                case HANDLECOMPATIBLE:
                    break;
                case INFERIORCLASSES:
                    break;
            }
        }
    }


    public MTree.Node node() {
        return node;
    }

    public boolean hasNode() {
        return node != null;
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

    public EAccess getAccess() {
        return access;
    }

    public boolean isProperty() {
        return nodeType == MTree.NodeType.EQUALS;
    }

    public boolean isStatic() {
        // (eMetaNodeType == eMetaNodeType.META_PROPERTY || eMetaNodeType == eMetaNodeType.META_METHOD) &&
        // isStatic is only set in Meta<Class,Property,Method> constructors, otherwise it's always false
        // so it is safe to just return isStatic
        return isStatic;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isDependent() {
        return isDependent;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public boolean isImmutable() {
        return isImmutable;
    }

    private String nodeDocumentation() {
        if (!(getType() == MTree.NodeType.FUNCTION || getType() == MTree.NodeType.CLASSDEF)) {
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

    public static NodeFS constructForCellTitle(Editor editor) {
        MFile mFile = MFile.construct(editor);
        NodeFS root = new NodeFS(mFile.getName());
        for (MFile.CellTitle cellTitle : mFile.getCellTitles()) {
            NodeFS nodeFS = new NodeFS();
            nodeFS.node = cellTitle.getNode();
            nodeFS.nodeText = cellTitle.getTitleString();
            nodeFS.nodeType = MTree.NodeType.CELL_TITLE;

            root.add(nodeFS);
        }
        return root;
    }

    public static NodeFS constructForFunctions(Editor editor) {
        MFile mFile = MFile.construct(editor);
        NodeFS root = new NodeFS(mFile.getName());
        for (MFile.ClassDef.Method.Function function : mFile.getFunctions()) {
            NodeFS nodeFS = new NodeFS();
            nodeFS.node = function.getNode();
            nodeFS.nodeText = function.getFunctionString();
            nodeFS.nodeType = MTree.NodeType.FUNCTION;

            root.add(nodeFS);
        }
        return root;
    }

    public static NodeFS constructForClassDef(Editor editor, boolean withInherited) {
        MFile mFile = MFile.construct(editor);
        NodeFS root = new NodeFS(mFile.getName());
        if (mFile.getClassDefs().size() < 1) {
            // e.g. gets called when typing in matlab editor and insert a single "%"
            return root;
        }
        MFile.ClassDef classDef = mFile.getClassDefs().get(0);
        root.node = classDef.getNode();
        root.nodeType = MTree.NodeType.CLASSDEF;

        if (withInherited) {
            List<MFile> mFilesCD = classDef.getSuperclassesMFileAll();

            for (MFile mFileCD : mFilesCD) {
                String suffix = " < " + mFileCD.getName();
                if (suffix.endsWith(".m")){
                    suffix = suffix.substring(0, suffix.length()-2);
                }
                populateProperties(root, mFileCD.getClassDefs().get(0).getProperties(), mFileCD.getFile(), suffix);
                populateMethods(root, mFileCD.getClassDefs().get(0).getMethod(), mFileCD.getFile(), suffix);
            }
        }

        populateProperties(root, classDef.getProperties(), null,"");
        populateMethods(root, classDef.getMethod(), null, "");
        populateUtilityFunctions(root, classDef.getUtilityFunctions(), null, "");

        return root;
    }

    private static void populateUtilityFunctions(NodeFS root, List<MFile.ClassDef.Method.Function> functionList, File file, String suffix) {
        for (MFile.ClassDef.Method.Function function : functionList) {
            NodeFS nodeFS = new NodeFS();
            nodeFS.node = function.getNode();
            nodeFS.nodeText = function.getFunctionString() + suffix;
            nodeFS.nodeType = MTree.NodeType.FUNCTION;
            nodeFS.file = file;
            // TODO: What attributes do utility function have?
            root.add(nodeFS);
        }
    }

    private static void populateMethods(NodeFS root, List<MFile.ClassDef.Method> methodList, File file, String suffix) {
        for (MFile.ClassDef.Method method : methodList) {
            List<MFile.Attributes.Attribute> attributeList = null;
            for (MFile.ClassDef.Method.Function function : method.getFunctionList()) {
                if (function.isGetter() || function.isSetter()) {
                    continue;
                }
                NodeFS nodeFS = new NodeFS();
                nodeFS.node = function.getNode();
                nodeFS.nodeText = function.getFunctionString() + suffix;
                nodeFS.nodeType = MTree.NodeType.FUNCTION;
                nodeFS.file = file;
                if (method.hasAttributes()) {
                    nodeFS.setAttributes(method.getAttributes());
                }
                root.add(nodeFS);
            }
        }
    }

    private static void populateProperties(NodeFS root, List<MFile.ClassDef.Properties> propertiesList, File file, String suffix) {
        for (MFile.ClassDef.Properties properties : propertiesList) {
            for (MFile.ClassDef.Properties.Property property : properties.getPropertyList()) {
                NodeFS nodeFS = new NodeFS();

                nodeFS.node = property.getNode();
                nodeFS.nodeText = property.getPropertyString() + suffix;
                nodeFS.nodeType = MTree.NodeType.EQUALS;
                nodeFS.file = file;

                if (properties.hasAttributes()) {
                    nodeFS.setAttributes(properties.getAttributes());
                }

                if (property.hasGetter()) {
                    NodeFS getter = new NodeFS();
                    getter.node = property.getGetter().getNode();
                    getter.nodeText = property.getGetter().getNode().getText();
                    getter.nodeType = MTree.NodeType.FUNCTION;
                    getter.file = file;

                    nodeFS.add(getter);
                }

                if (property.hasSetter()) {
                    NodeFS setter = new NodeFS();
                    setter.node = property.getSetter().getNode();
                    setter.nodeText = property.getSetter().getNode().getText();
                    setter.nodeType = MTree.NodeType.FUNCTION;
                    setter.file = file;

                    nodeFS.add(setter);
                }

                root.add(nodeFS);
            }
        }
    }
}
