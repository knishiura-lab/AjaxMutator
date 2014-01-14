package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMReplacement;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Detector for element.replaceChild(from, to)
 */
public class ReplaceChildDetector extends AbstractDetector<DOMReplacement> {
    private static final String REPLACE_CHILD_IDENTIFIER = "replaceChild";

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
                    && arguments.size() >= 2) {
                return new DOMReplacement(functionCall, arguments.get(0), arguments.get(1));
            }
        }
        return null;
    }
}
