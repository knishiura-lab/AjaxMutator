package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventCallbackRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTypeRAMutator;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EventMutatorTest extends MutatorTestBase {
    Collection<EventAttachment> eventAttachments;
    private String[] targets;
    private String[] events;
    private String[] callbacks;

    @Override
    protected void prepare() {
        targets = new String[] { "element", "document.getElementById('hoge')" };
        events = new String[] { "'blur'", "'click'" };
        callbacks = new String[] { "func1", "func2" };
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = addEventListner(targets[i], events[i], callbacks[i]);

        Set<EventAttacherDetector> attacherDetector = ImmutableSet
                .of((EventAttacherDetector) new AddEventListenerDetector());
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setEventAttacherDetectors(attacherDetector);
        visitor = builder.build();
        eventAttachments = visitor.getEventAttachments();
    }

    @Test
    public void testEventTargetRAMutator() {
        Mutator mutator = new EventTargetRAMutator(eventAttachments);

    }

    @Test
    public void testEventTypeMutator() {
        Mutator mutator = new EventTypeRAMutator(eventAttachments);

    }

    @Test
    public void testEventCallbackMutator() {
        Mutator mutator = new EventCallbackRAMutator(eventAttachments);

    }

    private String addEventListner(String target, String event, String callback) {
        return target + ".addEventListener(" + event + ", " + callback + ");";
    }

    private void assertChanged(int targetIndex, int eventIndex,
            int callbackIndex, String actual) {
        assertEquals(
                addEventListner(targets[targetIndex], events[eventIndex],
                        callbacks[callbackIndex]), actual);
    }
}
