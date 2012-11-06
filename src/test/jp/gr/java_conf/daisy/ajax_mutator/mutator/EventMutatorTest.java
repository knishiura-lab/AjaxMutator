package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToAstRoot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTypeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

public class EventMutatorTest {
	private String[] targets = {"element", "document.getElementById('hoge')"};
	private String[] events = {"'blur'", "'click'"};
	private String[] callbacks = {"func1", "func2"};
	private String[] inputs = new String[2];
	private AstRoot ast;
	private MutateVisitor visitor;
	
	@Before
	public void before() {
		for (int i = 0; i < 2; i++)
			inputs[i] = addEventListner(targets[i], events[i], callbacks[i]);
		ast = stringToAstRoot(inputs[0] + inputs[1]);
		EventAttacherDetector[] attahcerDetectorArray 
		= {new AddEventListenerDetector()};
		Set<EventAttacherDetector> attacherDetector 
			= new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray));
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setEventAttacherDetectors(attacherDetector);
		visitor = builder.build();
		ast.visit(visitor);
	}
	
	@Test
	public void testEventTargetMutator() {
		Mutator mutator = new EventTargetMutator(System.out, visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(1, 0, 0, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(0, 1, 1, outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	@Test
	public void testEventTypeMutator() {
		Mutator mutator = new EventTypeMutator(System.out, visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(0, 1, 0, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(1, 0, 1, outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	@Test
	public void testEventCallbackMutator() {
		Mutator mutator = new EventCallbackMutator(System.out, visitor.getEventAttachments());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertChanged(0, 0, 1, outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertChanged(1, 1, 0, outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	private String addEventListner(String target, String event, String callback) {
		return target + ".addEventListener(" + event + ", " + callback + ");";
	}
	
	private void assertUndo() {
		String[] outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(inputs[1], outputs[1]);
	}
	
	private void assertChanged(int targetIndex, int eventIndex, 
			int callbackIndex, String actual) {
		assertEquals(addEventListner(targets[targetIndex], events[eventIndex], 
				callbacks[callbackIndex]), actual);
	}
}
