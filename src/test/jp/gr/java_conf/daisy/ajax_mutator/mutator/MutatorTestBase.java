package test.jp.gr.java_conf.daisy.ajax_mutator.mutator;

import static org.junit.Assert.assertEquals;
import static test.jp.gr.java_conf.daisy.ajax_mutator.ASTUtil.stringToAstRoot;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.Util;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

import org.junit.Before;
import org.mozilla.javascript.ast.AstRoot;

public abstract class MutatorTestBase {
	protected AstRoot ast;
	protected MutateVisitor visitor;
	protected String[] inputs;
	
	/**
	 * Prepare for test execution. Subclasses MUST initialize visitor and inputs
	 * fields.
	 */
	abstract void prepare();
	
	public MutatorTestBase() {
		prepare();
	}
	
	@Before
	public void before() {
		ast = stringToAstRoot(Util.join(inputs));
		ast.visit(visitor);
	}
	
	protected void undoAndAssert(Mutator mutator) {
		mutator.undoMutation();
		String[] outputs = ast.toSource().split("\n");
		for (int i = 0; i < inputs.length; i++)
			assertEquals(inputs[0], outputs[0]);
	}
}
