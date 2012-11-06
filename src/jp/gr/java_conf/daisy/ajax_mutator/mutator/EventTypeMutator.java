package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class EventTypeMutator extends AbstractMutator<EventAttachment> {
	public EventTypeMutator(
			PrintStream printStream, Collection<EventAttachment> mutationTargets) {
		super(printStream, mutationTargets);
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getEvent();
	}

	@Override
	protected void replaceFocusedNodeOf(EventAttachment parent, AstNode newEvent) {
		parent.replaceEvent(newEvent);
	}
}	
