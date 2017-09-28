package at.mep.editor;

import at.mep.util.StringUtils;
import com.mathworks.widgets.text.mcode.MTree;
import com.mathworks.widgets.text.mcode.MTree.Node;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MTreeNode {
    // mTree = at.mep.editor.EditorWrapper.getMTree();
    // mtn = at.mep.editor.MTreeNode.construct(mTree);
    // mtn.attributeString
    //
    // parent: getParent() - above level
    // attributes: getLeft() - same level for current node
    // children: getRight() - lower Level
    // neighbour: getNext() - same Level
    private MTree mTree;

    private MTree.Node mtNode;
    private MTreeNode parent;
    private List<MTreeNode> attributes = new ArrayList<MTreeNode>(10);
    private List<MTreeNode> children = new ArrayList<MTreeNode>(10);

    private MTreeNode(MTree.Node mtNode) {
        this.mtNode = mtNode;
    }

    private static StringBuilder buildStringAttributes(StringBuilder stringBuilder, MTreeNode node) {
        if (node.hasAttributes()) {
            for (int i = 0; i < node.attributes.size(); i++) {
                buildStringAttributes(stringBuilder, node.attributes.get(i));
            }
        }

        stringBuilder.append(node.getText());
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != ' ')
            stringBuilder.append(" ");

        if (EnumSet.of(MTree.NodeType.LT, MTree.NodeType.AND).contains(node.getType())) {
            stringBuilder.append(stringForMTreeNodeType(node.getType()) + " ");
        }

        if (node.hasChildren()) {
            if (node.children.size() > 1) {
                return stringBuilder;
            }
            buildStringAttributes(stringBuilder, node.children.get(0));
        }
        return stringBuilder;
    }

    private static StringBuilder buildStringNewLevel(StringBuilder stringBuilder, MTreeNode node, int lvl) {
        if (node.getType() == MTree.NodeType.JAVA_NULL_NODE) return stringBuilder;
        stringBuilder.append("\n");
        String b = StringUtils.blanks(lvl * 2);

        stringBuilder.append(lvl + " " + b + " + " + node.getType().toString());

        for (MTreeNode n : node.children) {
            buildStringNewLevel(stringBuilder, n, lvl + 1);
        }
        return stringBuilder;
    }

    public static MTreeNode construct(MTree mTree) {
        if (mTree.size() < 1) return null;
        return MTreeNode.construct(mTree.getNode(0));
    }

    public static MTreeNode construct(MTree.Node mtNode) {
        MTreeNode node = new MTreeNode(mtNode);
        if (mtNode.getType() == MTree.NodeType.JAVA_NULL_NODE) {
            return node;
        }

        node.parent = new MTreeNode(mtNode.getParent());
        MTree.Node attribute = mtNode.getLeft();
        while (attribute.getType() != MTree.NodeType.JAVA_NULL_NODE) {
            node.attributes.add(MTreeNode.construct(attribute));
            attribute = attribute.getNext();
        }

        MTree.Node child = mtNode.getRight();
        while (child.getType() != MTree.NodeType.JAVA_NULL_NODE) {
            node.children.add(MTreeNode.construct(child));
            child = child.getNext();
        }

        return node;
    }

    private static String stringForMTreeNodeType(MTree.NodeType type) {
        switch (type) {
            case ERROR:
                return "error";
            case IF:
                return "if";
            case ELSE:
                return "else";
            case ELSEIF:
                return "elseif";
            case SWITCH:
                return "switch";
            case WHILE:
                return "while";
            case BREAK:
                return "break";
            case RETURN:
                return "return";
            case GLOBAL:
                return "global";
            case PERSISTENT:
                return "persistent";
            case TRY:
                return "try";
            case CATCH:
                return "catch";
            case CONTINUE:
                return "continue";
            case FUNCTION:
                return "function";
            case FOR:
                return "for";
            case PARFOR:
                return "parfor";
            case LEFT_PAREN:
                return "(";
            case RIGHT_PAREN:
                return ")";
            case LEFT_BRACKET:
                return "[";
            case RIGHT_BRACKET:
                return "]";
            case LEFT_CURLY_BRACE:
                return "{";
            case RIGHT_CURLY_BRACE:
                return "}";
            case AT_SIGN:
                return "@";
            case DOT_LEFT_PAREN:
                return ".(";
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            case LEFT_DIVIDE:
                return "\\";
            case EXPONENTIATION:
                return "^";
            case COLON:
                return ":";
            case DOT:
                return ".";
            case DOT_MULTIPLY:
                return ".*";
            case DOT_DIVIDE:
                return "./";
            case DOT_LEFT_DIVIDE:
                return ".\\";
            case DOT_EXPONENTIATION:
                return ".^";
            case AND:
                return "&";
            case OR:
                return "|";
            case ANDAND:
                return "&&";
            case OROR:
                return "||";
            case LT:
                return "<";
            case GT:
                return ">";
            case LE:
                return "<=";
            case GE:
                return ">=";
            case EQ:
                return "=";
            case NE:
                return "~=";
            case CASE:
                return "case";
            case OTHERWISE:
                return "otherwise";
            case DUAL:
                return "dual";
            case TRANS:
                return "'";
            case DOTTRANS:
                return ".'";
            case NOT:
                return "~";
            case ID:
                return "id";
            case INT:
                return "int";
            case DOUBLE:
                return "double";
            case STRING:
                return "string";
            case SEMI:
                return ";";
            case COMMA:
                return ",";
            case EOL:
                return "EOL";
            case BANG:
                return "bang";
            case END:
                return "end";
            case EQUALS:
                return "==";
            case CLASSDEF:
                return "classdef";
            case PROPERTIES:
                return "properties";
            case METHODS:
                return "methods";
            case EVENTS:
                return "events";
            case QUEST:
                return "?";
            case ENUMERATION:
                return "enum";
            case ERR:
                return "err";
            case CELL:
                return "cell";
            case SUBSCR:
                return "subscr";
            case CALL:
                return "call";
            case EXPR:
                return "expr";
            case PRINT_EXPR:
                return "print_expr";
            case ANON:
                return "anon";
            case ANONID:
                return "anonid";
            case DCALL:
                return "dcall";
            case JOIN:
                return "join";
            case LIST:
                return "list";
            case EVENT:
                return "event";
            case FIELD:
                return "field";
            case UMINUS:
                return "uminus";
            case UPLUS:
                return "uplus";
            case ATBASE:
                return "@";
            case CEXPR:
                return "cexpr";
            case ROW:
                return "row";
            case ATTR:
                return "attr";
            case ETC:
                return "...";
            case DISTFOR:
                return "distfor";
            case CELL_TITLE:
                return "%%";
            case COMMENT:
                return "%";
            case BLOCK_COMMENT:
                return "%{";
            case BLOCK_COMMENT_END:
                return "%}";
            case OLDFUN:
                return "oldfun";
            case PARENS:
                return "parens";
            case IFHEAD:
                return "ifhead";
            case PROTO:
                return "proto";
            case ATTRIBUTES:
                return "attributes";
            case SPMD:
                return "spmd";
            case PROPTYPEDECL:
                return "proptypedecl";
            case JAVA_NULL_NODE:
                return "javaNullNode";
            default:
                throw new IllegalArgumentException("type: " + type + "not defined");
        }
    }

    public String toString() {
        return "" + mtNode;
    }

    public void printTree() {
        System.out.println("Displaying Tree:");
        StringBuilder stringBuilder = buildStringNewLevel(new StringBuilder(), this, 0);
        System.out.println(stringBuilder);
    }

    public String attributeString() {
        String string = buildStringAttributes(new StringBuilder(""), this).toString();
        return StringUtils.trimEnd(string);
    }

    private boolean hasAttributes() {
        return attributes.size() > 0;
    }

    private boolean hasChildren() {
        return children.size() > 0;
    }

    public Node getMtNode() {
        return mtNode;
    }

    public MTree.NodeType getType() {
        return getMtNode().getType();
    }

    public String getText() {
        return getMtNode().getText();
    }

    public MTreeNode getParent() {
        return parent;
    }

    public List<MTreeNode> getAttributes() {
        return attributes;
    }

    public List<MTreeNode> getChildren() {
        return children;
    }
}
