package jp.gr.java_conf.daisy.ajax_mutator.detector;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

/**
 * Implementation of detector which detect function call for event handler
 *
 * @author Kazuki Nishiura
 */
public abstract class EventAttacherDetector
        extends AbstractDetector<EventAttachment> {
    /**
     * detect event attachment from passed function call
     */
    @Override
    public EventAttachment detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }
}
