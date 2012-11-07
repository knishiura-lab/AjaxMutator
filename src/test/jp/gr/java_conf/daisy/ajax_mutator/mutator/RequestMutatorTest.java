package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToAstRoot;

import java.util.Arrays;
import java.util.HashSet;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestOnSuccessHandlerMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestUrlMutator;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class RequestMutatorTest {
	private String[] urls = {"'hoge.php'", "url"};
	private String[] callbacks = {"func1", "func2"};
	private String[] inputs = new String[2];
	private AstRoot ast;
	private MutateVisitor visitor;
	
	@Before
	public void before() {
		for (int i = 0; i < 2; i++)
			inputs[i] = jQueryGet(urls[i], callbacks[i], i == 0 ? null : "{hoge: 'fuga'}");
		ast = stringToAstRoot(inputs[0] + inputs[1]);
		JQueryRequestDetector[] detectors = {new JQueryRequestDetector()};
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setRequestDetectors(new HashSet<AbstractDetector<Request>>(Arrays.asList(detectors)));
		visitor = builder.build();
		ast.visit(visitor);
	}

	@Test
	public void testRequestUrlMutator() {
		Mutator mutator = new RequestUrlMutator(System.out, visitor.getRequests());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(jQueryGet(urls[1], callbacks[0], null), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(jQueryGet(urls[0], callbacks[1], "{hoge: 'fuga'}"), outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}
	

	@Test
	public void testRequestOnSuccessCallbackMutator() {
		Mutator mutator = new RequestOnSuccessHandlerMutator(System.out, visitor.getRequests());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(jQueryGet(urls[0], callbacks[1], null), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		mutator.undoMutation();
		assertUndo();
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(jQueryGet(urls[1], callbacks[0], "{hoge: 'fuga'}"), outputs[1]);
		mutator.undoMutation();
		assertUndo();
	}

	private String jQueryGet(String url, String callback, String data) {
		return "$.get(" + url + ", " + (data != null ? data + ", " : "") 
				+ callback + ");";
	}

	private void assertUndo() {
		String[] outputs = ast.toSource().split("\n");
		for (int i = 0; i < inputs.length; i++)
			assertEquals(inputs[0], outputs[0]);
	}
}
