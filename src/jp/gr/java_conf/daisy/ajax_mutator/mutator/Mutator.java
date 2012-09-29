package jp.gr.java_conf.daisy.ajax_mutator.mutator;

/**
 * Interface for mutator, which apply mutation operation to AST.
 * 
 * @author Kazuki Nishiura
 */
public interface Mutator {
	/**
	 * @return if mutation operation is successfully applied or not.
	 */
	public boolean applyMutation();
	public void undoMutation();
	public boolean isFinished();
}
