package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToAstRoot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventDurationMutator;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class TimerEventMutatorTest {
	private String[] callbacks = {"func1", "func2"};
	private String[] durations = {"300", "duration"};
	private String[] inputs = new String[2];
	private AstRoot ast;
	private MutateVisitor visitor;
	
	@Before
	public void before() {
		for (int i = 0; i < 2; i++)
			inputs[i] = (i == 1 ? "window." : "") 
				+ setTimeout(callbacks[i], durations[i], i == 0);
		ast = stringToAstRoot(inputs[0] + inputs[1]);
		TimerEventDetector[] detectorArr = {new TimerEventDetector()};
		Set<TimerEventDetector> attacherDetector 
			= new HashSet<TimerEventDetector>(Arrays.asList(detectorArr));
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setTimerEventDetectors(attacherDetector);
		visitor = builder.build();
		ast.visit(visitor);
	}

	@Test
	public void testTimerDurationMutator() {
		Mutator mutator = new TimerEventDurationMutator(
				System.out, visitor.getTimerEventAttachmentExpressions());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(setTimeout(callbacks[0], durations[1], true), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals("window." + setTimeout(callbacks[1], durations[0], false), outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	@Test
	public void testTimerCallbackMutator() {
		Mutator mutator = new TimerEventCallbackMutator(
				System.out, visitor.getTimerEventAttachmentExpressions());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(setTimeout(callbacks[1], durations[0], true), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals("window." + setTimeout(callbacks[0], durations[1], false), outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	private String setTimeout(String func, String duration, boolean recurcive) {
		return (recurcive ? "setInterval" : "setTimeout") + "(" + func + ", "
				+ duration + ");";
	}
	
	private void assertUndo() {
		String[] outputs = ast.toSource().split("\n");
		for (int i = 0; i < inputs.length; i++)
			assertEquals(inputs[0], outputs[0]);
	}
}
