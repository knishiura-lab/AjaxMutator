package jp.gr.java_conf.daisy.ajax_mutator.mutator;

/**
 * Interface for mutator, which apply mutation operation to AST.
 *
 * @author Kazuki Nishiura
 */
public interface Mutator {
    /**
     * @return summary of applied mutation information, if mutation is NOT
     *         applied for whatever reasons, return null.
     */
    public String applyMutation();

    /**
     * Undo the mutation applied by {@link #applyMutation()}. This method MUST
     * NOT be called immediately after {@link #applyMutation()} returns null.
     */
    public void undoMutation();

    /**
     * skip doing mutation for current mutation target probably because this
     * mutant is already considered and killed in past mutation analysis.
     */
    public void skipMutation();

    /**
     * @return true if this mutator cannot applied mutation any more.
     */
    public boolean isFinished();

    /**
     * @return numberOfMutation, which indicates the maximum number of mutation
     *         that this mutator will apply.
     */
    public int numberOfMutation();

    /**
     * @return identifier of mutation applied by this mutator.
     */
    public String mutationName();
}
