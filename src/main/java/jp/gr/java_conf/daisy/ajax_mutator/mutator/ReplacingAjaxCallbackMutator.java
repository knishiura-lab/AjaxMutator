package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import org.mozilla.javascript.ast.AstNode;

/**
 * Replacing onsuccess and onfailure handler of jQuery's ajax method.
 */
public class ReplacingAjaxCallbackMutator extends AbstractMutator<Request> {
    public ReplacingAjaxCallbackMutator() {
        super(Request.class);
    }

    @Override
    public Mutation generateMutation(Request originalNode) {
        AstNode successHandler = originalNode.getSuccessHanlder();
        AstNode failureHandler = originalNode.getFailureHandler();
        if (successHandler == null || failureHandler == null) {
            return null;
        }
        String replacement = originalNode.getAstNode().toSource();
        String PLACE_HOLDER = "__OLD__SUCCESS__HANDLER__";
        String successHandlerSrc = successHandler.toSource();
        String failureHandlerSrc = failureHandler.toSource();
        replacement = replacement.replace(successHandlerSrc, PLACE_HOLDER);
        replacement = replacement.replace(failureHandlerSrc, successHandlerSrc);
        replacement = replacement.replace(PLACE_HOLDER, failureHandlerSrc);
        return new Mutation(originalNode.getAstNode(), replacement);
    }
}
