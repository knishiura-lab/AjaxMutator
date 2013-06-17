package jp.gr.java_conf.daisy.ajax_mutator.sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTypeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import com.google.common.collect.ImmutableSet;

public class JunitCoreRunTest {
    public static void main(String[] args) {
        MutationTestConductor conductor = new MutationTestConductor();
        EventAttacherDetector[] attahcerDetectorArray
            = {new AddEventDetector()};
        Set<EventAttacherDetector> attacherDetector
            = new HashSet<EventAttacherDetector>(Arrays.asList(attahcerDetectorArray));
        MutateVisitor visitor = new MutateVisitor(attacherDetector, null, null, null, null, null, null, null);

        conductor.setup("Path_to_AjaxLogin/login_presentation.js", "test_target_URI", visitor);

        Set<EventAttachment> eventAttachments = visitor.getEventAttachments();
        Set<Mutator> mutators = ImmutableSet.of(
                (Mutator) new EventTargetMutator(eventAttachments, System.out),
                (Mutator) new EventTypeMutator(eventAttachments, System.out));

        conductor.conductWithJunit4(mutators, LoginTest.class);
    }
}
