package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.AstUtil;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.VariableDeclaration;

import java.util.Set;

/**
 * Replacing statement including specified node to no-op. Removing a node would cause easy-to-kill
 * mutation (for instance, removing "getElementById('hoge').removeChild('fuga')" from
 * "document.getElementById('hoge').removeChild('fuga');" create "document.;" which is syntactically
 * invalid. To mitigate the issue, this class try to remove whole statement holding specified node.
 */
public abstract class ReplacingToNoOpMutator<T extends Mutatable> extends AbstractMutator<T> {
    private static final Set<Class> CLASS_OF_STATEMENT
            = ImmutableSet.<Class>of(VariableDeclaration.class, ExpressionStatement.class);
    public static final String NO_OPERATION_STR = "/* No-op */";

    public ReplacingToNoOpMutator(Class<? extends T> applicableClass) {
        super(applicableClass);
    }

    @Override
    public Mutation generateMutation(T originalNode) {
        return new Mutation(
                AstUtil.parentOfAnyTypes(originalNode.getAstNode(), CLASS_OF_STATEMENT, true),
                NO_OPERATION_STR);
    }
}
