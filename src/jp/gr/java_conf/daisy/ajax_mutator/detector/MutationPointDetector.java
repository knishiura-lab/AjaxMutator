package jp.gr.java_conf.daisy.ajax_mutator.detector;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Interface for mutation point detector, which determine where to mutate or not.
 * 
 * @author Kazuki Nishiura
 */
public interface MutationPointDetector {
	/**
	 * @param node
	 * @return mutatable instance if node is what we want to focus, otherwise return null
	 */
	public Mutatable detect(AstNode node);
}
