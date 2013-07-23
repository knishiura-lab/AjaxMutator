package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class DiffFileGeneratorTest {
    private DiffFileGenerator generator;

    @Before
    public void setup() throws Exception {
        URI resourceUri = this.getClass()
                .getResource("/mutation_generator/original.txt").toURI();
        generator = new DiffFileGenerator(new File(resourceUri));
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

    private List<String> readResourceAndSplitLines(String resourceName) {
        List<String> output = new ArrayList<String>();
        Scanner scanner = new Scanner(
                this.getClass().getResourceAsStream(resourceName)
        );
        while (scanner.hasNext()) {
            output.add(scanner.nextLine());
        }
        return output;
    }

    // Helper method to compare output with resource to (1) avoid new line code
    // issue and (2) make debug easier by doing direct String comparison.
    private void assertSameContentInFile(String resourcePath, String content) {
        assertEquals(
                StringUtils.join(
                        readResourceAndSplitLines(resourcePath), System.lineSeparator()),
                content
        );
    }
}
