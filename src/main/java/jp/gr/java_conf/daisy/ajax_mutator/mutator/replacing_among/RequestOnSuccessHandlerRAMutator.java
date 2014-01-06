package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class RequestOnSuccessHandlerRAMutator
        extends AbstractReplacingAmongMutator<Request> {

    public RequestOnSuccessHandlerRAMutator(
            Collection<Request> mutationTargets) {
        super(Request.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(Request node) {
        return node.getSuccessHanlder();
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsFunctionNode("function() {}");
    }
}
