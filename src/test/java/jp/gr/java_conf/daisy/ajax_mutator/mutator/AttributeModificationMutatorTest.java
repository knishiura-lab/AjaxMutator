package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AttributeAssignmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AttributeModificationMutatorTest extends MutatorTestBase {
    private String[] targetAttributes;
    private String[] assignedValues;

    @Override
    void prepare() {
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
    public void testAttributeModificationAttributeMutator() {
        Mutator mutator = new AttributeModificationTargetAttributeMutator(
                visitor.getAttributeModifications());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(
                getJQueryAssignment(targetAttributes[1], assignedValues[0]),
                outputs[0]);
        assertEquals(inputs[1], outputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(inputs[0], outputs[0]);
        assertEquals(
                getAssignment(targetAttributes[0], assignedValues[1]),
                outputs[1]);
        undoAndAssert(mutator);
    }

    @Test
    public void testAttributeModificationValueMutator() {
        Mutator mutator = new AttributeModificationValueMutator(
                visitor.getAttributeModifications());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(
                getJQueryAssignment(targetAttributes[0], assignedValues[1]),
                outputs[0]);
        assertEquals(inputs[1], outputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(inputs[0], outputs[0]);
        assertEquals(getAssignment(targetAttributes[1], assignedValues[0]),
                outputs[1]);
        undoAndAssert(mutator);
    }

    @Test
    public void testAttributeModificationAttributeMutatorForJQueryShortcuts() {
        String jQueryAttrModifications =
                "$elm1.width(100);"
                + "$elm2.attr('disabled', false);"
                + "$elm3.height(200);";
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setAttributeModificationDetectors(
                ImmutableSet.of(new JQueryAttributeModificationDetector()));
        MutateVisitor visitor = builder.build();
        AstRoot astRoot = StringToAst.parseAstRoot(jQueryAttrModifications);
        astRoot.visit(visitor);
        Mutator mutator = new AttributeModificationTargetAttributeMutator(
                visitor.getAttributeModifications());
        Randomizer.initializeWithMockValues(new double[] {2, 0, 1});
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        undoAndAssert(mutator);
        mutator.applyMutation();
        undoAndAssert(mutator);
        mutator.applyMutation();
        assertEquals("$elm1.width(100);$elm2.attr('disabled', false);"
                + "$elm3.attr('disabled', 200);", Util.omitLineBreak(astRoot));
        undoAndAssert(mutator);
        assertEquals(jQueryAttrModifications, Util.omitLineBreak(astRoot));
        Randomizer.setMockMode(false);
    }

    private String getJQueryAssignment(String attribute, String value) {
        return "$('#hoge').attr(" + attribute + ", " + value + ");";
    }

    private String getAssignment(String attribute, String value) {
        return "element." + attribute + " = " + value + ";";
    }
}
