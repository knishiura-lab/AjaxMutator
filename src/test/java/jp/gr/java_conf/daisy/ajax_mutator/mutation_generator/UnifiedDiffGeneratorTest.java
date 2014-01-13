package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class UnifiedDiffGeneratorTest {
    private UnifiedDiffGenerator generator;

    @Before
    public void setup() throws Exception {
        URI resourceUri = this.getClass()
                .getResource("/mutation_generator/original.txt").toURI();
        generator = new UnifiedDiffGenerator(new File(resourceUri));
    }

    @Test
    public void testDiffBodyFromIndicesForMutatingSingleLine() {
        String output = generator.generateUnifiedDiffBodyFromExactIndices(
                1,
                1,
                0,
                "var a = 200;".length(),
                ImmutableList.of("var a = 100;")
        );
        assertSameContentInFile(
                "/mutation_generator/replace_first_line.diff.body", output);
    }

    @Test
    public void testDiffBodyFromIndicesForMutatingReplaceMultipleLines() {
        String output = generator.generateUnifiedDiffBodyFromExactIndices(
                5,
                3,
                "      .addEventListener('click', ".length(),
                "      });".length() - 2,
                ImmutableList.of(
                        "function(event) {",
                        "         var identifier = \"main\";",
                        "         console.log(event.target);",
                        "         console.log(document.getElementById(identifier));",
                        "      }"
                )
        );
        assertSameContentInFile(
                "/mutation_generator/replace_event_handler.diff.body", output);
    }

    @Test
    public void testDiffBodyForMutatingSingleLine() {
        AstNode mockNode = EasyMock.createMock(AstNode.class);
        EasyMock.expect(mockNode.getAbsolutePosition()).andReturn(0);
        EasyMock.expect(mockNode.getLength()).andReturn(12);
        EasyMock.replay(mockNode);

        String output = generator.generateUnifiedDiffBody(
                mockNode,
                ImmutableList.of("var a = 100;"));
        assertSameContentInFile(
                "/mutation_generator/replace_first_line.diff.body", output);
    }

    @Test
    public void testDiffBodyForMutatingReplaceMultipleLines() {
        AstNode mockNode = EasyMock.createMock(AstNode.class);
        EasyMock.expect(mockNode.getAbsolutePosition()).andReturn(117);
        EasyMock.expect(mockNode.getLength()).andReturn(58);
        EasyMock.replay(mockNode);

        String output = generator.generateUnifiedDiffBody(
                mockNode,
                ImmutableList.of(
                        "function(event) {",
                        "         var identifier = \"main\";",
                        "         console.log(event.target);",
                        "         console.log(document.getElementById(identifier));",
                        "      }"
        ));
        assertSameContentInFile(
                "/mutation_generator/replace_event_handler.diff.body", output);
    }

    // Helper method to compare output with resource to make debug easier by doing direct
    // String comparison.
    private void assertSameContentInFile(String resourcePath, String content) {
        URL url = this.getClass().getResource(resourcePath);
        String resourceContent = null;
        try {
            resourceContent = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {

        }
        assertEquals(resourceContent, content);
    }
}
