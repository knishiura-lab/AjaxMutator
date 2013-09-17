package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AppendChildDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AppendedDOMRAMutator;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class DOMAppendingMutatorTest extends MutatorTestBase {
    private String[] appendTo;
    private String[] appendedElements;

    @Override
    protected void prepare() {
        appendTo = new String[] {"element", "document.getElementById('hoge')"};
        appendedElements = new String[] {"document.createElement('p')", "elm"};
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = appendChild(appendTo[i], appendedElements[i]);

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new AppendChildDetector()));
        visitor = builder.build();
    }

    @Test
    public void testAppendedElementRAMutator() {
        Collection<DOMAppending> domAppendings = visitor.getDomAppendings();
        Mutator mutator = new AppendedDOMRAMutator(visitor.getDomAppendings());
        Mutation mutation
                = mutator.generateMutation(Iterables.get(domAppendings, 0));
        assertEquals("elm", mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(domAppendings, 1));
        assertEquals("document.createElement('p')", mutation.getMutatingContent());
    }

    private String appendChild(String appendTo, String appendedElement) {
        return appendTo + ".appendChild(" + appendedElement + ");";
    }
}
