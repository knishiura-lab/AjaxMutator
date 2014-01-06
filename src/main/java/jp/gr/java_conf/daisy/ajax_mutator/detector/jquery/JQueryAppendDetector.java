package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * detector for elem.append and elem.appendTo
 */
public class JQueryAppendDetector extends AbstractDetector<DOMAppending> {
    private static final String APPEND_KEYWORD = "append";
    private static final String APPEND_TO_KEYWORD = "appendTo";

    @Override
    public DOMAppending detect(AstNode node) {
        return detectFromFunctionCall(node, false);
    }

    @Override
    public DOMAppending detectFromFunctionCall(
            FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            String methodName = propertyGet.getProperty().getIdentifier();
            if (APPEND_KEYWORD.equals(methodName)) {
                return new DOMAppending(functionCall,
                        propertyGet.getTarget(), arguments.get(0));
            } else if (APPEND_TO_KEYWORD.equals(methodName)) {
                return new DOMAppending(functionCall, arguments.get(0), propertyGet.getTarget());
            }
        }
        return null;
    }
}
