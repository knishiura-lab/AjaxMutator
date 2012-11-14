package test.jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import static jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request.ResponseType;

import org.junit.Test;

public class JQueryDetectorTest {
	@Test
	public void testJQueryEventDetector() {
		EventAttacherDetector detector = new JQueryEventAttachmentDetector();

		assertTrue(detector.detect(stringToFunctionCall(
				"$('#elm').on('click', func);")) != null);

		assertTrue(detector.detect(
				stringToFunctionCall("target.click(func);")) != null);
	}

	@Test
	public void jQueryDomSelectionDetectorTest() {
		JQueryDOMSelectionDetector detector = new JQueryDOMSelectionDetector();
		DOMSelection result = detector.detect(stringToFunctionCall("$('.aaa')"));
		assertTrue(result != null);
		assertEquals(DOMSelection.SelectionMethod.JQUERY,
				result.getSelectionMethod());
		assertEquals("'.aaa'", result.getSelector().toSource());
	}

	@Test
	public void testJQueryRequestDetector() {
		JQueryRequestDetector detector = new JQueryRequestDetector();
		String data = "{index: 1, value: 'fuga'}";
		Request result = detector.detect(stringToFunctionCall(
				"$.get('hogehoge.php', " + data
						+ ", function(data){var d = data.member; func(d);});"));
		assertTrue(result != null);
		assertEquals("'hogehoge.php'", result.getUrl().toSource());
		assertEquals(data, result.getParameters().toSource());
		assertTrue(result.getSuccessHanlder() != null);
		assertTrue(result.getFailureHandler() == null);

		data = "{type: 'POST', dataType: 'html', success: func1, error: func2}";
		result = detector.detect(stringToFunctionCall("$.ajax('fugafuga.php', "
				+ data + ");"));
		assertTrue(result != null);
		assertEquals("'fugafuga.php'", result.getUrl().toSource());
		assertEquals(ResponseType.HTML, result.getResponseType());
		assertEquals("func1", result.getSuccessHanlder().toSource());
		assertEquals("func2", result.getFailureHandler().toSource());
	}

	@Test
	public void testJQueryAttributeModificationDetector() {
		JQueryAttributeModificationDetector detector = new JQueryAttributeModificationDetector();
		AttributeModification result = detector.detect(stringToFunctionCall(
				"$(this).attr('disabled', true)"));
		assertTrue(result != null);
		assertEquals("$(this)", result.getTargetDom().toSource());
		assertEquals("'disabled'", result.getTargetAttribute().toSource());
		assertEquals("true", result.getAttributeValue().toSource());
	}
}
