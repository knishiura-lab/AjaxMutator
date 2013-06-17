package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom appending statement such as element.appendChild
 * 
 * @author Kazuki Nishiura
 */
public class DOMAppending extends Mutatable {
    private AstNode appendTarget;
    private AstNode appendedDom;

    public DOMAppending(AstNode astNode, AstNode appendTarget,
            AstNode appendedDom) {
        super(astNode);
        this.appendTarget = appendTarget;
        this.appendedDom = appendedDom;
    }

    public AstNode getAppendedDom() {
        return appendedDom;
    }

    public AstNode getAppendTarget() {
        return appendTarget;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append('\n');
        builder.append("  DOM appending: [appened:")
                .append(appendedDom.toSource()).append(", to:")
                .append(appendTarget.toSource()).append("]");
        return builder.toString();
    }

    public void replaceAppendedDOM(AstNode newDOM) {
        replace(appendedDom, newDOM);
    }
}
