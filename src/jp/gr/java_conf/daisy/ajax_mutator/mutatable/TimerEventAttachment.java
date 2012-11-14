package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.Util;

import org.mozilla.javascript.ast.AstNode;

/**
 * Timer event attachment call such as setTimeout, setInterval.
 * 
 * @author Kazuki Nishiura
 */
public class TimerEventAttachment extends Mutatable {
	private final TimerEventType timerEventType;
	private AstNode callback;
	private AstNode duration;

	public TimerEventAttachment(AstNode node, AstNode callback,
			AstNode duration, TimerEventType timerEventType) {
		super(node);
		this.timerEventType = timerEventType;
		this.callback = callback;
		this.duration = duration;
	}

	public TimerEventType getTimerEventType() {
		return timerEventType;
	}

	public AstNode getCallback() {
		return callback;
	}

	public AstNode getDuration() {
		return duration;
	}

	public void replaceCallback(AstNode newCallback) {
		replace(callback, newCallback);
	}

	public void replaceDuration(AstNode newDuration) {
		replace(duration, newDuration);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\n');
		builder.append("  ").append("Timer event attachment: [callback:");
		builder.append(Util.oneLineStringOf(callback));
		builder.append(", duration:").append(duration.toSource());
		builder.append("]");
		return builder.toString();
	}

	public enum TimerEventType {
		SET_TIMEOUT, SET_INTERVAL
	}
}
