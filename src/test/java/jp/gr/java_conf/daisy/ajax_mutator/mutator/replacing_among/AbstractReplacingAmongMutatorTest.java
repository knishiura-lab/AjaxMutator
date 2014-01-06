package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import java.util.Collection;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class AbstractReplacingAmongMutatorTest {
// Note: this class rely on EventCallbackRAMutator to test it's parent class
// AbstractReplacingAmongMutator.

    public Collection<EventAttachment> parseAndGetEventAttachment(String jsProgram) {
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
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback2"));
        Mutator mutator = new EventCallbackRAMutator(eventAttachments);
        assertEquals("callback1", Iterables.get(eventAttachments, 0).getCallback().toSource());
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertEquals("callback1", mutation.getOriginalNode().toSource());
        assertEquals("callback2", mutation.getMutatingContent());
    }

    @Test
    public void testDefaultMutationShouldBeUsedWhenOnlySameNodeAvailable() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback1"));
        AbstractReplacingAmongMutator mutator = new EventCallbackRAMutator(eventAttachments);
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutation.getMutatingContent());
    }

    @Test
    public void testDefaultMutationShouldBeUsedWhenInclusiveRelationshipExists() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString(
                        "elm", "'click'", "function() {"
                        + getAddEventListenerString("element", "'blur'", "callback2")
                        + "}"));
        assertEquals(2, eventAttachments.size());
        AbstractReplacingAmongMutator mutator = new EventCallbackRAMutator(eventAttachments);
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(eventAttachments, 1));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutation.getMutatingContent());
    }

    @Test
    public void testDefaultIsUsedWhenOnlyOneTargetExists() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "click", "func"));
        AbstractReplacingAmongMutator mutator = new EventCallbackRAMutator(eventAttachments);
        Mutation mutation = mutator.generateMutation(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutation.getMutatingContent());
    }

    private String getAddEventListenerString(String target, String event, String callback) {
        return target + ".addEventListener(" + event + ", " + callback + ");";
    }
}
