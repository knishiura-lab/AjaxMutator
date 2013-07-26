package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;

import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DOMSelectionMutatorTest extends MutatorTestBase {
    private String[] selectors;
    private String[] operations;

    @Override
    void prepare() {
        selectors = new String[] {
                "document.getElementById('piyo')", "$('#abc')", "($('.hoge'))"};
        operations = new String[] {
                ".className = 'abc';", ".attr('id', 'another');", ".hide();"};
        inputs = new String[4];
        for (int i = 0; i < 3; i++)
            inputs[i] = selectors[i] + operations[i];
        inputs[3] = "var hoge= $('#fuga');";
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomSelectionDetectors(ImmutableSet.of(
                new DOMSelectionDetector(), new JQueryDOMSelectionDetector()));
        visitor = builder.build();

        Randomizer.initializeWithMockValues(new double[] { 0.8, 0.2, 0.7, 0.2});
    }

    @AfterClass
    public static void resetRandomizer() {
        Randomizer.setMockMode(false);
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
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(outputs[2], selectors[2] + ".children(':first')"
                + operations[2]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(outputs[3], "var hoge = ($('#fuga')).parent();");
        undoAndAssert(mutator);
        assertTrue(mutator.isFinished());
    }


}
