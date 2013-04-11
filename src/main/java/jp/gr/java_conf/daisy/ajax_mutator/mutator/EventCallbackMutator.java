package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class EventCallbackMutator extends
        AbstractReplacingAmongMutator<EventAttachment> {
    public EventCallbackMutator(Collection<EventAttachment> mutationTargets) {
        this(mutationTargets, DEFAULT_STREAM);
    }

    public EventCallbackMutator(Collection<EventAttachment> mutationTargets,
            PrintStream printStream) {
        super(mutationTargets, printStream);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getCallback();
    }

    @Override
    protected void replaceFocusedNodeOf(
            EventAttachment parent, AstNode newCallback) {
        parent.replaceCallback(newCallback);
    }
}
