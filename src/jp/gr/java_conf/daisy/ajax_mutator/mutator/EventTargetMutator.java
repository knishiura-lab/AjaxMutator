package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

public class EventTargetMutator implements Mutator {
	private List<EventAttachment> eventAttachments;
	private List<AstNode> eventTargets;
	private AstNode originalTarget;
	private int targetIndex = 0;
	
	public EventTargetMutator(Collection<EventAttachment> eventAttachments) {
		this.eventAttachments = new ArrayList<EventAttachment>(eventAttachments);
		eventTargets = new ArrayList<AstNode>(this.eventAttachments.size());
		for (EventAttachment attachment: this.eventAttachments) {
			eventTargets.add(attachment.getTarget());
		}
	}
	
	@Override
	public void applyMutation() {
		EventAttachment mutationTarget = eventAttachments.get(targetIndex);
		AstNode newTarget = eventTargets.get((int) Math.floor(Math.random() * eventTargets.size()));
		originalTarget = mutationTarget.getTarget();
		mutationTarget.replaceTarget(newTarget);
	}

	@Override
	public void undoMutation() {
		EventAttachment mutationTarget = eventAttachments.get(targetIndex);
		mutationTarget.replaceTarget(originalTarget);
		targetIndex++;
	}

	@Override
	public boolean isFinished() {
		return eventAttachments.size() <= targetIndex;
	}
}
