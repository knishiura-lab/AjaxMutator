package jp.gr.java_conf.daisy.ajax_mutator.util;

import jp.gr.java_conf.daisy.ajax_mutator.JSType;
import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;
import org.mozilla.javascript.ast.*;

/**
 * Set of static method to create AstNode
 *
 * @author Kazuki Nishiura
 */
public class StringToAst {
    private StringToAst() {}

    public static AstRoot parseAstRoot(String javaScriptSnippet) {
        ParserWithBrowser parser = ParserWithBrowser.getParser();
        AstRoot root
            = parser.parse(javaScriptSnippet,
                    "jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector",1);
        return root;
    }

    public static <T extends AstNode> T parseAsType(Class<T> type,
            String javaScriptSnippet) {
        AstRoot ast = parseAstRoot(javaScriptSnippet);

        try {
            if (ast.getFirstChild() instanceof ExpressionStatement) {
                return  (T) ((ExpressionStatement) ast.getFirstChild()).getExpression();
            } else {
                return  (T) ast.getFirstChild();
            }
        } catch (ClassCastException e) {
            System.err.println(
                    javaScriptSnippet + " cannot parsed as " + type.getName());
            return null;
        }
    }

    public static FunctionCall parseAsFunctionCall(String javaScriptSnippet) {
        return parseAsType(FunctionCall.class, javaScriptSnippet);
    }

    public static FunctionNode parseAsFunctionNode(String javaScriptSnippet) {
        return parseAsType(FunctionNode.class, javaScriptSnippet);
    }

    public static Assignment parseAsAssignment(String javaScriptSnippet) {
        return parseAsType(Assignment.class, javaScriptSnippet);
    }

    public static PropertyGet parseAsPropertyGet(String javaScriptSnippet) {
        return parseAsType(PropertyGet.class, javaScriptSnippet);
    }

    public static StringLiteral parseAsStringLiteral(String javaScriptSnippet) {
        return parseAsType(StringLiteral.class, javaScriptSnippet);
    }

    public static String createParentNodeAsString(AstNode node, JSType domType) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(node.toSource()).append(")");
        if (domType == JSType.DOM_ELEMENT) {
            builder.append(".parentElement");
        } else if (domType == JSType.JQUERY_OBJECT) {
            builder.append(".parent()");
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
        return builder.toString();
    }

    public static AstNode createParentNode(AstNode node, JSType domType) {
        if (domType == JSType.DOM_ELEMENT) {
            return parseAsPropertyGet(createParentNodeAsString(node, domType));
        } else if (domType == JSType.JQUERY_OBJECT) {
            return parseAsFunctionCall(createParentNodeAsString(node, domType));
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
    }

    public static String createChildNodeAsString(AstNode node, JSType domType) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(node.toSource()).append(")");
        if (domType == JSType.DOM_ELEMENT) {
            builder.append(".children[0]");
            return builder.toString();
        } else if (domType == JSType.JQUERY_OBJECT) {
            builder.append(".children(':first')");
            return builder.toString();
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
    }

    public static AstNode createChildNode(AstNode node, JSType domType) {
        if (domType == JSType.DOM_ELEMENT) {
            return parseAsType(
                    ElementGet.class,
                    createChildNodeAsString(node, domType));
        } else if (domType == JSType.JQUERY_OBJECT) {
            return parseAsFunctionCall(createChildNodeAsString(node, domType));
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
    }
}
