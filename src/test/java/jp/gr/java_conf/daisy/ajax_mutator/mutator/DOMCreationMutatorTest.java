package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.CreateElementDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.DOMCreationToNoOpMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op.ReplacingToNoOpMutator;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMCreationMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "var btn = document.createElement('div');",
                "// some comment here" + System.lineSeparator(),
                "btn = document.createElement('span');",
                "var hoge = fuga;",
                "hoge.appendChild(document.createElement('a'));"
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomCreationDetectors(ImmutableSet.of(new CreateElementDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMCreationToNoOpMutator() {
        Set<DOMCreation> domCreations = visitor.getDomCreations();
        Mutator mutator = new DOMCreationToNoOpMutator();
        Mutation mutation = mutator.generateMutation(Iterables.get(domCreations, 0));
        assertEquals(inputs[0],
                mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domCreations, 1));
        assertEquals(inputs[2],
                mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domCreations, 2));
        assertEquals(inputs[4],
                mutation.getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutation.getMutatingContent());
    }
}