package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class EventTargetRAMutator
        extends AbstractReplacingAmongMutator<EventAttachment> {
    public EventTargetRAMutator(Collection<EventAttachment> mutationTargets) {
        super(EventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getTarget();
    }
}
