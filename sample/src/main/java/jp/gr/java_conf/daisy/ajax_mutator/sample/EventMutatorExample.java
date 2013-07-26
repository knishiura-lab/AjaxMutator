package jp.gr.java_conf.daisy.ajax_mutator.sample;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTypeRAMutator;

/**
 * Sample class to apply mutation analysis.
 * This class is to estimate the adequacy of {@link LoginTest} by using two
 * mutation operators, namely, EventTargetMutator and EventTypeMutator.
 */
public class EventMutatorExample {
    public static void main(String[] args) {
        MutationTestConductor conductor = new MutationTestConductor();

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(new AddEventDetector()));
        MutateVisitor visitor = builder.build();

        conductor.setup("Path_to_AjaxLogin/login_presentation.js", "test_target_URI", visitor);

        Set<EventAttachment> eventAttachments = visitor.getEventAttachments();
        Set<Mutator> mutators = ImmutableSet.<Mutator>of(
                new EventTargetRAMutator(eventAttachments),
                new EventTypeRAMutator(eventAttachments));

        conductor.conductWithJunit4(mutators, LoginTest.class);
    }
}
