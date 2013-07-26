package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AppendChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.MutatorTestBase;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import java.util.Collection;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Kazuki Nishiura
 */
public class AbstractReplacingAmongMutatorTest {
// Note: this class rely on EventCallbackRAMutator to test it's parent class
// AbstractReplacingAmongMutator.

    public Collection<EventAttachment> parseAndGetDomAppendings(String jsProgram) {
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(new AddEventListenerDetector()));
        MutateVisitor visitor = builder.build();
        AstRoot ast = parseAstRoot(jsProgram);
        ast.visit(visitor);
        return visitor.getEventAttachments();
    }

    @Test
    public void testEventCallbackRAMutator() {
        Collection<EventAttachment> eventAttachments = parseAndGetDomAppendings(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback2"));
        Mutator mutator = new EventCallbackRAMutator(eventAttachments);
        assertEquals("callback1", Iterables.get(eventAttachments, 0).getCallback().toSource());
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertEquals("callback1", mutation.getOriginalNode().toSource());
        assertEquals("callback2", mutation.getMutatingContent());
    }

    @Test
    public void testMutationFailsWhenOnlySameNodeAvailable() {
        Collection<EventAttachment> eventAttachments = parseAndGetDomAppendings(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback1"));
        Mutator mutator = new EventCallbackRAMutator(eventAttachments);
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertNull(mutation);
    }

    @Test
    public void testMutationFailsWhenInclusiveRelationshipExists() {
        Collection<EventAttachment> eventAttachments = parseAndGetDomAppendings(
                getAddEventListenerString(
                        "elm", "'click'", "function() {"
                        + getAddEventListenerString("element", "'blur'", "callback2")
                        + "}"));
        assertEquals(2, eventAttachments.size());
        Mutator mutator = new EventCallbackRAMutator(eventAttachments);
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertNull(mutation);
        mutation = mutator.generateMutation(Iterables.get(eventAttachments, 1));
        assertNull(mutation);
    }

    private String getAddEventListenerString(String target, String event, String callback) {
        return target + ".addEventListener(" + event + ", " + callback + ");";
    }
}
