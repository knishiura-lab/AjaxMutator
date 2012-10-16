package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom removal statment such as parent.removeChild(childNode);
 * 
 * @author Kazuki Nishiura
 */
public class DomRemoval extends Mutatable {
	private final AstNode from;
	private final AstNode target;
	
	public DomRemoval(AstNode node, AstNode from, AstNode target) {
		super(node);
		this.from = from;
		this.target = target;
	}

	public AstNode getFrom() {
		return from;
	}
	
	public AstNode getTarget() {
		return target;
	}
}
