package jp.gr.java_conf.daisy.ajax_mutator;

import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.NodeVisitor;

public class MutateVisitor implements NodeVisitor {
	Set<EventAttacherDetector> eventAttacherDetectors;
	public MutateVisitor(Set<EventAttacherDetector> eventAttacherDetectors) {
		this.eventAttacherDetectors 
			= new HashSet<EventAttacherDetector>(eventAttacherDetectors);
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
				System.out.println(attachment);
				return false;
			}
		}
		return true;
	}
}