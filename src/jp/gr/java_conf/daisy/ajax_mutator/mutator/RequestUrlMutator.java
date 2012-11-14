package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;

import org.mozilla.javascript.ast.AstNode;

public class RequestUrlMutator extends AbstractReplacingAmongMutator<Request> {
	public RequestUrlMutator(Collection<Request> mutationTargets) {
		this(mutationTargets, DEFAULT_STREAM);
	}

	public RequestUrlMutator(Collection<Request> mutationTargets,
			PrintStream printStream) {
		super(mutationTargets, printStream);
	}

	@Override
	protected AstNode getFocusedNode(Request node) {
		return node.getUrl();
	}

	@Override
	protected void replaceFocusedNodeOf(Request parent, AstNode newUrl) {
		parent.replaceUrl(newUrl);
	}
}
