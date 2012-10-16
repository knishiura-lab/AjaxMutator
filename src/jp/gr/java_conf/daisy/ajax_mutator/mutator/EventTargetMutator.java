package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

public class EventTargetMutator extends AbstractMutator {
	private List<EventAttachment> eventAttachments;
	private List<AstNode> eventTargets;
	private int targetIndex = 0;
	
	public EventTargetMutator(
			PrintStream printStream, Collection<EventAttachment> eventAttachments) {
		super(printStream);
		this.eventAttachments = new ArrayList<EventAttachment>(eventAttachments);
		eventTargets = new ArrayList<AstNode>(this.eventAttachments.size());
		for (EventAttachment attachment: this.eventAttachments) {
			eventTargets.add(attachment.getTarget());
		}
	}
	
	@Override
	public boolean applyMutation() {
		EventAttachment mutationTarget = eventAttachments.get(targetIndex);
		Set<AstNode> equivalents = new HashSet<AstNode>();
		equivalents.add(mutationTarget.getTarget());
		AstNode newTarget = null;
		while (equivalents.size() < eventTargets.size()) {
			newTarget = eventTargets.get((int) Math.floor(Math.random() * eventTargets.size()));
			if (ifEquals(mutationTarget.getTarget(), newTarget))
				equivalents.add(newTarget);
			else
				break;
		}
		if (newTarget == null)
			return false;
		printMutationInformation(mutationTarget.getTarget(), newTarget);
		mutationTarget.replaceTarget(newTarget);
		return true;
	}

	@Override
	public void undoMutation() {
		EventAttachment mutationTarget = eventAttachments.get(targetIndex);
		mutationTarget.undoLastReplace();
		targetIndex++;
	}

	@Override
	public boolean isFinished() {
		return eventAttachments.size() <= targetIndex;
	}
}
