package test.jp.gr.java_conf.daisy.ajax_mutator.detector.request_detector;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToFunctionCall;

import jp.gr.java_conf.daisy.ajax_mutator.detector.request_detector.jQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request.ResponseType;

import org.junit.Test;

public class RequestDetectorTest {
	@Test
	public void testJQueryRequestDetector() {
		jQueryRequestDetector detector = new jQueryRequestDetector();
		String data = "{index: 1, value: 'fuga'}";
		Request result = detector.detect(stringToFunctionCall(
				"$.get('hogehoge.php', " + data + ", function(data){var d = data.member; func(d);});"));
		assertTrue(result != null);
		assertEquals("'hogehoge.php'", result.getUrl().toSource());
		assertEquals(data, result.getParameters().toSource());
		assertTrue(result.getSuccessHanlder() != null);
		assertTrue(result.getFailureHandler() == null);
		

		data = "{type: 'POST', dataType: 'html', success: func1, error: func2}";
		result = detector.detect(stringToFunctionCall(
				"$.ajax('fugafuga.php', " + data + ");"));
		assertTrue(result != null);
		assertEquals("'fugafuga.php'", result.getUrl().toSource());
		assertEquals(ResponseType.HTML, result.getResponseType());
		assertEquals("func1", result.getSuccessHanlder().toSource());
		assertEquals("func2", result.getFailureHandler().toSource());
	}
}