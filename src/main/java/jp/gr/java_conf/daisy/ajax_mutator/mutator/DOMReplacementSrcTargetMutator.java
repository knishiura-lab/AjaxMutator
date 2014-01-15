package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMReplacement;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;

/**
 * {@link jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator} that replace src element and target
 * element of DOM replacement, e.g., elm.replaceChild(src, target)
 */
public class DOMReplacementSrcTargetMutator extends AbstractMutator<DOMReplacement> {
    public DOMReplacementSrcTargetMutator() {
        super(DOMReplacement.class);
    }

    @Override
    public Mutation generateMutation(DOMReplacement originalNode) {
        String replacement = originalNode.getAstNode().toSource();
        String PLACE_HOLDER = "__TARGET_AST_NODE__";
        String targetNodeStr = originalNode.getReplacedNode().toSource();
        String replacingNodeStr = originalNode.getReplacingNode().toSource();
        replacement = replacement.replace(targetNodeStr, PLACE_HOLDER);
        replacement = replacement.replace(replacingNodeStr, targetNodeStr);
        replacement = replacement.replace(PLACE_HOLDER, replacingNodeStr);
        return new Mutation(originalNode.getAstNode(), replacement);
    }
}
