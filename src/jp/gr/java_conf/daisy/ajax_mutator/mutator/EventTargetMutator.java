package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

public class EventTargetMutator extends AbstractReplacingAmongMutator<EventAttachment> {
	public EventTargetMutator(
			PrintStream printStream, Collection<EventAttachment> mutationTargets) {
		super(printStream, mutationTargets);
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getTarget();
	}

	@Override
	protected void replaceFocusedNodeOf(EventAttachment parent, AstNode newTarget) {
		parent.replaceTarget(newTarget);
	}
}
