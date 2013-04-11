package jp.gr.java_conf.daisy.ajax_mutator.util;

import org.mozilla.javascript.ast.AstNode;

public class AstUtil {
    private AstUtil () {}

    public static boolean isContained(
            AstNode mayAncestor, AstNode filial) {
        AstNode node = filial;
        while (node != null) {
            if (node.equals(mayAncestor))
                return true;
            node = node.getParent();
        }

        return false;
    }

    /**
     * return nearest parent whose type is type. if such node is not exist,
     *           return null.
     */
    public static <T extends AstNode> T parentOfType(
            AstNode node, Class<T> type) {
        while (node != null) {
            if (type.isInstance(node)) {
                // This cast always success since I check type.isInstance(node)
                @SuppressWarnings("unchecked")
                T ret = (T) node;
                return ret;
            }
            node = node.getParent();
        }
        return null;
    }
}
