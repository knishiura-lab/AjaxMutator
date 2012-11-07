package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestOnSuccessHandlerMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestUrlMutator;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class RequestMutatorTest extends MutatorTestBase {
	private String[] urls;
	private String[] callbacks;
	
	@Override
	public void prepare() {
		urls = new String[] {"'hoge.php'", "url"};
		callbacks = new String[] {"func1", "func2"};
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
		visitor = builder.build();
		inputs = new String[2];
		for (int i = 0; i < 2; i++)
			inputs[i] = jQueryGet(urls[i], callbacks[i], i == 0 ? null : "{hoge: 'fuga'}");
	}
	
	@Test
	public void testRequestUrlMutator() {
		Mutator mutator = new RequestUrlMutator(System.out, visitor.getRequests());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(jQueryGet(urls[1], callbacks[0], null), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(jQueryGet(urls[0], callbacks[1], "{hoge: 'fuga'}"), outputs[1]);
		undoAndAssert(mutator);
	}

	@Test
	public void testRequestOnSuccessCallbackMutator() {
		Mutator mutator = new RequestOnSuccessHandlerMutator(System.out, visitor.getRequests());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(jQueryGet(urls[0], callbacks[1], null), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(jQueryGet(urls[1], callbacks[0], "{hoge: 'fuga'}"), outputs[1]);
		undoAndAssert(mutator);
	}

	private String jQueryGet(String url, String callback, String data) {
		return "$.get(" + url + ", " + (data != null ? data + ", " : "") 
				+ callback + ");";
	}
}
