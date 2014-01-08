package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Implementation of detector to detect element.remove()
 * This match is done only by method name. so false positive might occur.
 */
public class JQueryRemoveDetector extends AbstractDetector<DOMRemoval> {
    private static String REMOVE_IDENTIFIIER = "remove";

    @Override
    public DOMRemoval detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }

    @Override
    protected DOMRemoval detectFromFunctionCall(FunctionCall functionCall,
                                                AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            if (REMOVE_IDENTIFIIER.equals(propertyGet.getProperty().getIdentifier())) {
                return new DOMRemoval(
                        functionCall, /* unknown */null, propertyGet.getTarget());
            }
        }
        return null;
    }
}
