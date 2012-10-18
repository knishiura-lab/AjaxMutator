package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom appending statement such as element.appendChild
 * 
 * @author Kazuki Nishiura
 */
public class DOMAppending extends Mutatable {
	private AstNode appendTarget;
	private AstNode appendedDom;
	
	public DOMAppending(AstNode astNode, AstNode appendTarget, AstNode appendedDom) {
		super(astNode);
		this.appendTarget = appendTarget;
		this.appendedDom = appendedDom;
	}
	
	public AstNode getAppendedDom() {
		return appendedDom;
	}
	
	public AstNode getAppendTarget() {
		return appendTarget;
	}
}
