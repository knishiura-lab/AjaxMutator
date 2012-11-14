package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

import org.mozilla.javascript.ast.AstNode;

public class AttributeModificationValueMutator extends
		AbstractReplacingAmongMutator<AttributeModification> {
	public AttributeModificationValueMutator(
			Collection<AttributeModification> mutationTargets) {
		this(mutationTargets, DEFAULT_STREAM);
	}

	public AttributeModificationValueMutator(
			Collection<AttributeModification> mutationTargets,
			PrintStream printStream) {
		super(mutationTargets, printStream);
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getAttributeValue();
	}

	@Override
	protected void replaceFocusedNodeOf(AttributeModification parent,
			AstNode newValue) {
		parent.replaceAttributeValue(newValue);
	}
}
