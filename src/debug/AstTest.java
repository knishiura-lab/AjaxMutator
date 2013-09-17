

import jp.gr.java_conf.daisy.ajax_mutator.ParserWithBrowser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.FunctionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mozilla.javascript.ast.ReturnStatement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Kazuki Nishiura
 */
public class AstTest {
    private static Logger logger = LoggerFactory.getLogger(AstTest.class);

    public static void main(String[] args) {
        ParserWithBrowser parser = ParserWithBrowser.getParser();
        AstRoot astRoot = null;
        try {
            File file = new File("/home/kazuki/dev/AjaxMutator/src/test/resources/mutation_generator/original.txt");
            FileReader fileReader = new FileReader(file);
            logger.info(file.getParent() + File.separator);
//            astRoot = parser.parse(fileReader, "http://hoge.fuga", 1);
            astRoot = parser.parse("function func(){ return func();}", "http://hoge.fuga", 1);
        } catch (IOException e) {
            logger.error("IOException");
//            System.err.println("IOException: cannot parse AST.");
        }

        astRoot.visit(new NodeVisitor() {
            @Override
            public boolean visit(AstNode nodes) {
                if (nodes instanceof FunctionNode) {
                    logger.info(nodes.toString());
                    System.out.println("+++" + nodes.getLineno() + " " + nodes.getAbsolutePosition() + " " + nodes.getLength());
                    System.out.println(nodes.toSource());
                    System.out.println();
                } else if (nodes instanceof ReturnStatement) {
                    System.out.println(((ReturnStatement) nodes).getReturnValue().getClass().getSimpleName());
                }
                return true;
            }
        });
    }
}
