package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * element.normalize
 */
public class DOMNormalization extends Mutatable {
    private final AstNode target;

    public DOMNormalization(AstNode node, AstNode target) {
        super(node);
        this.target = target;
    }

    public AstNode getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append('\n');
        builder.append("  DOM normalize: [target:");
        builder.append(target.toSource()).append("]");
        return builder.toString();
    }
}
