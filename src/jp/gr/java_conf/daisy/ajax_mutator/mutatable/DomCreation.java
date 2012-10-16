package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom creation such as document.createElement
 * 
 * @author Kazuki Nishiura
 */
public class DomCreation extends Mutatable {
	private final AstNode tagName;
	
	public DomCreation(AstNode node, AstNode tagName) {
		super(node);
		this.tagName = tagName;
	}
	
	public AstNode getTagName()	{
		return tagName;
	}
}
