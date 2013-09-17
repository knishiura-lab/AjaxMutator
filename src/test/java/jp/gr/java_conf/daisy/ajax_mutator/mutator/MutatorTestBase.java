package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.junit.Before;
import org.mozilla.javascript.ast.AstRoot;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAstRoot;

/**
 * Base class for testing subclasses of {@code Mutator}. This class provide
 * common setups; parsing inputs into AST and visit it to collect mutators..
 * 
 * @author Kazuki Nishiura
 */
public abstract class MutatorTestBase {
    protected AstRoot ast;
    /**
     * Array of JavaScript statements including mutation targets.
     */
    protected String[] inputs;
    /**
     * visitor instance which can detect mutation targets from given inputs.
     */
    protected MutateVisitor visitor;

    /**
     * Prepare for test execution. Subclasses MUST initialize visitor and inputs
     * fields. <br>
     * Note that this method is called in the Constructor.
     */
    protected abstract void prepare();

    /**
     * @throws IllegalStateException
     *             when either inputs or visitor is not initialized by
     *             {@code prepare} method.
     */
    public MutatorTestBase() {
        prepare();
        if (inputs == null || visitor == null)
            throw new IllegalStateException(
                    "field both inputs and visitor MUST be initialized.");
    }

    @Before
    public void before() {
        ast = parseAstRoot(Util.join(inputs));
        ast.visit(visitor);
    }
}
