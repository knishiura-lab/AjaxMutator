package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTypeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class EventMutatorTest extends MutatorTestBase {
	private String[] targets;
	private String[] events;
	private String[] callbacks;

	@Override
	void prepare() {
		targets = new String[] { "element", "document.getElementById('hoge')" };
		events = new String[] { "'blur'", "'click'" };
		callbacks = new String[] { "func1", "func2" };
		inputs = new String[2];
		for (int i = 0; i < 2; i++)
			inputs[i] = addEventListner(targets[i], events[i], callbacks[i]);

		Set<EventAttacherDetector> attacherDetector = ImmutableSet
				.of((EventAttacherDetector) new AddEventListenerDetector());
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setEventAttacherDetectors(attacherDetector);
		visitor = builder.build();
	}

	@Test
	public void testEventTargetMutator() {
		Mutator mutator = new EventTargetMutator(visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(1, 0, 0, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(0, 1, 1, outputs[1]);
		undoAndAssert(mutator);
	}

	@Test
	public void testEventTypeMutator() {
		Mutator mutator = new EventTypeMutator(visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(0, 1, 0, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(1, 0, 1, outputs[1]);
		undoAndAssert(mutator);
	}

	@Test
	public void testEventCallbackMutator() {
		Mutator mutator = new EventCallbackMutator(
				visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(0, 0, 1, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(1, 1, 0, outputs[1]);
		undoAndAssert(mutator);
	}

	private String addEventListner(String target, String event, String callback) {
		return target + ".addEventListener(" + event + ", " + callback + ");";
	}

	private void assertChanged(int targetIndex, int eventIndex,
			int callbackIndex, String actual) {
		assertEquals(
				addEventListner(targets[targetIndex], events[eventIndex],
						callbacks[callbackIndex]), actual);
	}
}
