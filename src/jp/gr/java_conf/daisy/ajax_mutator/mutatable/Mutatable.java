package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Mutatable object, which means mutation operator can be applied to astnode held by this instance.
 * 
 * @author Kazuki Nishiura
 */
public abstract class Mutatable {
	protected final AstNode astNode;
	
	public Mutatable(AstNode astNode) {
		this.astNode = astNode;
	}
	
	public AstNode getAstNode() {
		return astNode;
	}
	
	protected void replace(AstNode from, AstNode to) {
		AstNode parent = from.getParent();
		boolean replaced = false;
		
		// Note: from.getParent.replaceChild(from, to) do not work well in some situations...
		if (parent instanceof PropertyGet) {
			replaced = applyReplaceTo((PropertyGet) parent, from, to);
		} else if (parent instanceof FunctionCall) {
			replaced = applyReplaceTo((FunctionCall) parent, from, to);
		} else {
			parent.replaceChild(from, to);
			replaced = true;
		}
		
		if (!replaced) {
			throw new IllegalArgumentException("Cannot replace " + from + " to " + to);
		}
	}
	
	private boolean applyReplaceTo(PropertyGet propertyGet, AstNode from, AstNode to) {
		if (propertyGet.getProperty().equals(from)) {
			propertyGet.setProperty((Name) to);
			return true;
		} else if (propertyGet.getTarget().equals(from)){
			propertyGet.setTarget(to);
			return true;
		}
		return false;
	}
	
	private boolean applyReplaceTo(FunctionCall functionCall, AstNode from, AstNode to) {
		if (functionCall.getTarget().equals(from)) {
			functionCall.setTarget(to);
			return true;
		} else {
			List<AstNode> arguments = functionCall.getArguments();
			if (arguments.contains(from)) {
				arguments.set(arguments.indexOf(from), to);
				return true;
			} else {
				return false;
			}
		}
	}
}
