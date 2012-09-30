package test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import static org.junit.Assert.*;

import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

public class EventDetectorTest {
	@Test
	public void testAddEventListnerDetector() {
		EventAttacherDetector detector = new AddEventListenerDetector();

		EventAttachment result = detector.detect(stringToFunctionCall(
				"target.addEventListener('click', func);"));
		assertTrue(result != null);
		assertEquals("target", ((Name) result.getTarget()).getIdentifier());
		
		result =detector.detect(stringToFunctionCall(
				"target.addEventListenr('click', func);")); 
		assertTrue(result == null);
	}
	
	@Test
	public void testAttachEventDetector() {
		EventAttacherDetector detector = new AttachEventDetector();

		assertTrue(detector.detect(stringToFunctionCall(
				"target.attachEvent('onclick', func);")) != null);

		assertTrue(detector.detect(stringToFunctionCall(
				"target.addEventListener('click', func);")) == null);
	}
	
	@Test
	public void testAddEventDetector() {
		EventAttacherDetector detector = new AddEventDetector();

		assertTrue(detector.detect(stringToFunctionCall(
				"addEvent(target, 'click', func);")) != null);

		assertTrue(detector.detect(stringToFunctionCall(
				"target.addEventListener('click', func);")) == null);
	}
	
	private FunctionCall stringToFunctionCall(String javaScriptSnipet) {
		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot ast = parser.parse(javaScriptSnipet, 
				"test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector", 1);
		return (FunctionCall) ((ExpressionStatement) ast.getFirstChild()).getExpression();
	}
}
