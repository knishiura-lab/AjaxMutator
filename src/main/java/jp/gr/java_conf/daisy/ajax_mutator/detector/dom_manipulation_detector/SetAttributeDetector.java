package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * Detector to detect attribute assignment like elm.setAttribute(name, val);
 */
public class SetAttributeDetector extends AbstractDetector<AttributeModification> {
    private static final String SET_ATTRIBUTE_IDENTIFIER = "setAttribute";

    @Override
    public AttributeModification detect(AstNode node) {
        return detectFromFunctionCall(node, false);
    }

    @Override
    protected AttributeModification detectFromFunctionCall(FunctionCall functionCall,
                                                            AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) functionCall.getTarget();
            if (SET_ATTRIBUTE_IDENTIFIER.equals(propertyGet.getProperty().getIdentifier())
                    && arguments.size() >= 2) {
                return new AttributeModification(
                        functionCall,((PropertyGet) functionCall.getTarget()).getTarget(),
                        arguments.get(0), arguments.get(1));
            }
        }
        return null;
    }
}
