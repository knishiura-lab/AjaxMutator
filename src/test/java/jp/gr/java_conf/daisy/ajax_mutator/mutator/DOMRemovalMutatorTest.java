package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.RemoveChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.DOMRemovalToNoOpMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.ReplacingToNoOpMutator;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMRemovalMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "document.getElementById('hoge').removeChild(bar);",
                "// some comment here" + System.lineSeparator(),
                "abc.removeChild(document.getElementByTagName('abc'));"
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomRemovalDetectors(ImmutableSet.of(new RemoveChildDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMRemovalToNoNoMutator() {
        Set<DOMRemoval> domRemovals = visitor.getDomRemovals();
        Mutator mutator = new DOMRemovalToNoOpMutator();
        Mutation mutation = mutator.generateMutation(Iterables.get(domRemovals, 0));
        assertEquals(inputs[0],
                mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domRemovals, 1));
        assertEquals(inputs[2],
                mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
    }
}
