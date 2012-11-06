package jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import com.google.common.collect.ImmutableSet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

/**
 * Event detector that detect jQuery like event attachment.
 * e.g., $("#hoge").click('hoge');
 *       $("#hoge").on('click', hoge);
 *
 * @author Kazuki Nishiura
 */
public class JQueryEventAttachmentDetector extends EventAttacherDetector {
	private final Set<String> jQueryEvents = ImmutableSet.of(
			"blur", "change", "click", "dblclick", "error", "focus", "keydown",
			"keypress", "keyup", "load", "mousedown", "mousemove", "mouseout",
			"mouseover", "mouseup", "resize", "scroll", "select", "submit",
			"unload");
	private final Set<String> jQueryEventAttachers 
		= ImmutableSet.of("bind", "on", "one", "live");
	
	@Override
	public EventAttachment detectFromFunctionCall(
			FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
		if (target instanceof PropertyGet) {
			PropertyGet propertyGet = (PropertyGet) target;
			String methodName = propertyGet.getProperty().getIdentifier();
			if (jQueryEvents.contains(methodName) && arguments.size() > 0) {
				return new EventAttachment(functionCall, propertyGet.getTarget(), 
						propertyGet.getProperty(), arguments.get(0));
			} else if (jQueryEventAttachers.contains(methodName)) {
				return new EventAttachment(functionCall, propertyGet.getProperty(),
						arguments.get(0), arguments.get(arguments.size() - 1));
			}
		}
		return null;
	}
}
