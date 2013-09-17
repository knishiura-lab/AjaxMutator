import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;
import org.mozilla.javascript.EvaluatorException;

/**
 * @author Kazuki Nishiura
 */
public class ASTInvalidTest {

    // What happens if we pass invalid js?
    public static void main(String[] args) {
        ParserWithBrowser parser = ParserWithBrowser.getParser();
        try {
            parser.parse("function hoge() {console.log('hello, world);}", "http://test.com", 1);
        } catch (EvaluatorException e) {
            // Expected exception.
            e.printStackTrace();
        }
    }

}
