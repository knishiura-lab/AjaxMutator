package jp.gr.java_conf.daisy.ajax_mutator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;

public class ParserWithBrowser extends Parser {
	private static String PATH_TO_ENV_JS = "res/env.rhino.1.2.js";
	private static Global global = null;

	private ParserWithBrowser(CompilerEnvirons compilerEnvirons) {
		super(compilerEnvirons);
	}
	
	public static ParserWithBrowser getParser() {
		Context context = ContextFactory.getGlobal().enterContext();
		if (global == null) {
			global = Main.getGlobal();
			global.init(context);
		}
		Scriptable scope = context.initStandardObjects(global);
		context.setOptimizationLevel(-1);
		try {
			context.evaluateReader(scope, new FileReader(PATH_TO_ENV_JS),
					"env.rhino.1.2", 1, null);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Environment file '" + PATH_TO_ENV_JS
					+ "' does not exist.");
		} catch (IOException e) {
			throw new IllegalStateException("Instantiating parser failed.");
		}
		CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
		compilerEnvirons.initFromContext(context);
		return new ParserWithBrowser(compilerEnvirons);
	}
}
