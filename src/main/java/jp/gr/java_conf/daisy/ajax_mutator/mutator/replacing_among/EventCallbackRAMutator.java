package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class EventCallbackRAMutator
        extends AbstractReplacingAmongMutator<EventAttachment> {
    public EventCallbackRAMutator(Collection<EventAttachment> mutationTargets) {
        super(EventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getCallback();
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsFunctionNode("function(){/* no-op function */}");
    }
}
