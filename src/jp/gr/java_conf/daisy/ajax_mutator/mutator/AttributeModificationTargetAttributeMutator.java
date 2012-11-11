package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

public class AttributeModificationTargetAttributeMutator 
		extends AbstractReplacingAmongMutator<AttributeModification> {
	public AttributeModificationTargetAttributeMutator(
			PrintStream printStream,
			Collection<AttributeModification> mutationTargets) {
		super(printStream, mutationTargets);
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getTargetAttribute();
	}

	@Override
	protected void replaceFocusedNodeOf(
			AttributeModification parent, AstNode newTargetAttribute) {
		parent.replaceAttribute(newTargetAttribute);
	}
}
