package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

public class AttributeModificationValueMutator extends AbstractReplacingAmongMutator<AttributeModification>{
	public AttributeModificationValueMutator(PrintStream printStream,
			Collection<AttributeModification> mutationTargets) {
		super(printStream, mutationTargets);
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getAttributeValue();
	}

	@Override
	protected void replaceFocusedNodeOf(
			AttributeModification parent, AstNode newValue) {
		parent.replaceAttributeValue(newValue);
	}

}
