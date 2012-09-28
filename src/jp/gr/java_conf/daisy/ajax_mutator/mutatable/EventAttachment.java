package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Event attachment statement
 * 
 * @author Kazuki Nishiura
 */
public class EventAttachment extends Mutatable {
	private AstNode target;
	private AstNode event;
	private AstNode callback;
	
	public EventAttachment(AstNode astNode, AstNode target, AstNode event, AstNode callback) {
		super(astNode);
		this.target = target;
		this.event = event;
		this.callback = callback;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Event attachment: ");
		buffer.append("[target: " + target.toSource());
		buffer.append(", event:" + event.toSource());
		buffer.append(", callback: " + callback.toSource());
		buffer.append("]");
		return buffer.toString();
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
