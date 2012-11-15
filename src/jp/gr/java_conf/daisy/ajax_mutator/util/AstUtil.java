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
}
