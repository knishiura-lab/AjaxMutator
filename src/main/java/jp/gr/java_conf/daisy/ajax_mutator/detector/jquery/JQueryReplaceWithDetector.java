package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMReplacement;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Detector for elm.replaceWith(foo)
 */
public class JQueryReplaceWithDetector extends AbstractDetector<DOMReplacement> {
    private static String REPLACE_CHILD_IDENTIFIER = "replaceWith";

    @Override
    public DOMReplacement detect(AstNode node) {
        return detectFromFunctionCall(node);
    }

    @Override
    protected DOMReplacement detectFromFunctionCall(
            FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            if (REPLACE_CHILD_IDENTIFIER.equals(propertyGet.getProperty().getIdentifier())
                    && arguments.size() >= 1) {
                return new DOMReplacement(functionCall, propertyGet.getTarget(), arguments.get(0));
            }
        }
        return null;
    }
}
