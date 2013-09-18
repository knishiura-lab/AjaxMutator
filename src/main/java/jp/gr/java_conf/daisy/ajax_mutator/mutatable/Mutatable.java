package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.mozilla.javascript.ast.AstNode;

/**
 * Mutatable object, which means mutation operator can be applied to astnode
 * held by this instance.
 *
 * @author Kazuki Nishiura
 */
public abstract class Mutatable implements Comparable<Mutatable> {
    protected final AstNode astNode;

    public Mutatable(AstNode astNode) {
        this.astNode = astNode;
    }

    public AstNode getAstNode() {
        return astNode;
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
