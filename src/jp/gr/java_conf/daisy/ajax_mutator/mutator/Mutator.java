package jp.gr.java_conf.daisy.ajax_mutator.mutator;

/**
 * Interface for mutator, which apply mutation operation to AST.
 * 
 * @author Kazuki Nishiura
 */
public interface Mutator {
	/**
	 * @return summary of applied mutation information, if mutation is applied
	 *         because of any reasons, return null.
	 */
	public String applyMutation();

	public void undoMutation();

	public boolean isFinished();
}
