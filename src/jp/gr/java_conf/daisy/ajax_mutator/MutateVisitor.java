package jp.gr.java_conf.daisy.ajax_mutator;

import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.NodeVisitor;

import com.google.common.collect.ImmutableSet;

/**
 * Visitor for JavaScript's AST to get information needed to apply mutation operations.
 *
 * @author Kazuki Nishiura
 */
public class MutateVisitor implements NodeVisitor {
	private final ImmutableSet<EventAttacherDetector> eventAttacherDetectors;
	private Set<EventAttachment> eventAttachmentExpressions
		= new HashSet<EventAttachment>();
	
	public MutateVisitor(Set<EventAttacherDetector> eventAttacherDetectors) {
		this.eventAttacherDetectors
			= ImmutableSet.copyOf(eventAttacherDetectors);
	}
	
	@Override
	public boolean visit(AstNode node) {
		if (node instanceof FunctionCall) {
			return visit((FunctionCall) node);
		}
		return true;
	}
	
	public boolean visit(FunctionCall call) {
		for (EventAttacherDetector detector: eventAttacherDetectors) {
			EventAttachment attachment = detector.detect(call);
			if (attachment != null) {
				eventAttachmentExpressions.add(attachment);
				return false;
			}
		}
		return true;
	}
	
	public Set<EventAttachment> getEventAttachments() {
		return eventAttachmentExpressions;
	}
}