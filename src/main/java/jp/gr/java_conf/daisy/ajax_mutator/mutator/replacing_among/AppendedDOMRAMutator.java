package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class AppendedDOMRAMutator
        extends AbstractReplacingAmongMutator<DOMAppending> {
    public AppendedDOMRAMutator(Collection<DOMAppending> mutationTargets) {
        super(DOMAppending.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(DOMAppending node) {
        return node.getAppendedDom();
    }
}
