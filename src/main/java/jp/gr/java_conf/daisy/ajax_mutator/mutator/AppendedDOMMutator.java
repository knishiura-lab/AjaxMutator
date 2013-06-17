package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import org.mozilla.javascript.ast.AstNode;

import java.io.PrintStream;
import java.util.Collection;

public class AppendedDOMMutator extends
        AbstractReplacingAmongMutator<DOMAppending> {
    public AppendedDOMMutator(Collection<DOMAppending> mutationTargets) {
        this(mutationTargets, DEFAULT_STREAM);
    }

    public AppendedDOMMutator(Collection<DOMAppending> mutationTargets,
                            PrintStream printStream) {
        super(mutationTargets, printStream);
    }

    @Override
    protected AstNode getFocusedNode(DOMAppending node) {
        return node.getAppendedDom();
    }

    @Override
    protected void replaceFocusedNodeOf(DOMAppending parent, AstNode newDOM) {
        parent.replaceAppendedDOM(newDOM);
    }
}
