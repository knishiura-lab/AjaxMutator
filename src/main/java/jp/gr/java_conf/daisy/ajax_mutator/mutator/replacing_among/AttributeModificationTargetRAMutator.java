package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class AttributeModificationTargetRAMutator
        extends AbstractReplacingAmongMutator<AttributeModification> {
    public AttributeModificationTargetRAMutator(
            Collection<AttributeModification> mutationTargets) {
        super(AttributeModification.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(AttributeModification node) {
        return node.getTargetAttribute();
    }
}
