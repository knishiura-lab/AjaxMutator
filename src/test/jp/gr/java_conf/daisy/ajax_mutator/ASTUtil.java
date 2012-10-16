package test.jp.gr.java_conf.daisy.ajax_mutator;

import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;

public class ASTUtil {
	public static FunctionCall stringToFunctionCall(String javaScriptSnipet) {
		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot ast = parser.parse(javaScriptSnipet, 
				"test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector", 1);
		return (FunctionCall) ((ExpressionStatement) ast.getFirstChild()).getExpression();
	}
}
