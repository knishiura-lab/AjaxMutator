package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.Util;

import org.mozilla.javascript.ast.AstNode;

/**
 * Event attachment statement such as element.addEventListner or
 * element.attachEvent
 * 
 * @author Kazuki Nishiura
 */
public class EventAttachment extends Mutatable {
	private AstNode target;
	private AstNode event;
	private AstNode callback;

	public EventAttachment(AstNode astNode, AstNode target, AstNode event,
			AstNode callback) {
		super(astNode);
		this.target = target;
		this.event = event;
		this.callback = callback;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\n');
		builder.append("  ").append("Event attachment: ");
		builder.append("[target: " + target.toSource());
		builder.append(", event:" + event.toSource());
		builder.append(", callback: " + Util.oneLineStringOf(callback));
		builder.append("]");
		return builder.toString();
	}

	public AstNode getTarget() {
		return target;
	}

	public AstNode getEvent() {
		return event;
	}

	public AstNode getCallback() {
		return callback;
	}

	public void replaceTarget(AstNode newTarget) {
		replace(target, newTarget);
	}

	public void replaceEvent(AstNode newEvent) {
		replace(event, newEvent);
	}

	public void replaceCallback(AstNode newCallback) {
		replace(callback, newCallback);
	}
}
