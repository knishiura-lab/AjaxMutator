package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class RequestUrlRAMutator extends AbstractReplacingAmongMutator<Request> {
    public RequestUrlRAMutator(Collection<Request> mutationTargets) {
        super(Request.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(Request node) {
        return node.getUrl();
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsStringLiteral("'http://google.com'");
    }
}
