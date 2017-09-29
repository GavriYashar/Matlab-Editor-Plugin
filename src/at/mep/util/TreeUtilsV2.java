package at.mep.util;

import com.mathworks.util.tree.Tree;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.ArrayList;
import java.util.List;

import static com.mathworks.widgets.text.mcode.MTree.NodeType.*;

/** Created by Andreas Justin on 2017-09-29. */
public class TreeUtilsV2 {
    public static List<MTree.Node> findNode(List<MTree.Node> nodes, MTree.NodeType nodeType) {
        List<MTree.Node> list = new ArrayList<>(0);
        for (MTree.Node node : nodes) {
            if (node.getType() == nodeType) {
                list.add(node);
            }
        }
        return list;
    }

    public static List<MTree.Node> treeToArrayList(Tree<MTree.Node> tree) {
        int iMax = tree.getChildCount(tree.getRoot());
        List<MTree.Node> list = new ArrayList<>(iMax);
        for (int i = 0; i < iMax; i++) {
            list.add(tree.getChild(tree.getRoot(), i));
        }
        return list;
    }

    public static boolean hasChildren(Tree<MTree.Node> tree) {
        return tree.getChildCount(tree.getRoot()) > 0;
    }

    public static List<MTree.Node> searchForAttributes(MTree.Node tree) {
        List<MTree.Node> attributes = TreeUtilsV2.findNode(tree.getSubtree(), ATTRIBUTES);
        List<MTree.Node> attrs = new ArrayList<>(10);
        for (MTree.Node n : attributes) {
            List<MTree.Node> attributeBlock = n.getSubtree();
            for (MTree.Node mtNode : attributeBlock) {
                if (mtNode.getType() == ATTR) {
                    attrs.add(mtNode);
                }
            }
        }

        return attrs;
    }

    public static List<MTree.Node> searchForProperties(MTree.Node tree) {
        List<MTree.Node> properties = TreeUtilsV2.findNode(tree.getSubtree(), EQUALS);
        return properties;
    }


    public static String stringForMTreeNodeType(MTree.NodeType type) {
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
                return "error";
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
}
