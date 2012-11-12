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
	String executionMessage;

	public JUnitExecutor(Class<?>... targetClasses) {
		this.targetClasses = targetClasses;
	}

	@Override
	public boolean execute()	{
		JUnitCore core = new JUnitCore();
		Result result = core.run(targetClasses);
		if (result.wasSuccessful()) {
			executionMessage = "Success! " + result.getRunCount() + " tests ran.";
			return true;
		} else {
			executionMessage = result.getFailureCount()
					+ " tests fail within " + result.getRunCount();
			return false;
		}
	}

	@Override
	public String getMessageOnLastExecution() {
		return executionMessage;
	}
}
