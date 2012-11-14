package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMAppending;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Detector that detect element.appendChild(child)
 *
 * @author Kazuki Nishiura
 */
public class AppendChildDetector extends AbstractDetector<DOMAppending> {
	private static String APPEND_CHILD_IDENTIFIER = "appendChild";

	@Override
	public DOMAppending detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}

	@Override
	protected DOMAppending detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) functionCall.getTarget();
			if (APPEND_CHILD_IDENTIFIER.equals(
					propertyGet.getProperty().getIdentifier())) {
				return new DOMAppending(functionCall, propertyGet.getTarget(),
						functionCall.getArguments().get(0));
			}
		}
		return null;
	}
}
