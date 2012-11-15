package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.util.AstUtil;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

import com.google.common.collect.ImmutableSet;

/**
 * Dom attribute modification such as element.id = 'anotherId'
 *
 * @author Kazuki Nishiura
 */
public class AttributeModification extends Mutatable {
	private final AstNode targetDom;
	private final AstNode targetAttribute;
	private final AstNode attributeValue;
	public static final Set<String> JQUERY_ATTR_SHORTCUTS
		= ImmutableSet.of("height", "width", "text");
	private final boolean usingJQueryShortcut;

	public AttributeModification(AstNode node, AstNode targetDom,
			AstNode targetAttribute, AstNode attributeValue) {
		this(node, targetDom, targetAttribute, attributeValue, false);
	}

	public AttributeModification(AstNode node, AstNode targetDom,
					AstNode targetAttribute, AstNode attributeValue,
			boolean usingJQueryShortcut) {
		super(node);
		this.targetDom = targetDom;
		this.targetAttribute = targetAttribute;
		this.attributeValue = attributeValue;
		this.usingJQueryShortcut = usingJQueryShortcut;
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

	public void replaceAttributeValue(AstNode newValue) {
		replace(attributeValue, newValue);
	}

	public void replaceAttribute(AstNode newAttribute) {
		if (usingJQueryShortcut) {
			if ((newAttribute instanceof Name)
					&& JQUERY_ATTR_SHORTCUTS.contains(
							((Name) newAttribute).getIdentifier())) {
				replace(targetAttribute, newAttribute);
			} else {
				StringBuilder builder = new StringBuilder();
				builder.append(targetDom.toSource())
					.append(".attr(")
					.append(newAttribute.toSource()).append(", ")
					.append(attributeValue.toSource()).append(")");
				AstNode newNode = StringToAst.parseAsFunctionCall(builder.toString());
				replace(AstUtil.parentOfType(
						targetDom.getParent(), FunctionCall.class), newNode);
			}
		} else {
			replace(targetAttribute, newAttribute);
		}
	}

	public void replaceTargetDOM(AstNode newTarget) {
		replace(targetDom, newTarget);
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
