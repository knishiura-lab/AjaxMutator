package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_to_no_op;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMNormalization;

/**
 * Replace DOM Normalization to No-op
 */
public class DOMNormalizationToNoOpMutator  extends ReplacingToNoOpMutator<DOMNormalization> {
    public DOMNormalizationToNoOpMutator() {
        super(DOMNormalization.class);
    }
}
