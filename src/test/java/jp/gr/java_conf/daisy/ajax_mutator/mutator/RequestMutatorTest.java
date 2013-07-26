package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.util.Util;
import org.junit.Test;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RequestMutatorTest extends MutatorTestBase {
    private String[] urls;
    private String[] callbacks;

    @Override
    public void prepare() {
        urls = new String[] { "'hoge.php'", "url" };
        callbacks = new String[] { "func1", "func2" };
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setRequestDetectors(
                ImmutableSet.of(new JQueryRequestDetector()));
        visitor = builder.build();
        inputs = new String[] {
                jQueryGet(urls[0], callbacks[0], null),
                jQueryPost(urls[1], callbacks[1], "{hoge: 'fuga'}")
        };
    }

    @Test
    public void testRequestUrlMutator() {
        Mutator mutator = new RequestUrlMutator(visitor.getRequests());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(jQueryGet(urls[1], callbacks[0], null), outputs[0]);
        assertEquals(inputs[1], outputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(inputs[0], outputs[0]);
        assertEquals(jQueryPost(urls[0], callbacks[1], "{hoge: 'fuga'}"),
                outputs[1]);
        undoAndAssert(mutator);
    }

    @Test
    public void testRequestOnSuccessCallbackMutator() {
        Mutator mutator = new RequestOnSuccessHandlerMutator(
                visitor.getRequests());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(jQueryGet(urls[0], callbacks[1], null), outputs[0]);
        assertEquals(inputs[1], outputs[1]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(inputs[0], outputs[0]);
        assertEquals(jQueryPost(urls[1], callbacks[0], "{hoge: 'fuga'}"),
                outputs[1]);
        undoAndAssert(mutator);
    }

    @Test
    public void testRequestMethodMutator() {
        Mutator mutator = new RequestMethodMutator(visitor.getRequests());
        assertFalse(mutator.isFinished());
        mutator.applyMutation();
        String[] outputs = ast.toSource().split("\n");
        assertEquals(jQueryPost(urls[0], callbacks[0], null), outputs[0]);
        undoAndAssert(mutator);
        mutator.applyMutation();
        outputs = ast.toSource().split("\n");
        assertEquals(
                jQueryGet(urls[1], callbacks[1], "{hoge: 'fuga'}"), outputs[1]);
        undoAndAssert(mutator);
    }

    @Test
    public void testRequestMethodMutatorForAjaxMethod() {
        prepare();
        ast = parseAstRoot("$.ajax('fuga.php', {type: 'PUT'});");
        ast.visit(visitor);
        Mutator mutator = new RequestMethodMutator(visitor.getRequests());
        mutator.applyMutation();
        assertEquals(
                "$.ajax('fuga.php', {type: 'GET'});",
                ast.toSource().trim());
        mutator.undoMutation();
        assertEquals(
                "$.ajax('fuga.php', {type: 'PUT'});",
                ast.toSource().trim());
    }

    private String jQueryRequest(
            String methodName, String url, String callback, String data) {
        return "$." + methodName + "(" + url + ", " + (data != null ? data +
                ", " : "") + callback + ");";
    }

    private String jQueryGet(String url, String callback, String data) {
        return jQueryRequest("get", url, callback, data);
    }

    private String jQueryPost(String url, String callback, String data) {
        return jQueryRequest("post", url, callback, data);
    }
}
