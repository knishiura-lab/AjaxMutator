package jp.gr.java_conf.daisy.ajax_mutator.detector;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Interface for mutation point detector, which determine where to mutate or
 * not.
 * 
 * @author Kazuki Nishiura
 */
public interface MutationPointDetector<T extends Mutatable> {
	/**
	 * @param node
	 * @return T instance if node is what we want to focus, otherwise return
	 *         null
	 */
	public T detect(AstNode node);
}
