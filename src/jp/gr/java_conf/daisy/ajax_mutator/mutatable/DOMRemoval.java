package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.Util;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom removal statment such as parent.removeChild(childNode);
 * 
 * @author Kazuki Nishiura
 */
public class DOMRemoval extends Mutatable {
	private final AstNode from;
	private final AstNode target;
	
	public DOMRemoval(AstNode node, AstNode from, AstNode target) {
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\n');
		builder.append("  DOM removal: [from:");
		builder.append(Util.oneLineStringOf(from)).append(", removed:");
		builder.append(target).append("]");
		return builder.toString();
	}	
}
