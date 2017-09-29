package at.mep.util;

import at.mep.editor.tree.EAttributePropertyMethod;
import at.mep.meta.EMetaAccess;
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

    public static List<AttributeHolder> convertAttributes(List<MTree.Node> attributes) {
        List<AttributeHolder> list = new ArrayList<>(10);

        for (MTree.Node mtNodeAttr : attributes) {
            List<MTree.Node> attrs = mtNodeAttr.getSubtree();
            EAttributePropertyMethod eAttributes;

            switch (attrs.size()) {
                case 2:
                    // single definition e.g. (Transient):
                    // properties (Transient)
                    // properties (Transient, Access = private)
                    eAttributes = EAttributePropertyMethod.valueOf(attrs.get(1).getText().toUpperCase());
                    list.add(new AttributeHolder(eAttributes, eAttributes.getDefaultAccess()));
                    break;
                case 3:
                    // definition e.g.:
                    // properties (Transient = true)
                    // properties (Transient = true, Access = private)
                    eAttributes = EAttributePropertyMethod.valueOf(attrs.get(1).getText().toUpperCase());
                    EMetaAccess access = EMetaAccess.INVALID;
                    if (attrs.get(2).getType() != INT){
                        access = EMetaAccess.valueOf(attrs.get(2).getText().toUpperCase());
                    }
                    list.add(new AttributeHolder(eAttributes, access));
                    break;
                default:
                    throw new IllegalStateException(
                            "unknown state for Attributes to have neither 2 or 3 fields, Editor.Line: "
                                    + mtNodeAttr.getStartLine());
            }
        }
        return list;
    }

    public static List<MTree.Node> searchForProperties(MTree.Node tree) {
        List<MTree.Node> properties = TreeUtilsV2.findNode(tree.getSubtree(), EQUALS);
        return properties;
    }

    public static List<PropertyHolder> convertProperties(List<MTree.Node> properties) {
        return null;
    }

    public static String stringForMTreeNodeType(MTree.NodeType type) {
        return EMTreeNodeTypeString.valueOf(type.name()).getDisplayString();
    }


    public static class AttributeHolder {
        private EAttributePropertyMethod attribute;
        private EMetaAccess access;

        public AttributeHolder(EAttributePropertyMethod attribute, EMetaAccess access) {
            this.attribute = attribute;
            this.access = access;
        }

        public EAttributePropertyMethod getAttribute() {
            return attribute;
        }

        public EMetaAccess getAccess() {
            if (access == null)
                return attribute.getDefaultAccess();

            return access;
        }
    }

    public static class PropertyHolder {
        private String name = ": NAME NOT SET";
        private String type = ": TYPE NOT DEFINED";
        private String validator = ": VALIDATORS NOT DEFINED";

        public PropertyHolder(String name, String type, String validator) {
            this.name = name;
            this.type = type;
            this.validator = validator;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getValidator() {
            return validator;
        }
    }

    private enum EMTreeNodeTypeString {
        ERROR("error"),
        IF("if"),
        ELSE("else"),
        ELSEIF("elseif"),
        SWITCH("switch"),
        WHILE("while"),
        BREAK("break"),
        RETURN("return"),
        GLOBAL("global"),
        PERSISTENT("persistent"),
        TRY("try"),
        CATCH("catch"),
        CONTINUE("continue"),
        FUNCTION("function"),
        FOR("for"),
        PARFOR("parfor"),
        LEFT_PAREN("("),
        RIGHT_PAREN(")"),
        LEFT_BRACKET("["),
        RIGHT_BRACKET("]"),
        LEFT_CURLY_BRACE("{"),
        RIGHT_CURLY_BRACE("}"),
        AT_SIGN("@"),
        DOT_LEFT_PAREN(".("),
        PLUS("+"),
        MINUS("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        LEFT_DIVIDE("\\"),
        EXPONENTIATION("^"),
        COLON(":"),
        DOT("."),
        DOT_MULTIPLY(".*"),
        DOT_DIVIDE("./"),
        DOT_LEFT_DIVIDE(".\\"),
        DOT_EXPONENTIATION(".^"),
        AND("&"),
        OR("|"),
        ANDAND("&&"),
        OROR("||"),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">="),
        EQ("="),
        NE("~="),
        CASE("case"),
        OTHERWISE("otherwise"),
        DUAL("dual"),
        TRANS("'"),
        DOTTRANS(".'"),
        NOT("~"),
        ID("id"),
        INT("int"),
        DOUBLE("double"),
        STRING("string"),
        SEMI(";"),
        COMMA(","),
        EOL("EOL"),
        BANG("bang"),
        END("end"),
        EQUALS("=="),
        CLASSDEF("classdef"),
        PROPERTIES("properties"),
        METHODS("methods"),
        EVENTS("events"),
        QUEST("?"),
        ENUMERATION("enum"),
        ERR("error"),
        CELL("cell"),
        SUBSCR("subscr"),
        CALL("call"),
        EXPR("expr"),
        PRINT_EXPR("print_expr"),
        ANON("anon"),
        ANONID("anonid"),
        DCALL("dcall"),
        JOIN("join"),
        LIST("list"),
        EVENT("event"),
        FIELD("field"),
        UMINUS("uminus"),
        UPLUS("uplus"),
        ATBASE("@"),
        CEXPR("cexpr"),
        ROW("row"),
        ATTR("attr"),
        ETC("ETC"),
        DISTFOR("distfor"),
        CELL_TITLE("%%"),
        COMMENT("%"),
        BLOCK_COMMENT("%{"),
        BLOCK_COMMENT_END("%}"),
        OLDFUN("oldfun"),
        PARENS("parens"),
        IFHEAD("ifhead"),
        PROTO("proto"),
        ATTRIBUTES("attributes"),
        SPMD("spmd"),
        PROPTYPEDECL("proptypedecl"),
        JAVA_NULL_NODE("javaNullNode");

        private final String displayString;

        EMTreeNodeTypeString(String displayString) {
            this.displayString = displayString;
        }

        public String getDisplayString() {
            return displayString;
        }
    }
}
