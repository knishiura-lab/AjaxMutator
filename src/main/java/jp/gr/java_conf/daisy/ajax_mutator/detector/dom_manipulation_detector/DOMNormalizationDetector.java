package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMNormalization;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Detector for element.normalize()
 */
public class DOMNormalizationDetector extends AbstractDetector<DOMNormalization> {
    @Override
    public DOMNormalization detect(AstNode node) {
        return detectFromFunctionCall(node);
    }

    @Override
    protected DOMNormalization detectFromFunctionCall(FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            if ("normalize".equals(propertyGet.getProperty().getIdentifier())) {
                return new DOMNormalization(functionCall, propertyGet.getTarget());
            }
        }
        return null;
    }
}
