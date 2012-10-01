package jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.MutationPointDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

/**
 * Detect timer event registration call, which is, setTimeout or setInterval
 * 
 * @author Kazuki Nishiura
 */
public class TimerEventDetector implements MutationPointDetector {
	private static String SET_TIMEOUT_IDENTIFIIER = "setTimeout";
	private static String SET_INTERVAL_IDENTIFIER = "setInterval";
	
	/**
	 * Detect timer event registration call
	 */
	@Override
	public TimerEventAttachment detect(AstNode node) {
		if (node instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) node;
			return detectFromFunctionCall(call, call.getTarget(), call.getArguments());
		}
		throw new IllegalArgumentException(this.getClass() + ".detect()"
				+ " must receive any function call, but called for " + node.getClass());
	}

	/**
	 * detect timer event attachment from passed function call 
	 * (e.g., window.setInterval(func, 100)).
	 * This method expected to detect:
	 * <ul>
	 * <li>window.setTimeout</li>
	 * <li>window.setInterval</li>
	 * <li>setTimeout</li>
	 * <li>setInterval</li>
	 * </ul>
	 * 
	 * @param functionCall function call (e.g. window.setTimeout(callback, 100))
	 * @param target target for function call (e.g. window.setTimeout)
	 * @param arguments argument for function call (e.g. [callback, 100])
	 * @return TimerEventAttachment instance or null
	 */
	public TimerEventAttachment detectFromFunctionCall(
			FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
		TimerEventAttachment.TimerEventType timerEventType = null;
		if (target instanceof Name) {
			timerEventType = getTimerEventType((Name) target);
		} else if (target instanceof PropertyGet) {
			timerEventType = getTimerEventType(((PropertyGet) target).getProperty());
		}
		
		if (timerEventType != null)
			return new TimerEventAttachment(
					functionCall, arguments.get(0), arguments.get(1), timerEventType);
		else
			return null;
	}
	
	private TimerEventAttachment.TimerEventType getTimerEventType(Name functionName) {
		String functionNameString = functionName.getIdentifier();
		if (SET_TIMEOUT_IDENTIFIIER.equals(functionNameString))
			return TimerEventAttachment.TimerEventType.SET_TIMEOUT;
		else if (SET_INTERVAL_IDENTIFIER.equals(functionNameString))
			return TimerEventAttachment.TimerEventType.SET_INTERVAL;
		else
			return null;
	}
}
