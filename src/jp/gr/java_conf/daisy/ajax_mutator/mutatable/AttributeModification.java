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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\n');
		builder.append("  DOM attribute modification: [dom:");
		builder.append(targetDom.toSource()).append(", attr:");
		builder.append(targetAttribute.toSource()).append(", val:");
		builder.append(attributeValue.toSource()).append("]");
		return builder.toString();
	}
}
