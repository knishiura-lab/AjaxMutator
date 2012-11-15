package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * detector for jQuery attribute modifications, which can be classfied into two
 * types: (1) attr method: e.g., elm.attr('attr_name', 'attr_value') (2)
 * shortcut method: e.g., elm.width('hoge')
 *
 * @author Kazuki Nishiura
 */
public class JQueryAttributeModificationDetector extends
		AbstractDetector<AttributeModification> {
	private static final String ATTR_KEYWORD = "attr";

	@Override
	public AttributeModification detect(AstNode node) {
		return detectFromFunctionCall(node, false);
	}

	@Override
	public AttributeModification detectFromFunctionCall(
			FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) target;
			String methodName = propertyGet.getProperty().getIdentifier();
			if (ATTR_KEYWORD.equals(methodName) && arguments.size() >= 2) {
				return new AttributeModification(functionCall,
						propertyGet.getTarget(), arguments.get(0),
						arguments.get(1));
			} else if (AttributeModification.JQUERY_ATTR_SHORTCUTS.contains(methodName)
					&& arguments.size() >= 1) {
				return new AttributeModification(functionCall,
						propertyGet.getTarget(), propertyGet.getProperty(),
						arguments.get(0), true);
			}
		}
		return null;
	}
}
