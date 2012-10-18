package test.jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToAssignment;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToFunctionCall;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AppendChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AttributeAssignmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.CreateElementDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.RemoveChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;

import org.junit.Test;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.StringLiteral;

public class DOMManipulationDetectorTest {
	@Test
	public void testAppendChildDetector() {
		AppendChildDetector detector = new AppendChildDetector();

		DOMAppending result = detector.detect(
				stringToFunctionCall("hoge.appendChild(fuga);"));
		assertTrue(result != null);
		assertEquals("hoge", ((Name) result.getAppendTarget()).getIdentifier());
		assertEquals("fuga", ((Name) result.getAppendedDom()).getIdentifier());
	}

	@Test
	public void removeChildDetectorTest() {
		RemoveChildDetector detector = new RemoveChildDetector();

		DOMRemoval result = detector.detect(
				stringToFunctionCall("hoge.removeChild(fuga);"));
		assertTrue(result != null);
		assertEquals("hoge", ((Name) result.getFrom()).getIdentifier());
		assertEquals("fuga", ((Name) result.getTarget()).getIdentifier());
	}
	
	@Test
	public void craeteElementDetectorTest() {
		CreateElementDetector detector = new CreateElementDetector();
		DOMCreation result = detector.detect(
				stringToFunctionCall("document.createElement('div')"));
		assertTrue(result != null);
		assertEquals("div", ((StringLiteral) result.getTagName()).getValue());
		
	}
	
	@Test
	public void attributeAssignmentDetectorTest() {
		AttributeAssignmentDetector detector = new AttributeAssignmentDetector();
		AttributeModification result = detector.detect(
				stringToAssignment("hoge.hidden = true;"));
		assertTrue(result != null);
		assertEquals("true", result.getAttributeValue().toSource());
		assertEquals("hoge", result.getTargetDom().toSource());
		assertEquals("hidden", result.getTargetAttribute().toSource());
		result = detector.detect(stringToAssignment("hoge.hige = true;"));
		assertTrue(result == null);
		result = detector.detect(stringToAssignment("document.findElementById('hoge')['hidden'] = true;"));
		assertTrue(result != null);
	}
}
