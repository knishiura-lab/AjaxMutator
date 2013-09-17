package jp.gr.java_conf.daisy.ajax_mutator.util;

import org.mozilla.javascript.ast.AstNode;

import java.util.Set;

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

    /**
     * return nearest parent node whose type is one of specified ones.
     * @param isStrict if true, raise exception when no element is found. Otherwise just return null.
     */
    public static AstNode parentOfAnyTypes(AstNode node, Set<Class> types, boolean isStrict) {
        AstNode originalCopy = node;
        while (node != null) {
            for (Class type: types) {
                if (type.isInstance(node)) {
                    return node;

                }
            }
            node = node.getParent();
        }
        if (isStrict) {
            throw new IllegalArgumentException("Cannot find parent of type " + types + " from "
                    + originalCopy.toSource() + "(" + originalCopy.getClass().getSimpleName() + ")");
        }
        return null;
    }
}
