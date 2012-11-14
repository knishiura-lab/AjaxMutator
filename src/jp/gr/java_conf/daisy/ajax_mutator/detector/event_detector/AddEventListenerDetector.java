package jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

/**
 * Detect event attachment in form of target.addEventListner(event, callback).
 * This is normal event attachment used by Chrome, Firefox, Safari, and so on.
 * 
 * @author Kazuki Nishiura
 */
public class AddEventListenerDetector extends EventAttacherDetector {
	static private String targetString = "addEventListener";

	@Override
	public EventAttachment detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) target;
			if (targetString.equals(propertyGet.getProperty().getIdentifier())) {
				return new EventAttachment(functionCall,
						propertyGet.getTarget(), arguments.get(0),
						arguments.get(1));
			}
		}
		return null;
	}
}
