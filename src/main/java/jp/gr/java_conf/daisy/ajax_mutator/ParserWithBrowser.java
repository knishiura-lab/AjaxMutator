package jp.gr.java_conf.daisy.ajax_mutator;

import org.mozilla.javascript.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;

import java.io.*;
import java.net.URL;

/**
 * JavaScript parser with browser environment (e.g., document object).
 *
 * @author Kazuki Nishiura
 */
public class ParserWithBrowser extends Parser {
    private static String PATH_TO_ENV_JS = "/env.rhino.1.2.js";
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
        URL url = ParserWithBrowser.class.getResource(PATH_TO_ENV_JS);

        if (url == null)
            throw new IllegalStateException("Cannot access ENV_JS, "
                    + "which may be unexist under "
                    + ParserWithBrowser.class.getResource("").getPath()
                    + PATH_TO_ENV_JS);
        try {
            context.evaluateReader(scope, new InputStreamReader(url.openStream()), "env.rhino.1.2",
                    1, null);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Environment file '" + PATH_TO_ENV_JS + "' does not exist.");
        } catch (IOException e) {
            throw new IllegalStateException("Instantiating parser failed.");
        }
        CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.initFromContext(context);
        return new ParserWithBrowser(compilerEnvirons);
    }
}
