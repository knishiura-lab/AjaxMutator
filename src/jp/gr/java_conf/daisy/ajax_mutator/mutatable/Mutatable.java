package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Mutatable object, which means mutation operator can be applied to astnode held by this instance.
 * 
 * @author Kazuki Nishiura
 */
public abstract class Mutatable {
	protected final AstNode astNode;
	public Mutatable(AstNode astNode) {
		this.astNode = astNode;
	}
	public AstNode getAstNode() {
		return astNode;
	}
}
