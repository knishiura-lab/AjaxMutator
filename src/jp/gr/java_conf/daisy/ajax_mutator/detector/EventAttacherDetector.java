package jp.gr.java_conf.daisy.ajax_mutator.detector;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;

/**
 * Implementation of detector which detect function call for event handler
 * 
 * @author Kazuki Nishiura
 */
public abstract class EventAttacherDetector implements MutationPointDetector {
	/**
	 * detect event attachment from passed function call
	 */
	@Override
	public EventAttachment detect(AstNode node) {
		if (node instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) node;
			return detectFromFunctionCall(call, call.getTarget(), call.getArguments());
		}
		throw new IllegalArgumentException(this.getClass() + ".detect()"
				+ " must receive any function call, but called for " + node.getClass());
	}

	/**
	 * detect event attachment from passed function call (e.g., hoge.attachEvent('onclick', callback) )
	 * 
	 * @param functionCall function call (e.g. hoge.attachEvent('onclick', callback))
	 * @param target target for function call (e.g. hoge.attachEvent)
	 * @param arguments argument for function call (e.g. ['onclick', callback])
	 * @return EventAttachment instance or null
	 */
	abstract public EventAttachment detectFromFunctionCall(
			FunctionCall functionCall, AstNode target, List<AstNode> arguments);
}
