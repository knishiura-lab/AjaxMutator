package test.jp.gr.java_conf.daisy.ajax_mutator;

import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;

public class ASTUtil {
	public static <T extends AstNode> T stringToType(Class<T> type, String javaScriptSnippet) {
		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot ast = parser.parse(javaScriptSnippet, 
				"test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector", 1);
		
		try {
			@SuppressWarnings("unchecked")
			T ret = (T) ((ExpressionStatement) ast.getFirstChild()).getExpression();
			return ret;
		} catch (ClassCastException e) {
			System.err.println(javaScriptSnippet + " cannot parsed as " + type.getName());
			return null;
		}
	}
	
	public static FunctionCall stringToFunctionCall(String javaScriptSnippet) {
		return stringToType(FunctionCall.class, javaScriptSnippet);
	}
	
	public static Assignment stringToAssignment(String javaScriptSnippet) {
		return stringToType(Assignment.class, javaScriptSnippet);
	}
}
