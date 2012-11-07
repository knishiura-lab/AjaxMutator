package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;

public class RequestOnSuccessHandlerMutator extends AbstractMutator<Request> {
	public RequestOnSuccessHandlerMutator(PrintStream printStream,
			Collection<Request> mutationTargets) {
		super(printStream, mutationTargets);
	}

	@Override
	protected AstNode getFocusedNode(Request node) {
		return node.getSuccessHanlder();
	}

	@Override
	protected void replaceFocusedNodeOf(Request parent, AstNode newCallback) {
		parent.replaceOnSuccessCallback(newCallback);
	}
}
