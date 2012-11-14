package test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import static jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

import org.junit.Test;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;

public class EventDetectorTest {
	@Test
	public void testAddEventListnerDetector() {
		EventAttacherDetector detector = new AddEventListenerDetector();

		EventAttachment result
			= detector.detect(stringToFunctionCall(
				"target.addEventListener('click', func);"));
		assertTrue(result != null);
		assertEquals("target", ((Name) result.getTarget()).getIdentifier());

		result = detector.detect(stringToFunctionCall(
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

	@Test
	public void testTimerEventDetector() {
		TimerEventDetector detector = new TimerEventDetector();
		assertTrue(detector.detect(
				stringToFunctionCall("setInterval(func, 1000)")) != null);
		assertTrue(detector.detect(
				stringToFunctionCall("window.setTimeout(func, callAfter)")) != null);
		assertTrue(detector.detect(
				stringToFunctionCall("window.setInterval(func, funcName)")) != null);

		TimerEventAttachment attachment = detector.detect(
				stringToFunctionCall("setTimeout(func, 1000)"));
		NumberLiteral duration = (NumberLiteral) attachment.getDuration();
		assertEquals(1000.0, duration.getNumber(), 0.0000000000001);

		assertTrue(detector.detect(stringToFunctionCall(
				"target.addEventListener('click', func);")) == null);
	}
}
