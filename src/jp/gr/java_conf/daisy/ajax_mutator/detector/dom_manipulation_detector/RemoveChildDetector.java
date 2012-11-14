package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Implementation of detector to detect element.removeChild(hoge)
 *
 * @author Kazuki Nishiura
 */
public class RemoveChildDetector extends AbstractDetector<DOMRemoval> {
	private static String REMOVE_CHILD_IDENTIFIIER = "removeChild";

	@Override
	public DOMRemoval detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}

	@Override
	protected DOMRemoval detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) target;
			if (REMOVE_CHILD_IDENTIFIIER.equals(
					propertyGet.getProperty().getIdentifier())) {
				return new DOMRemoval(
						functionCall, propertyGet.getTarget(), arguments.get(0));
			}
		}
		return null;
	}
}
