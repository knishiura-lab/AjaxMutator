package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.mozilla.javascript.ast.AstNode;

/**
 * DOM Replacement such as elm.replaceChild(replaced, to)
 */
public class DOMReplacement extends Mutatable {
    private final AstNode replaced;
    private final AstNode replacing;

    public DOMReplacement(AstNode node, AstNode replaced, AstNode replacing) {
        super(node);
        this.replaced = replaced;
        this.replacing = replacing;
    }

    public AstNode getReplacedNode() {
        return replaced;
    }

    public AstNode getReplacingNode() {
        return replacing;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append('\n');
        builder.append("  DOM replacement: [replaced:");
        builder.append(Util.oneLineStringOf(replaced)).append(", replacing:");
        builder.append(replacing).append("]");
        return builder.toString();
    }
}
