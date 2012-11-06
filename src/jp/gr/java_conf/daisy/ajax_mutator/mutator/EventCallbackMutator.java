package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

public class EventCallbackMutator extends AbstractMutator<EventAttachment> {
	public EventCallbackMutator(
			PrintStream stream, Collection<EventAttachment> eventAttachments) {
		super(stream, eventAttachments);
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getCallback();
	}

	@Override
	protected void replaceFocusedNodeOf(EventAttachment parent, AstNode newCallback) {
		parent.replaceCallback(newCallback);
	}
}
