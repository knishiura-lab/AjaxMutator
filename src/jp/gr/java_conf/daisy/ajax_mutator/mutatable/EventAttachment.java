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
}
