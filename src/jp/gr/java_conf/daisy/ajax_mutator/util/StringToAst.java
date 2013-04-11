package jp.gr.java_conf.daisy.ajax_mutator.util;

import jp.gr.java_conf.daisy.ajax_mutator.JSType;
import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

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
            @SuppressWarnings("unchecked")
            T ret = (T) ((ExpressionStatement) ast.getFirstChild()).getExpression();
            return ret;
        } catch (ClassCastException e) {
            System.err.println(
                    javaScriptSnippet + " cannot parsed as " + type.getName());
            return null;
        }
    }

    public static FunctionCall parseAsFunctionCall(String javaScriptSnippet) {
        return parseAsType(FunctionCall.class, javaScriptSnippet);
    }

    public static Assignment parseAsAssignment(String javaScriptSnippet) {
        return parseAsType(Assignment.class, javaScriptSnippet);
    }

    public static PropertyGet parseAsPropertyGet(String javaScriptSnippet) {
        return parseAsType(PropertyGet.class, javaScriptSnippet);
    }

    public static AstNode createParentNode(AstNode node, JSType domType) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(node.toSource()).append(")");
        if (domType == JSType.DOM_ELEMENT) {
            builder.append(".parentElement");
            return parseAsPropertyGet(builder.toString());
        } else if (domType == JSType.JQUERY_OBJECT) {
            builder.append(".parent()");
            return parseAsFunctionCall(builder.toString());
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
    }

    public static AstNode createChildNode(AstNode node, JSType domType) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(node.toSource()).append(")");
        if (domType == JSType.DOM_ELEMENT) {
            builder.append(".children[0]");
            return parseAsType(ElementGet.class, builder.toString());
        } else if (domType == JSType.JQUERY_OBJECT) {
            builder.append(".children(':first')");
            return parseAsFunctionCall(builder.toString());
        } else {
            throw new IllegalArgumentException(domType + " is not supported.");
        }
    }
}
