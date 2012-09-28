package jp.gr.java_conf.daisy.ajax_mutator;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * TestExecutor for testclasses written in Junit4.
 *
 * @author Kazuki Nishiura
 */
public class JUnitExecutor implements TestExecutor {
	Class<?>[] targetClasses;
	
	public JUnitExecutor(Class<?>... targetClasses) {
		this.targetClasses = targetClasses;
	}
	
	@Override
	public String execute()	{
		JUnitCore core = new JUnitCore();
		Result result = core.run(targetClasses);
		if (result.wasSuccessful()) {
			return "Success! " + result.getRunCount() + " tests ran.";
		} else {
			return result.getFailureCount() + " tests fail within " + result.getRunCount();
		}
	}
}
