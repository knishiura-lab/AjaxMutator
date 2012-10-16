package test.jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToFunctionCall;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AppendChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.RemoveChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DomAppending;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DomRemoval;

import org.junit.Test;
import org.mozilla.javascript.ast.Name;

public class DOMManipulationDetectorTest {
	@Test
	public void testAppendChildDetector() {
		AppendChildDetector detector = new AppendChildDetector();

		DomAppending result = detector.detect(
				stringToFunctionCall("hoge.appendChild(fuga);"));
		assertTrue(result != null);
		assertEquals("hoge", ((Name) result.getAppendTarget()).getIdentifier());
		assertEquals("fuga", ((Name) result.getAppendedDom()).getIdentifier());
	}

	@Test
	public void testRemoveChildDetector() {
		RemoveChildDetector detector = new RemoveChildDetector();

		DomRemoval result = detector.detect(
				stringToFunctionCall("hoge.removeChild(fuga);"));
		assertTrue(result != null);
		assertEquals("hoge", ((Name) result.getFrom()).getIdentifier());
		assertEquals("fuga", ((Name) result.getTarget()).getIdentifier());
	}
}
