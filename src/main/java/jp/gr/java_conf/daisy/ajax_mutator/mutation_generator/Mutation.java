package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.mozilla.javascript.ast.AstNode;

/**
 * Data class that poses mutation information, namely, original node that is
 * mutated and content that replaces the original node.
 *
 * @author Kazuki Nishiura
 */
public class Mutation {
    private final AstNode originalNode;
    private final String mutatingContent;

    public Mutation(AstNode originalNode, AstNode mutatingContent) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent.toSource();
    }

    public Mutation(AstNode originalNode, String mutatingContent) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent;
    }

    public AstNode getOriginalNode() {
        return originalNode;
    }

    public String getMutatingContent() {
        return mutatingContent;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Mutation").append(System.lineSeparator())
                .append("from: ").append(Util.oneLineStringOf(originalNode))
                .append(System.lineSeparator())
                .append("to: ").append(Util.omitLineBreak(mutatingContent))
                .append(System.lineSeparator());
        return builder.toString();
    }
}
