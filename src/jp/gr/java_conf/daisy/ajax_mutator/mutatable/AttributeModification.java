package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom attribute modification such as element.id = 'anotherId'
 *
 * @author Kazuki Nishiura
 */
public class AttributeModification extends Mutatable {
	private final AstNode targetDom;
	private final AstNode targetAttribute;
	private final AstNode attributeValue;
	public AttributeModification(AstNode node, AstNode targetDom,
			AstNode targetAttribute, AstNode attributeValue) {
		super(node);
		this.targetDom = targetDom;
		this.targetAttribute = targetAttribute;
		this.attributeValue = attributeValue;
	}
	
	public AstNode getTargetDom() {
		return targetDom;
	}
	
	public AstNode getTargetAttribute() {
		return targetAttribute;
	}
	
	public AstNode getAttributeValue() {
		return attributeValue;
	}
}
