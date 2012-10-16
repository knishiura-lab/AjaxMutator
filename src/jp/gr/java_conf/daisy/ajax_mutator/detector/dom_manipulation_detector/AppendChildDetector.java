package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DomAppending;

/**
 * Detector that detect element.appendChild(child)
 * 
 * @author Kazuki Nishiura
 */
public class AppendChildDetector extends AbstractDetector<DomAppending> {
	private static String APPEND_CHILD_IDENTIFIER = "appendChild";
	
	@Override
	public DomAppending detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}
	
	@Override
	protected DomAppending detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) functionCall.getTarget();
			if (APPEND_CHILD_IDENTIFIER.equals(propertyGet.getProperty().getIdentifier())) {
				return new DomAppending(functionCall, propertyGet.getTarget(), functionCall.getArguments().get(0));
			}	
		}
		return null;
	}
}
