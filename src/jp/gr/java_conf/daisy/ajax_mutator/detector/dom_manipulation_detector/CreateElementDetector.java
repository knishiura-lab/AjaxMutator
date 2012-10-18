package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Detector to detect dom creation like document.createElement("tagName")
 *
 * @author Kazuki Nishiura
 */
public class CreateElementDetector extends AbstractDetector<DOMCreation> {
	private static String CREATE_ELEMENT_IDENTIFIER = "createElement";

	@Override
	public DOMCreation detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}

	@Override
	protected DOMCreation detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (functionCall.getTarget() instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) functionCall.getTarget();
			if (CREATE_ELEMENT_IDENTIFIER.equals(
					propertyGet.getProperty().getIdentifier())) {
				return new DOMCreation(functionCall, arguments.get(0));
			}
		}
		return null;
	}
}
