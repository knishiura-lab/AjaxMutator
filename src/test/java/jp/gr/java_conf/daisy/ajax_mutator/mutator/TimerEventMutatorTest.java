package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.TimerEventCallbackRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.TimerEventDurationRAMutator;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TimerEventMutatorTest extends MutatorTestBase {
    private String[] callbacks;
    private String[] durations;
    private Collection<TimerEventAttachment> timerEventAttachments;

    @Override
    public void prepare() {
        callbacks = new String[] { "func1", "func2" };
        durations = new String[] { "300", "duration" };
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = (i == 1 ? "window." : "")
                    + setTimeout(callbacks[i], durations[i], i == 0);

        Set<TimerEventDetector> attacherDetector = ImmutableSet
                .of(new TimerEventDetector());
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setTimerEventDetectors(attacherDetector);
        visitor = builder.build();
        timerEventAttachments = visitor.getTimerEventAttachmentExpressions();
    }

    @Test
    public void testTimerDurationRAMutator() {
        Mutator mutator = new TimerEventDurationRAMutator(timerEventAttachments);
        Mutation mutation;
        mutation = mutator.generateMutation(
                Iterables.get(timerEventAttachments, 0));
        assertEquals(durations[1], mutation.getMutatingContent());
        mutation = mutator.generateMutation(
                Iterables.get(timerEventAttachments, 1));
        assertEquals(durations[0], mutation.getMutatingContent());
    }

    public void testTimerCallbackRAMutator() {
        Mutator mutator = new TimerEventCallbackRAMutator(timerEventAttachments);
        Mutation mutation;
        mutation = mutator.generateMutation(
                Iterables.get(timerEventAttachments, 0));
        assertEquals(callbacks[1], mutation.getMutatingContent());
        mutation = mutator.generateMutation(
                Iterables.get(timerEventAttachments, 1));
        assertEquals(callbacks[0], mutation.getMutatingContent());
    }

    private String setTimeout(String func, String duration, boolean recurcive) {
        return (recurcive ? "setInterval" : "setTimeout") + "(" + func + ", "
                + duration + ");";
    }
}
