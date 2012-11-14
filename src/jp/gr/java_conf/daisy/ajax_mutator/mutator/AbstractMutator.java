package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.Util;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Abstract implementation of {@code Mutator} which replacing part of Mutatable
 * by something.
 *
 * @author Kazuki Nishiura
 */
public abstract class AbstractMutator<T extends Mutatable> implements Mutator {
	protected PrintStream stream;
	protected List<T> mutationTargets;
	protected int targetIndex = 0;
	protected static PrintStream DEFAULT_STREAM = System.out;

	protected AbstractMutator(Collection<T> mutationTargets) {
		this(mutationTargets, DEFAULT_STREAM);
	}

	protected AbstractMutator(Collection<T> mutationTargets,
			PrintStream printStream) {
		this.stream = printStream;
		this.mutationTargets = new ArrayList<T>(mutationTargets);
	}

	/**
	 * @param parent
	 *            Mutatable part of which to be mutated
	 * @param replacingNode
	 *            node that replace part of {@code parent}
	 */
	abstract protected void replaceFocusedNodeOf(T parent,
			AstNode replacingNodwe);

	/**
	 * @return node that can replace a part of mutation target. When appropriate
	 *         node do not exist or cannot be found, returns null.
	 */
	abstract protected AstNode selectReplacingCandidate(T mutationTarget);

	@Override
	public String applyMutation() {
		T mutationTarget = mutationTargets.get(targetIndex);
		AstNode replacingNode = selectReplacingCandidate(mutationTarget);
		if (replacingNode == null) {
			System.out.println("mutation is not applied to: ");
			System.out.println(mutationTarget.toString());
			return null;
		}
		String mutationInformation = mutationInformation(mutationTarget,
				replacingNode);
		if (stream != null)
			stream.println(mutationInformation);
		replaceFocusedNodeOf(mutationTarget, replacingNode);
		return mutationInformation;
	}

	@Override
	public void undoMutation() {
		Mutatable mutationTarget = mutationTargets.get(targetIndex);
		mutationTarget.undoLastReplace();
		targetIndex++;
	}

	@Override
	public boolean isFinished() {
		return mutationTargets.size() <= targetIndex;
	}

	@Override
	public int numberOfMutation() {
		return mutationTargets.size();
	}

	/**
	 * output mutation information to PrintStream pointed by {@code stream}.
	 */
	protected String mutationInformation(T target, AstNode replacingNode) {
		StringBuilder builder = new StringBuilder();
		builder.append("mutate '");
		builder.append(target);
		builder.append("\" (at line ");
		builder.append(target.getAstNode().getLineno());
		builder.append(") by using '");
		builder.append(Util.oneLineStringOf(replacingNode));
		builder.append("'");
		return builder.toString();
	}

	/**
	 * Determine the equality of given to AstNodes in the context of mutator.
	 * Subclass may want to override this method to create only meaningful
	 * mutants.
	 */
	protected boolean ifEquals(AstNode node1, AstNode node2) {
		return node1.toSource().equals(node2.toSource());
	}
}
