package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DomRemoval;

/**
 * Implementation of detector to detect element.removeChild(hoge)
 *
 * @author Kazuki Nishiura
 */
public class RemoveChildDetector extends AbstractDetector<DomRemoval> {
	private static String REMOVE_CHILD_IDENTIFIIER = "removeChild";
	
	@Override
	public DomRemoval detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}
	
	@Override
	protected DomRemoval detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) target;
			if (REMOVE_CHILD_IDENTIFIIER.equals(propertyGet.getProperty().getIdentifier())) {
				return new DomRemoval(functionCall, propertyGet.getTarget(), arguments.get(0));
			}
		}
		return null;
	}
}
