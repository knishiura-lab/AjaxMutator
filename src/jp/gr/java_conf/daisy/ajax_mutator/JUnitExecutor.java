package jp.gr.java_conf.daisy.ajax_mutator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

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
	public boolean execute() {
		JUnitCore core = new JUnitCore();
		Result result = core.run(targetClasses);
		List<String> testMethods = new ArrayList<String>();
		for (Class<?> clazz: targetClasses) {
			for (Method method: clazz.getMethods()) {
				if (method.isAnnotationPresent(Test.class))
					testMethods.add(method.getName());
			}
		}
		Map<String, Boolean> testSucceed = new TreeMap<String, Boolean>();
		for (String methodName: testMethods)
			testSucceed.put(methodName, true);

		StringBuilder messageBuilder = new StringBuilder();
		if (result.wasSuccessful()) {
			messageBuilder.append("Test succeed (failed to kill mutants), ")
				.append(result.getRunCount()).append(" tests ran.\n");
			for (int i = 0; i < testSucceed.size(); i++)
				messageBuilder.append("x ");
			messageBuilder.append("x");
			executionMessage = messageBuilder.toString();
			return true;
		} else {
			messageBuilder.append(result.getFailureCount())
				.append(" tests failed within ").append(result.getRunCount())
				.append('\n');
			for (Failure failure: result.getFailures())
				testSucceed.put(failure.getDescription().getMethodName(), false);
			for (Map.Entry<String, Boolean> entry: testSucceed.entrySet())
				messageBuilder.append(entry.getValue() ? 'x' : 'o').append(' ');
			messageBuilder.append("o");
			executionMessage = messageBuilder.toString();
			return false;
		}
	}

	@Override
	public String getMessageOnLastExecution() {
		return executionMessage;
	}
}
