package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

public class TimerEventDurationMutator extends AbstractMutator<TimerEventAttachment> {
	public TimerEventDurationMutator(PrintStream printStream,
			Collection<TimerEventAttachment> mutationTargets) {
		super(printStream, mutationTargets);
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
