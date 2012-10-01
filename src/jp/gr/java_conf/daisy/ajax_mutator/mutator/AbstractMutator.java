package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import java.io.PrintStream;

import org.mozilla.javascript.ast.AstNode;

/**
 * Provide utility methods to implement Mutator
 *  
 * @author Kazuki Nishiura
 */
public abstract class AbstractMutator implements Mutator {
	protected PrintStream stream;
	
	protected AbstractMutator(PrintStream stream) {
		this.stream = stream;
	}
	
	protected void printMutationInformation(AstNode from, AstNode to) {
		if (stream != null) {
			StringBuilder builder = new StringBuilder();
			AstNode parent = from.getParent();
			builder.append("mutate '");
			builder.append(from.toSource());
			builder.append("' in \"");
			builder.append(parent.toSource());
			builder.append("\" (at line ");
			builder.append(parent.getLineno());
			builder.append(") -> '");
			builder.append(to.toSource());
			builder.append("'");
			stream.println(builder);
		}
	}
	
	protected boolean ifEquals(AstNode node1, AstNode node2) {
		return node1.toSource().equals(node2.toSource());
	}
}
