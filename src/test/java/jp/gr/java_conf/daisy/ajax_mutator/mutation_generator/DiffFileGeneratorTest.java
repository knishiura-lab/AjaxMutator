package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class DiffFileGeneratorTest {
    private DiffFileGenerator generator;
    private List<String> contentsOfOriginalFile
            = readResourceAndSplitLines("/mutation_generator/original.txt");

    @Before
    public void setup() {
        generator = new DiffFileGenerator();
    }


    @Test
    public void testDiffBodyForMutatingSingleLine() {
        String output = generator.generateUnifiedDiffBody(
                contentsOfOriginalFile,
                1,
                1,
                0,
                contentsOfOriginalFile.get(0).length(),
                ImmutableList.of("var a = 100;")
        );
        assertSameContentInFile(
                "/mutation_generator/replace_first_line.diff.body", output);
    }

    @Test
    public void testDiffBodyForMutatingReplaceMultipleLines() {
        String output = generator.generateUnifiedDiffBody(
                contentsOfOriginalFile,
                5,
                3,
                "      .addEventListener('click', ".length(),
                contentsOfOriginalFile.get(6).length() - 2,
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
