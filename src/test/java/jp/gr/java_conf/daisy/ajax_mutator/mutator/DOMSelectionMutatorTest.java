package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;

import org.junit.AfterClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class DOMSelectionMutatorTest extends MutatorTestBase {
    private String[] selectors;
    private String[] operations;

    @Override
    public void prepare() {
        selectors = new String[] {
                "document.getElementById('piyo')", "$('#abc')", "($('.hoge'))"};
        operations = new String[] {
                ".className = 'abc';", ".attr('id', 'another');", ".hide();"};
        inputs = new String[4];
        for (int i = 0; i < 3; i++)
            inputs[i] = selectors[i] + operations[i];
        inputs[3] = "var hoge = $('#fuga');";
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomSelectionDetectors(ImmutableSet.of(
                new DOMSelectionDetector(), new JQueryDOMSelectionDetector()));
        visitor = builder.build();

        Randomizer.initializeWithMockValues(new double[] {0.8, 0.2, 0.7, 0.2});
    }

    @AfterClass
    public static void resetRandomizer() {
        Randomizer.setMockMode(false);
    }

    @Test
    public void testDomSelectionMutation() {
        String[] expectedTarget
                = {selectors[0], selectors[1], "$('.hoge')", "$('#fuga')"};
        String[] expectedMutatingContents = {
                "(document.getElementById('piyo')).children[0]",
                "($('#abc')).parent()",
                "($('.hoge')).children(':first')",
                "($('#fuga')).parent()"};

        Collection<DOMSelection> domSelections = visitor.getDomSelections();
        Mutator mutator = new DOMSelectionSelectNearbyMutator();
        for (int i = 0; i < 4; i++) {
            Mutation mutation
                    = mutator.generateMutation(Iterables.get(domSelections, i));
            assertEquals(
                    expectedTarget[i], mutation.getOriginalNode().toSource());
            assertEquals(
                    expectedMutatingContents[i],
                    mutation.getMutatingContent());
        }
    }
}
