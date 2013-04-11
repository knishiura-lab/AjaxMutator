package jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;
import org.junit.Test;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAsFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventDetectorTest {
    @Test
    public void testAddEventListnerDetector() {
        EventAttacherDetector detector = new AddEventListenerDetector();

        EventAttachment result
            = detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);"));
        assertTrue(result != null);
        assertEquals("target", ((Name) result.getTarget()).getIdentifier());

        result = detector.detect(parseAsFunctionCall(
                "target.addEventListenr('click', func);"));
        assertTrue(result == null);
    }

    @Test
    public void testAttachEventDetector() {
        EventAttacherDetector detector = new AttachEventDetector();

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.attachEvent('onclick', func);")) != null);

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);")) == null);
    }

    @Test
    public void testAddEventDetector() {
        EventAttacherDetector detector = new AddEventDetector();

        assertTrue(detector.detect(parseAsFunctionCall(
                "addEvent(target, 'click', func);")) != null);

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);")) == null);
    }

    @Test
    public void testTimerEventDetector() {
        TimerEventDetector detector = new TimerEventDetector();
        assertTrue(detector.detect(
                parseAsFunctionCall("setInterval(func, 1000)")) != null);
        assertTrue(detector.detect(
                parseAsFunctionCall("window.setTimeout(func, callAfter)")) != null);
        assertTrue(detector.detect(
                parseAsFunctionCall("window.setInterval(func, funcName)")) != null);

        TimerEventAttachment attachment = detector.detect(
                parseAsFunctionCall("setTimeout(func, 1000)"));
        NumberLiteral duration = (NumberLiteral) attachment.getDuration();
        assertEquals(1000.0, duration.getNumber(), 0.0000000000001);

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);")) == null);
    }
}
