package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.CloneNodeDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryCloneDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCloning;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.DOMCloningToNoOpMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.ReplacingToNoOpMutator;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMCloningMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "$elm.clone();",
                "// some comment here" + System.lineSeparator(),
                "elm.cloneNode(false);",
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomCloningDetectors(ImmutableSet.of(new CloneNodeDetector(), new JQueryCloneDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMCloningToNoOpMutator() {
        Set<DOMCloning> domClonings = visitor.getDomClonings();
        Mutator mutator = new DOMCloningToNoOpMutator();
        Mutation mutation = mutator.generateMutation(Iterables.get(domClonings, 0));
        assertEquals(inputs[0], mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domClonings, 1));
        assertEquals(inputs[2], mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
    }
}