package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

public class DOMCloning extends Mutatable {
    private final AstNode targetNode;

    public DOMCloning(AstNode node, AstNode targetNode) {
        super(node);
        this.targetNode = targetNode;
    }

    public AstNode getTargetNode() {
        return targetNode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append('\n');
        builder.append("  DOM cloning: ");
        builder.append(targetNode.toSource());
        return builder.toString();
    }
}
