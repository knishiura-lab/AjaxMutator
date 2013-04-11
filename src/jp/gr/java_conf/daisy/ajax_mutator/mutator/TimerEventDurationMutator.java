package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class TimerEventDurationMutator extends
        AbstractReplacingAmongMutator<TimerEventAttachment> {
    public TimerEventDurationMutator(
            Collection<TimerEventAttachment> mutationTargets) {
        this(mutationTargets, DEFAULT_STREAM);
    }

    public TimerEventDurationMutator(
            Collection<TimerEventAttachment> mutationTargets,
            PrintStream printStream) {
        super(mutationTargets, printStream);
    }

    @Override
    protected AstNode getFocusedNode(TimerEventAttachment node) {
        return node.getDuration();
    }

    @Override
    protected void replaceFocusedNodeOf(TimerEventAttachment parent,
            AstNode newDuration) {
        parent.replaceDuration(newDuration);
    }
}
