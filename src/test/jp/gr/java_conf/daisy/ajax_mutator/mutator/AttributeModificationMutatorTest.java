package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.AttributeAssignmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AttributeModificationTargetAttributeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AttributeModificationValueMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import com.google.common.collect.ImmutableSet;

public class AttributeModificationMutatorTest extends MutatorTestBase {
	private String[] targetAttributes;
	private String[] assignedValues;
	
	@Override
	void prepare() {
		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setAttributeModificationDetectors(
				ImmutableSet.of(new AttributeAssignmentDetector(), new JQueryAttributeModificationDetector()));
		visitor = builder.build();
		targetAttributes = new String[] {"id", "hidden"};
		assignedValues = new String[] {"hoge", "'true'"};
		inputs = new String[2];
		inputs[0] = getJQueryAssignment(targetAttributes[0], assignedValues[0]);
		inputs[1] = getAssignment(targetAttributes[1], assignedValues[1]);
	}
	
	@Test
	public void testAttributeModificationAttributeMutator() {
		Mutator mutator = new AttributeModificationTargetAttributeMutator(
				System.out, visitor.getAttributeModifications());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String[] outputs = ast.toSource().split("\n");
		assertEquals(getJQueryAssignment(targetAttributes[1], assignedValues[0]), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(getAssignment(targetAttributes[0], assignedValues[1]), outputs[1]);
		undoAndAssert(mutator);
	}
	
	@Test
	public void testAttributeModificationValueMutator() {
		Mutator mutator = new AttributeModificationValueMutator(
				System.out, visitor.getAttributeModifications());
		assertFalse(mutator.isFinished());
		mutator.applyMutation();
		String [] outputs = ast.toSource().split("\n");
		assertEquals(getJQueryAssignment(targetAttributes[0], assignedValues[1]), outputs[0]);
		assertEquals(inputs[1], outputs[1]);
		undoAndAssert(mutator);
		mutator.applyMutation();
		outputs = ast.toSource().split("\n");
		assertEquals(inputs[0], outputs[0]);
		assertEquals(getAssignment(targetAttributes[1], assignedValues[0]), outputs[1]);
		undoAndAssert(mutator);
	}

	private String getJQueryAssignment(String attribute, String value) {
		return "$('#hoge').attr(" + attribute + ", " + value + ");";
	}
	
	private String getAssignment(String attribute, String value) {
		return "element." + attribute + " = " + value + ";";
	}
}
