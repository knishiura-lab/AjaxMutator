package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.MutationPointDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DomAppending;

/**
 * Detector that detect element.appendChild(child)
 * 
 * @author Kazuki Nishiura
 */
public class AppendChildDetector implements MutationPointDetector<DomAppending> {
	private static String APPEND_CHILD_IDENTIFIER = "appendChild";
	
	@Override
	public DomAppending detect(AstNode node) {
		if (node instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) node;
			if (call.getTarget() instanceof PropertyGet) {
				PropertyGet propertyGet = (PropertyGet) call.getTarget();
				if (APPEND_CHILD_IDENTIFIER.equals(propertyGet.getProperty().getIdentifier())) {
					return new DomAppending(node, propertyGet.getTarget(), call.getArguments().get(0));
				}	
			}
		}
		return null;
	}
}
