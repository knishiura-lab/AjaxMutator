package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DOMSelectionMutatorTest extends MutatorTestBase {
    private String[] selectors;
    private String[] operations;

    @Override
    void prepare() {
        selectors = new String[] {
                "document.getElementById('piyo')", "$('#abc')"};
        operations = new String[] {
                ".className = 'abc';", ".attr('id', 'another');"};
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = selectors[i] + operations[i];
        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setDomSelectionDetectors(ImmutableSet.of(
                new DOMSelectionDetector(), new JQueryDOMSelectionDetector()));
        visitor = builder.build();

        Randomizer.setValues(new double[]{0.8, 0.2});
    }

    @Test
    public void testDomSelectionMutation() {
        Mutator mutator = new DOMSelectionMutator(visitor.getDomSelections());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(outputs[0], "(" + selectors[0] + ").children[0]"
                + operations[0]);
        assertEquals(outputs[1], inputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(outputs[0], inputs[0]);
        assertEquals(outputs[1], "(" + selectors[1] + ").parent()"
                + operations[1]);
        undoAndAssert(mutator);
        assertTrue(mutator.isFinished());
    }
}
