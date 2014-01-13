package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCloning;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Detector for element.cloneNode
 */
public class CloneNodeDetector extends AbstractDetector<DOMCloning> {
    @Override
    public DOMCloning detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }

    @Override
    protected DOMCloning detectFromFunctionCall(FunctionCall functionCall,
                                                  AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            String methodName = propertyGet.getProperty().getIdentifier();

            if ("cloneNode".equals(methodName)) {
                return new DOMCloning(functionCall, propertyGet.getTarget());
            }
        }

        return null;
    }
}