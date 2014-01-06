package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;

public class UnifiedDiffParserTest {
    @Test
    public void testParse() {
        UnifiedDiffParser parser = new UnifiedDiffParser();
        UnifiedDiffParser.Mutation mutation
                = parser.parse(readResourceAndSplitLines("/mutation_viewer/mutant0.diff"));
        assertEquals("quizzy.js", mutation.getFileName());
        assertEquals(59, mutation.getStartLine());
        assertEquals(
                Arrays.asList(new String[] {
                        "\t\t$('.quizzy_quiz_lbl').click(function () {",
                        "\t\t\t// ユーザがある選択肢をクリックした場合",
                        "\t\t\t//the user clicked on one of the options",
                        "\t\t\t// IDの取得",
                        "\t\t\t//get the id",
                        "\t\t\tvar thisId = $(this).attr('id');",
                        "\t\t\t",
                        "\t\t\t// IDからそのインデックスを習得(計算)し，selOpt変数にインデックスを代入",
                        "\t\t\t//hack out the index and set selOpt to it",
                        "\t\t\tvar selQuiz = thisId.substring(thisId.lastIndexOf(\"lbl\") + 3) * 1;",
                        "\t\t\t",
                        "\t\t\t// ラジオボタンが選択されている状態にする",
                        "\t\t\t//make sure that the radio button is selected",
                        "\t\t\t$('#quizzy_quiz_opt'+selQuiz).click();",
                        "\t\t});"
                }),
                mutation.getOriginalLines());
        assertEquals(
                Arrays.asList(new String[] {
                        "\t\t$('.quizzy_quiz_lbl').click(function() {",
                        "  var thisId = $(this).attr('id');",
                        "  var selQuiz = thisId.substring(thisId.lastIndexOf(\"opt\") + 3) * 1;",
                        "  $('.quizzy_quiz_desc[id!=quizzy_quiz_desc' + selQuiz + ']').slideUp(slideSpeed, function() {",
                        "  $('#quizzy_quiz_desc' + selQuiz).slideDown(slideSpeed);",
                        "});",
                        "});"
                }),
                mutation.getMutatedLines());
    }

    @Test
    public void testParseSingleLineFile() {
        UnifiedDiffParser parser = new UnifiedDiffParser();
        UnifiedDiffParser.Mutation mutation
                = parser.parse(readResourceAndSplitLines("/mutation_viewer/mutant34.diff"));
        assertEquals("p3.js", mutation.getFileName());
        assertEquals(124, mutation.getStartLine());
        assertEquals(
                Arrays.asList(new String[] {
                        "  $('body').append('<div class=\"box\" id=\"box_0\" name=\"boxName\"></div> ');"
                }),
                mutation.getOriginalLines());
        assertEquals(
                Arrays.asList(new String[] {
                        "  $('body').append(newBox);"
                }),
                mutation.getMutatedLines());
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
}
