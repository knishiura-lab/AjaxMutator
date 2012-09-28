package jp.gr.java_conf.daisy.ajax_mutator;

/**
 * Interface that indicate implementing classes can execute tests and return 
 * results in String.
 * 
 * @author Kazuki Nishiura
 */
public interface TestExecutor {
	/**
	 * Execute test
	 * 
	 * @return test result
	 */
	public String execute();
}
