package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AppendChildDetector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DOMAppendingMutatorTest extends MutatorTestBase {
    private String[] appendTo;
    private String[] appendedElements;

    @Override
    void prepare() {
        appendTo = new String[] {"element", "document.getElementById('hoge')"};
        appendedElements = new String[] {"document.createElement('p')", "elm"};
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = appendChild(appendTo[i], appendedElements[i]);

        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new AppendChildDetector()));
        visitor = builder.build();
    }

    @Test
    public void testAppendedElementMutator() {
        Mutator mutator = new AppendedDOMMutator(visitor.getDomAppendings());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(appendChild(appendTo[0], appendedElements[1]), outputs[0]);
        assertEquals(inputs[1], outputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(inputs[0], outputs[0]);
        assertEquals(appendChild(appendTo[1], appendedElements[0]), outputs[1]);
        undoAndAssert(mutator);
    }

    private String appendChild(String appendTo, String appendedElement) {
        return appendTo + ".appendChild(" + appendedElement + ");";
    }
}
