package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;

/**
 * detector that detect jQuery's dom selection like $("#hoge") or elm.children("#hoge")
 * 
 * jQuery provide rich selectors such as $("#prev ~ div"), but we donot 
 * detect how they select elements, we just regards all of them as 
 * 'selected by SelectionMethod.JQUERY'.
 *
 * @author Kazuki Nishiura
 */
public class JQueryDOMSelectionDetector extends AbstractDetector<DOMSelection> {
	@Override
	public DOMSelection detect(AstNode node) {
		return detectFromFunctionCall(node, true);
	}
	
	@Override
	protected DOMSelection detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof Name && "$".equals(((Name) target).getIdentifier())) {
			return new DOMSelection(functionCall, null, DOMSelection.SelectionMethod.JQUERY, arguments.get(0));
		}
		
		if (target instanceof PropertyGet) {
			if ("children".equals((((PropertyGet) target).getProperty()).getIdentifier())
					&& arguments.size() > 0) {
				return new DOMSelection(functionCall, ((PropertyGet) target).getTarget(),
						DOMSelection.SelectionMethod.JQUERY, arguments.get(0));
			}
		}
		
		return null;
	}
}
