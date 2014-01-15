package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.ReplaceChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryReplaceWithDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMReplacement;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class DOMReplacementMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "document.findElementById('hoge').replaceChild(child1, child2);",
                "$foo.replaceWith($('#bar'));"
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomReplacementDetectors(
                ImmutableSet.of(new ReplaceChildDetector(), new JQueryReplaceWithDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMRemovalToNoOpMutator() {
        Set<DOMReplacement> domReplacements = visitor.getDomReplacements();
        Mutator mutator = new DOMReplacementSrcTargetMutator();
        Mutation mutation = mutator.generateMutation(Iterables.get(domReplacements, 0));
        assertEquals(
                "document.findElementById('hoge').replaceChild(child1, child2)",
                mutation.getOriginalNode().toSource());
        assertEquals(
                "document.findElementById('hoge').replaceChild(child2, child1)",
                mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domReplacements, 1));
        assertEquals("$foo.replaceWith($('#bar'))", mutation.getOriginalNode().toSource());
        assertEquals("$('#bar').replaceWith($foo)", mutation.getMutatingContent());
    }
}
