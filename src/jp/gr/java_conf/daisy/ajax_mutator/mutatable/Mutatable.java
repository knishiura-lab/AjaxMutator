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
public abstract class Mutatable implements Comparable<Mutatable> {
	protected final AstNode astNode;
	private AstNode lastReplacedFrom;
	private AstNode lastReplacedTo;
	
	public Mutatable(AstNode astNode) {
		this.astNode = astNode;
	}
	
	public AstNode getAstNode() {
		return astNode;
	}
	
	protected void replace(AstNode from, AstNode to) {
		AstNode _lastReplacedFrom = from;
		AstNode _lastReplacedTo = to;
		replace(from.getParent(), from, to);
		
		lastReplacedFrom = _lastReplacedFrom;
		lastReplacedTo = _lastReplacedTo;
	}
	
	private void replace(AstNode parent, AstNode from, AstNode to) {
		boolean replaced = false;
		if (parent instanceof PropertyGet) {
			replaced = applyReplaceTo((PropertyGet) parent, from, to);
		} else if (parent instanceof FunctionCall) {
			replaced = applyReplaceTo((FunctionCall) parent, from, to);
		} else {
			parent.replaceChild(from, to);
			replaced = true;
		}
		if (!replaced) {
			throw new IllegalArgumentException(
					"Cannot replace " + from.toSource() + "(" 
					+ from.getParent().toSource() +  ") to " + to.toSource() 
					+ "(" + to.getParent().toSource() + ")");
		}
	}
	
	public void undoLastReplace() {
		replace(lastReplacedFrom.getParent(), lastReplacedTo, lastReplacedFrom);
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
				StringBuilder builder = new StringBuilder();
				builder.append("[");
				for (AstNode node: arguments) {
					builder.append(node.toSource() + ",");
				}
				builder.setCharAt(builder.length() - 1, ']');
				builder.append(" do not contain ");
				builder.append(from.toSource());
				System.err.println(builder.toString());
				return false;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(astNode.getLineno()).append(' ');
		builder.append(astNode.toSource());
		return builder.toString();
	}

	@Override
	public int compareTo(Mutatable opponent) {
		int lineNo = astNode.getLineno();
		int opponentLineNo = opponent.astNode.getLineno();
		return lineNo - opponentLineNo;
	}
}
