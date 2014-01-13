package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCloning;

/**
 * Replacing DOM cloning to No-op.
 */
public class DOMCloningToNoOpMutator extends ReplacingToNoOpMutator<DOMCloning> {
    public DOMCloningToNoOpMutator() {
        super(DOMCloning.class);
    }
}
