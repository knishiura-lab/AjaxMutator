package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.JSType;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection.SelectionMethod;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;

import org.mozilla.javascript.ast.AstNode;

/**
 * Mutator for {@code DOMSelection}, which mutate domSelection result to its
 * parent or child.
 *
 * @author Kazuki Nishiura
 */
public class DOMSelectionMutator extends AbstractMutator<DOMSelection> {
	public DOMSelectionMutator(Collection<DOMSelection> mutationTargets) {
		this(mutationTargets, DEFAULT_STREAM);
	}

	public DOMSelectionMutator(Collection<DOMSelection> mutationTargets,
			PrintStream printStream) {
		super(mutationTargets, printStream);
	}

	@Override
	protected AstNode selectReplacingCandidate(DOMSelection mutationTarget) {
		return mutationTarget.getAstNode();
	}

	@Override
	protected void replaceFocusedNodeOf(DOMSelection parent,
			AstNode replacingNode) {
		double randomValue = Randomizer.getDouble();
		AstNode node = parent.getAstNode();
		JSType domType
			= (parent.getSelectionMethod() == SelectionMethod.JQUERY)
				? JSType.JQUERY_OBJECT
				: JSType.DOM_ELEMENT;

		if (randomValue < 0.5) {
			parent.replace(node, StringToAst.createParentNode(node, domType));
		} else {
			parent.replace(node, StringToAst.createChildNode(node, domType));
		}
	}
}
