package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class TimerEventDurationRAMutator
        extends AbstractReplacingAmongMutator<TimerEventAttachment> {
    public TimerEventDurationRAMutator(Collection<TimerEventAttachment> mutationTargets) {
        super(TimerEventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(TimerEventAttachment node) {
        return node.getDuration();
    }
}
