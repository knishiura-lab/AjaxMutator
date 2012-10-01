package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class EventTypeMutator extends AbstractMutator {
	private List<EventAttachment> eventAttachments;
	private List<AstNode> eventTypes;
	private int targetIndex = 0;
	
	public EventTypeMutator(
			PrintStream printStream, Collection<EventAttachment> eventAttachments) {
		super(printStream);
		this.eventAttachments = new ArrayList<EventAttachment>(eventAttachments);
		eventTypes = new ArrayList<AstNode>(this.eventAttachments.size());
		for (EventAttachment attachment: this.eventAttachments) {
			eventTypes.add(attachment.getEvent());
		}
	}
	
	@Override
	public boolean applyMutation() {
		EventAttachment mutationTarget = eventAttachments.get(targetIndex);
		Set<AstNode> equivalents = new HashSet<AstNode>();
		equivalents.add(mutationTarget.getEvent());
		AstNode newtype = null;
		while (equivalents.size() < eventTypes.size()) {
			newtype = eventTypes.get((int) Math.floor(Math.random() * eventTypes.size()));
			if (ifEquals(mutationTarget.getEvent(), newtype))
				equivalents.add(newtype);
			else
				break;
		}
		if (newtype == null)
			return false;
		printMutationInformation(mutationTarget.getEvent(), newtype);
		mutationTarget.replaceEvent(newtype);
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
