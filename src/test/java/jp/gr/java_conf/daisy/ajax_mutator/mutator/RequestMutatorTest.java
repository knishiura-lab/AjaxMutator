package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestMethodRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestOnSuccessHandlerRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestUrlRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import org.junit.Test;

import java.util.Collection;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestMutatorTest extends MutatorTestBase {
    private String[] urls;
    private String[] callbacks;
    private Collection<Request> requests;

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
        requests = visitor.getRequests();
    }

    @Test
    public void testRequestUrlRAMutator() {
        Mutator mutator = new RequestUrlRAMutator(requests);
        Mutation mutation;
        mutation = mutator.generateMutation(Iterables.get(requests, 0));
        assertEquals(urls[1], mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(requests, 1));
        assertEquals(urls[0], mutation.getMutatingContent());

        mutator = new RequestUrlRAMutator(Sets.newHashSet(Iterables.limit(requests, 1)));
        mutation = mutator.generateMutation(Iterables.get(requests, 0));
        assertEquals("'http://google.com'", mutation.getMutatingContent());
    }

    @Test
    public void testRequestOnSuccessCallbackMutator() {
        Mutator mutator = new RequestOnSuccessHandlerRAMutator(requests);
        Mutation mutation;
        mutation = mutator.generateMutation(Iterables.get(requests, 0));
        assertEquals(callbacks[1], mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(requests, 1));
        assertEquals(callbacks[0], mutation.getMutatingContent());
    }

    @Test
    public void testRequestMethodRAMutator() {
        Mutator mutator = new RequestMethodRAMutator(requests);
        Mutation mutation;
        mutation = mutator.generateMutation(Iterables.get(requests, 0));
        assertEquals("post", mutation.getMutatingContent());
        mutation = mutator.generateMutation(Iterables.get(requests, 1));
        assertEquals("get", mutation.getMutatingContent());

    }

    @Test
    public void testRequestMethodMutatorForAjaxMethod() {
        prepare();
        ast = parseAstRoot(
                "$.ajax('fuga.php', {type: 'PUT'});$.get('hoge.php', callback);");
        ast.visit(visitor);
        Mutator mutator = new RequestMethodRAMutator(visitor.getRequests());
        Mutation mutation;
        mutation = mutator.generateMutation(
                Iterables.get(visitor.getRequests(), 0));
        assertEquals("\"GET\"", mutation.getMutatingContent());
    }

    @Test
    public void testCallbackReplacingMutatorForAjax() {
        prepare();
        ast = parseAstRoot(
                "$.ajax('fuga.php', {success: handleSuccess, error: function(e){console.log(e);}});"
                        + "$.post('just-put.php', {data: someValue});");
        ast.visit(visitor);
        Mutator mutator = new ReplacingAjaxCallbackMutator();
        Mutation mutation;
        mutation = mutator.generateMutation(Iterables.get(visitor.getRequests(), 0));
        assertEquals(
                StringToAst.parseAsFunctionCall("$.ajax('fuga.php', {success: handleSuccess,"
                        + " error: function(e){console.log(e);}});").toSource(),
                mutation.getOriginalNode().toSource());
        assertEquals(
                StringToAst.parseAsFunctionCall("$.ajax('fuga.php', "
                        + "{success: function(e){console.log(e);}, error: handleSuccess});").toSource(),
                mutation.getMutatingContent());
        assertNull(mutator.generateMutation(Iterables.get(visitor.getRequests(), 1)));
    }

    @Test
    public void testPassBlankResponseMutator() {
        prepare();
        ast = parseAstRoot("$.getJSON('fuga.php', handleSuccess);");
        ast.visit(visitor);
        Mutator mutator = new FakeBlankResponseBodyMutator();
        Mutation mutation;
        mutation = mutator.generateMutation(Iterables.get(visitor.getRequests(), 0));
        assertEquals("handleSuccess", mutation.getOriginalNode().toSource());
        assertEquals(
                "function(data, textStatus, jqXHR) {(handleSuccess).apply(this, [/* blank response mutation */'', textStatus, jqXHR]);}",
                mutation.getMutatingContent());
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
