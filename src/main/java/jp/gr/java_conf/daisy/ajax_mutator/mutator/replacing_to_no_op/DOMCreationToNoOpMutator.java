package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;

/**
 * Replace DOMCreation to No-op.
 */
public class DOMCreationToNoOpMutator extends ReplacingToNoOpMutator<DOMCreation> {
    public DOMCreationToNoOpMutator() {
        super(DOMCreation.class);
    }
}
