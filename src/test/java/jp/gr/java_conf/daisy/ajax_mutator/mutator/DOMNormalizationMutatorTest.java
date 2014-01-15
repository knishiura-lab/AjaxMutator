package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMNormalizationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMNormalization;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.DOMNormalizationToNoOpMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.ReplacingToNoOpMutator;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMNormalizationMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "document.findElementById('foo').normalize();",
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomNormalizationDetectors(ImmutableSet.of(new DOMNormalizationDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMCloningToNoOpMutator() {
        Set<DOMNormalization> normalizations = visitor.getDomNormalizations();
        Mutator mutator = new DOMNormalizationToNoOpMutator();
        Mutation mutation = mutator.generateMutation(Iterables.get(normalizations, 0));
        assertEquals(inputs[0], mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
    }
}
