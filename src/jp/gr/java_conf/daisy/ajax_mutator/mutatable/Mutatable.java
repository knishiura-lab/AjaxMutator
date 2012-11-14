package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.Util;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Mutatable object, which means mutation operator can be applied to astnode
 * held by this instance.
 * 
 * @author Kazuki Nishiura
 */
public abstract class Mutatable implements Comparable<Mutatable> {
	protected final AstNode astNode;
	private AstNode lastReplacedFrom;
	private AstNode lastReplacedTo;
	private AstNode parentOfLastReplacedTo;

	public Mutatable(AstNode astNode) {
		this.astNode = astNode;
	}

	public AstNode getAstNode() {
		return astNode;
	}

	public void replace(AstNode from, AstNode to) {
		lastReplacedFrom = from;
		lastReplacedTo = to;
		parentOfLastReplacedTo = lastReplacedTo.getParent();

		replace(from.getParent(), from, to);
	}

	private void replace(AstNode parent, AstNode from, AstNode to) {
		boolean replaced = false;
		// parent node do not always have replace target as its child node.
		// For instance, assignment node that models "element = val", do not
		// have Variable node 'val' as its child.
		// Therefore, here we have a branch to provide node-specific replace
		// operations.
		if (parent instanceof PropertyGet) {
			replaced = applyReplaceTo((PropertyGet) parent, from, to);
		} else if (parent instanceof FunctionCall) {
			replaced = applyReplaceTo((FunctionCall) parent, from, to);
		} else if (parent instanceof InfixExpression) {
			replaced = applyReplaceTo((InfixExpression) parent, from, to);
		} else {
			parent.replaceChild(from, to);
			replaced = true;
		}
		if (!replaced) {
			throw new IllegalArgumentException("Cannot replace "
					+ from.toSource() + "(" + from.getParent().toSource()
					+ ") to " + to.toSource() + "(" + to.getParent().toSource()
					+ ")");
		}
	}

	public void undoLastReplace() {
		replace(lastReplacedFrom.getParent(), lastReplacedTo, lastReplacedFrom);
		lastReplacedTo.setParent(parentOfLastReplacedTo);
	}

	private boolean applyReplaceTo(PropertyGet propertyGet, AstNode from,
			AstNode to) {
		if (propertyGet.getProperty().equals(from)) {
			propertyGet.setProperty((Name) to);
			return true;
		} else if (propertyGet.getTarget().equals(from)) {
			propertyGet.setTarget(to);
			return true;
		}
		return false;
	}

	private boolean applyReplaceTo(FunctionCall functionCall, AstNode from,
			AstNode to) {
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
				for (AstNode node : arguments) {
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

	private boolean applyReplaceTo(InfixExpression expression, AstNode from,
			AstNode to) {
		System.out.println("hoge");
		if (expression.getLeft().equals(from)) {
			expression.setLeft(to);
			return true;
		} else if (expression.getRight().equals(from)) {
			expression.setRight(to);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(astNode.getLineno()).append(' ');
		builder.append(Util.oneLineStringOf(astNode));
		return builder.toString();
	}

	@Override
	public int compareTo(Mutatable opponent) {
		int lineDiff = astNode.getLineno() - opponent.astNode.getLineno();
		if (lineDiff != 0)
			return lineDiff;
		int posDiff = astNode.getPosition() - opponent.astNode.getPosition();
		if (posDiff != 0)
			return posDiff;
		// In same case, it seems all position is 0, so I need workaround like
		// bellow to distinguish another program element.
		return this.equals(opponent) ? 0 : 1;
	}
}
