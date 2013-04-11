package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class TimerEventCallbackMutator extends
        AbstractReplacingAmongMutator<TimerEventAttachment> {
    public TimerEventCallbackMutator(
            Collection<TimerEventAttachment> mutationTargets) {
        this(mutationTargets, DEFAULT_STREAM);
    }

    public TimerEventCallbackMutator(
            Collection<TimerEventAttachment> mutationTargets,
            PrintStream printStream) {
        super(mutationTargets, printStream);
    }

    @Override
    protected AstNode getFocusedNode(TimerEventAttachment node) {
        return node.getCallback();
    }

    @Override
    protected void replaceFocusedNodeOf(TimerEventAttachment parent,
            AstNode newCallback) {
        parent.replaceCallback(newCallback);
    }
}
