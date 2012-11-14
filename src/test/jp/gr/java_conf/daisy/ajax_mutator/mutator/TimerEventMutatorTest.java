package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventDurationMutator;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class TimerEventMutatorTest extends MutatorTestBase {
	private String[] callbacks;
	private String[] durations;

	@Override
	public void prepare() {
		callbacks = new String[] { "func1", "func2" };
		durations = new String[] { "300", "duration" };
		inputs = new String[2];
		for (int i = 0; i < 2; i++)
			inputs[i] = (i == 1 ? "window." : "")
					+ setTimeout(callbacks[i], durations[i], i == 0);

		Set<TimerEventDetector> attacherDetector = ImmutableSet
				.of(new TimerEventDetector());
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setTimerEventDetectors(attacherDetector);
		visitor = builder.build();
	}

	@Test
	public void testTimerDurationMutator() {
		Mutator mutator = new TimerEventDurationMutator(
				visitor.getTimerEventAttachmentExpressions());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(setTimeout(callbacks[0], durations[1], true), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals("window." + setTimeout(callbacks[1], durations[0], false),
				outputs[1]);
		undoAndAssert(mutator);
	}

	@Test
	public void testTimerCallbackMutator() {
		Mutator mutator = new TimerEventCallbackMutator(
				visitor.getTimerEventAttachmentExpressions());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(setTimeout(callbacks[1], durations[0], true), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals("window." + setTimeout(callbacks[0], durations[1], false),
				outputs[1]);
		undoAndAssert(mutator);
	}

	private String setTimeout(String func, String duration, boolean recurcive) {
		return (recurcive ? "setInterval" : "setTimeout") + "(" + func + ", "
				+ duration + ");";
	}
}
