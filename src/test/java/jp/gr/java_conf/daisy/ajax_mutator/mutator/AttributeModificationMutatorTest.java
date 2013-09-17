package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AttributeAssignmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AttributeModificationTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AttributeModificationValueRAMutator;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class AttributeModificationMutatorTest extends MutatorTestBase {
    private String[] targetAttributes;
    private String[] assignedValues;

    @Override
    public void prepare() {
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setAttributeModificationDetectors(ImmutableSet.of(
                new AttributeAssignmentDetector(),
                new JQueryAttributeModificationDetector()));
        visitor = builder.build();
        targetAttributes = new String[] { "id", "hidden" };
        assignedValues = new String[] { "hoge", "'true'" };
        inputs = new String[2];
        inputs[0] = getJQueryAssignment(targetAttributes[0], assignedValues[0]);
        inputs[1] = getAssignment(targetAttributes[1], assignedValues[1]);
    }

    @Test
    public void testAttributeModificationAttributeRAMutator() {
        Collection<AttributeModification> attributeModifications
                = visitor.getAttributeModifications();
        Mutator mutator = new AttributeModificationTargetRAMutator(
                attributeModifications);
        AttributeModification modification;
        modification = Iterables.get(attributeModifications, 0);
        Mutation mutation = mutator.generateMutation(modification);
        assertEquals(targetAttributes[1], mutation.getMutatingContent());
        modification = Iterables.get(attributeModifications, 1);
        mutation = mutator.generateMutation(modification);
        assertEquals(targetAttributes[0], mutation.getMutatingContent());
    }

    @Test
    public void testAttributeModificationValueRAMutator() {
        Collection<AttributeModification> attributeModifications
                = visitor.getAttributeModifications();
        Mutator mutator = new AttributeModificationValueRAMutator(
                attributeModifications);
        AttributeModification modification;
        modification = Iterables.get(attributeModifications, 0);
        Mutation mutation = mutator.generateMutation(modification);
        assertEquals(assignedValues[1], mutation.getMutatingContent());
        modification = Iterables.get(attributeModifications, 1);
        mutation = mutator.generateMutation(modification);
        assertEquals(assignedValues[0],mutation.getMutatingContent());
    }

    private String getJQueryAssignment(String attribute, String value) {
        return "$('#hoge').attr(" + attribute + ", " + value + ");";
    }

    private String getAssignment(String attribute, String value) {
        return "element." + attribute + " = " + value + ";";
    }
}
