package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import org.mozilla.javascript.ast.AstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeBlankResponseBodyMutator extends AbstractMutator<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeBlankResponseBodyMutator.class);

    FakeBlankResponseBodyMutator() {
        super(Request.class);
    }

    @Override
    public Mutation generateMutation(Request originalNode) {
        AstNode successHandler = originalNode.getSuccessHanlder();
        if (successHandler == null) {
            return null;
        }

        if (originalNode.getType() == Request.Type.JQUERY) {
            // success(data, textStatus, jqXHR)
            StringBuilder replacementBuilder = new StringBuilder();
            replacementBuilder
                    .append("function(data, textStatus, jqXHR) {(")
                    .append(successHandler.toSource())
                    .append(")")
                    .append(".apply(this, [/* blank response mutation */'', textStatus, jqXHR]);}");
            return new Mutation(successHandler, replacementBuilder.toString());
        } else {
            LOGGER.info("Unknown request type for " + originalNode.getAstNode());
            return null;
        }
    }
}
