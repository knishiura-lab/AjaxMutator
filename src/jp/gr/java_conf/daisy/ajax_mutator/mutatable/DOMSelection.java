package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * DOM Selecting statement, such as document.getElementById("hoge");
 *
 * @author Kazuki Nishiura
 */
public class DOMSelection extends Mutatable {
	private SelectionMethod method;
	private AstNode range;
	private AstNode selector;
	
	public DOMSelection(AstNode node, AstNode range, SelectionMethod method, AstNode selector) {
		super(node);
		this.range = range;
		this.method = method;
		this.selector = selector;
	}
	
	public enum SelectionMethod {
		ID, NAME, CLASS, TAG_NAME, JQUERY
	}
	
	public SelectionMethod getSelectionMethod() {
		return method;
	}
	
	public AstNode getRange() {
		return range;
	}
	
	public AstNode getSelector() {
		return selector;
	}
}
