package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.StringLiteral;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class RequestMethodRAMutator
        extends AbstractReplacingAmongMutator<Request> {
    public RequestMethodRAMutator(Collection<Request> mutationTargets) {
        super(Request.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(Request node) {
        return node.getRequestMethodNode();
    }

    @Override
    protected String formatAccordingTo(AstNode mutatingNode, AstNode mutatedNode) {
        if (mutatedNode instanceof StringLiteral
                && !(mutatingNode instanceof StringLiteral)) {
            // mutated: $.ajax(.., 'POST', ...)
            // mutating: $.get(...)
            return '"' + mutatingNode.toSource().toUpperCase() + '"';
        } else if (mutatingNode instanceof StringLiteral
            && JQueryRequestDetector.AJAX_SHORTCUT_METHODS.contains(
                mutatedNode.toSource().trim())) {
            // mutated: $.get(...)
            // mutating: $.ajax(.., 'POST', ...)
            return ((StringLiteral) mutatingNode).getValue();
        }
        return super.formatAccordingTo(mutatingNode, mutatedNode);
    }
}
