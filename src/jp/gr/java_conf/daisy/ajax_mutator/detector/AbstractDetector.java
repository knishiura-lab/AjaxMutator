package jp.gr.java_conf.daisy.ajax_mutator.detector;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;

/**
 * Abstract detector which provide common functionality required to implement
 * Detector.
 *
 * @author Kazuki Nishiura
 * @param <T>
 */
public abstract class AbstractDetector<T extends Mutatable> implements MutationPointDetector<T>{
	/**
	 * Assuming node is FunctionCall and detect if node is desired element.
	 * 
	 * @return T if node is FunctionCall instance and what we want to focus,
	 * otherwise, return null
	 */
	protected T detectFromFunctionCall(AstNode node) {
		return detectFromFunctionCall(node, false);
	}
	
	/**
	 * Assuming node is FunctionCall and detect if node is desired element.
	 * 
	 * @return T if node is FunctionCall instance and what we want to focus,
	 * otherwise, return null
	 * @throws IllegalStatementException if strict is true and node is not an
	 * instance of FunctionalCall
	 */
	protected T detectFromFunctionCall(AstNode node, boolean strict) {
		if (node instanceof FunctionCall) {
			FunctionCall funcCall = (FunctionCall) node;
			return detectFromFunctionCall(
					funcCall, funcCall.getTarget(), funcCall.getArguments());
		} else if (strict) {
			throw new IllegalArgumentException(this.getClass() + ".detect()"
					+ " must receive any function call, but called for " + node.getClass());
		}
		return null;
	}
	
	/**
	 * detect Mutatable from passed function call (e.g., hoge.attachEvent('onclick', callback) )
	 * 
	 * @param functionCall whole function call (e.g. hoge.attachEvent('onclick', callback))
	 * @param target target for function call (e.g. hoge.attachEvent)
	 * @param arguments argument for function call (e.g. ['onclick', callback])
	 * @return EventAttachment instance or null
	 */
	protected T detectFromFunctionCall(
			FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
		return null;
	}
}
