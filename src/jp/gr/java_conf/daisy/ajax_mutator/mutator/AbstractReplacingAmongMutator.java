package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Abstract implementation of {@code Mutator}. This implementation provide
 * framework to mutate Mutatbles by replacing subnode of Mutatables among them.
 *
 * @author Kazuki Nishiura
 */
public abstract class AbstractReplacingAmongMutator<T extends Mutatable>
		extends AbstractMutator<T> {
	List<AstNode> mutatedElements;
	protected AbstractReplacingAmongMutator(
			PrintStream printStream, Collection<T> mutationTargets) {
		super(printStream, mutationTargets);
		mutatedElements = new ArrayList<AstNode>(this.mutationTargets.size());
		for (T attachment: this.mutationTargets) {
			mutatedElements.add(getFocusedNode(attachment));
		}
	}

	/**
	 * @return focus of Mutator object. If the class try to mutate event kind
	 * from event attachment statement like
	 * 'element.addEventListener('click', func);', this method must return
	 * something like 'click'.
	 */
	abstract protected AstNode getFocusedNode(T node);

	/**
	 * @return node that can replace mutation target. When appropriate node
	 * do not exist or cannot be found, returns null.
	 */
	@Override
	protected AstNode selectReplacingCandidate(T mutationTarget) {
		Set<AstNode> equivalents = new HashSet<AstNode>();
		equivalents.add(getFocusedNode(mutationTarget));
		while (equivalents.size() < mutatedElements.size()) {
			AstNode candidate = mutatedElements.get(
					Randomizer.getInt(mutatedElements.size()));
			if (ifEquals(getFocusedNode(mutationTarget), candidate))
				equivalents.add(candidate);
			else
				return candidate;
		}
		return null;
	}

	@Override
	protected void printMutationInformation(T target, AstNode replacingNode) {
		AstNode replaced = getFocusedNode(target);
		if (stream != null) {
			StringBuilder builder = new StringBuilder();
			AstNode parent = replaced.getParent();
			builder.append("mutate '");
			builder.append(replaced.toSource());
			builder.append("' in \"");
			builder.append(parent.toSource());
			builder.append("\" (at line ");
			builder.append(parent.getLineno());
			builder.append(") -> '");
			builder.append(replacingNode.toSource());
			builder.append("'");
			stream.println(builder);
		}
	}
}
