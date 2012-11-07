package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

public class TimerEventCallbackMutator extends AbstractMutator<TimerEventAttachment>{
	public TimerEventCallbackMutator(PrintStream printStream,
			Collection<TimerEventAttachment> mutationTargets) {
		super(printStream, mutationTargets);
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
