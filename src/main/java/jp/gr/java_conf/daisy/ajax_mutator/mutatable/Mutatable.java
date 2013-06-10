package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.util.AstUtil;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
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
    private boolean lastMutationSuccessed;

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

        AstNode parent = from.getParent();
        if (AstUtil.isContained(to, parent)) {
            // TODO: currently we just ignore mutation request.
            // In the future, we can do it another way to tell callee to tell
            // the result (e.g., throw exception?)
            lastMutationSuccessed = false;
            System.err.println("Cannot replace "
                    + Util.oneLineStringOf(from) + "("
                    + Util.oneLineStringOf(parent)
                    + ") to " + Util.oneLineStringOf(to) + "("
                    + Util.oneLineStringOf(to.getParent())
                    + ")\n, quit this mutation");
            return;
        }

        lastMutationSuccessed = true;
        replace(parent, from, to);
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
        } else if (parent instanceof ExpressionStatement) {
            replaced = applyReplaceTo((ExpressionStatement) parent, from, to);
        } else {
            try {
                parent.replaceChild(from, to);
                replaced = true;
            } catch (NullPointerException exception) {
                System.err.println("Null point error happens when replacing:");
                exception.printStackTrace();
                System.err.println("parent is " + parent.toSource()
                        + " (class:" + parent.getClass().getSimpleName() + ")"
                        + " and " + (parent.hasChildren() ? "no" : "has")
                        + " child") ;
                System.err.println(to.toSource() + " : " + to.getParent());
                System.err.println(from.toSource() + " : " + from.getParent().toSource());
            }
        }

        if (!replaced) {
            throw new IllegalArgumentException("Cannot replace ["
                    + from.toSource() + " in '" + from.getParent().toSource()
                    + "' at line " + from.getParent().getLineno() + "] to ["
                    + to.toSource() + " in '" + to.getParent().toSource()
                    + "' at line " + to.getParent().getLineno() + "]" + " : "
                    + "parent's class is " + parent.getClass().getSimpleName());
        }
    }

    public void undoLastReplace() {
        if (lastMutationSuccessed) {
            replace(lastReplacedFrom.getParent(), lastReplacedTo, lastReplacedFrom);
            lastReplacedTo.setParent(parentOfLastReplacedTo);
        }
    }

    private boolean applyReplaceTo(PropertyGet propertyGet, AstNode from,
            AstNode to) {
        if (propertyGet.getProperty().equals(from) && to instanceof Name) {
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

    private boolean applyReplaceTo(
            InfixExpression expression, AstNode from, AstNode to) {
        if (expression.getLeft().equals(from)) {
            expression.setLeft(to);
            return true;
        } else if (expression.getRight().equals(from)) {
            expression.setRight(to);
            return true;
        }
        return false;
    }

    private boolean applyReplaceTo(
            ExpressionStatement statement, AstNode from, AstNode to) {
        if (from.equals(statement.getExpression())) {
            statement.setExpression(to);
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
