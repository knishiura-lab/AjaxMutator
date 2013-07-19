package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import java.util.List;

/**
 * @author Kazuki Nishiura
 */
public class DiffFileGenerator {

    /**
     * @param contentsOfOriginalFile contents of original file which will be
     *                               applied generated patch. Each element
     *                               corresponds to each line of the file.
     * @param mutationStartLine The index of line where mutation starts. In this
     *                          context, index starts from 1; if the first line
     *                          is a target of mutation, this argument must be
     *                          one.
     * @param numOfLinesForMutation The number of lines in original file that
     *                              will be mutated.
     * @param positionOfStartPoint The index in the first mutated line that will
     *                             mutated to a new content.
     * @param positionOfEndPoint The index in the last mutated line that will
     *                           mutated to a new content.
     * @param mutatingContent a new content that may include some comment
     *                        denoting here is auto-assigned code. Each element
     *                        of the list corresponds to an each line.
     * @return unified diff-formatted string that representing mutation
     */
    public String generateUnifiedDiffBody(
            List<String> contentsOfOriginalFile, int mutationStartLine,
            int numOfLinesForMutation, int positionOfStartPoint,
            int positionOfEndPoint, List<String> mutatingContent
    ) {
        StringBuilder builder = new StringBuilder();
        // First line (e.g., "@@ -2,3 +2,5 @@"
        builder.append("@@ -").append(mutationStartLine);
        if (numOfLinesForMutation > 1) {
            builder.append(',').append(numOfLinesForMutation);
        }
        builder.append(' ').append('+').append(mutationStartLine);
        if(mutatingContent.size() > 1) {
            builder.append(',').append(mutatingContent.size());
        }
        builder.append(" @@").append(System.lineSeparator());

        // Lines corresponding original file
        for (int i = 0; i < numOfLinesForMutation; i++) {
            builder.append("-")
                    .append(contentsOfOriginalFile.get(mutationStartLine + i - 1))
                    .append(System.lineSeparator());
        }

        String prefix = contentsOfOriginalFile
                .get(mutationStartLine - 1).substring(0, positionOfStartPoint);
        String lastMutatedLine = contentsOfOriginalFile
                .get(mutationStartLine + numOfLinesForMutation - 1 - 1);
        String suffix = lastMutatedLine.substring(
                positionOfEndPoint, lastMutatedLine.length());
        for (int i = 0; i < mutatingContent.size(); i++) {
            builder.append('+');
            if (i == 0) {
                builder.append(prefix);
            }
            builder.append(mutatingContent.get(i));
            if (i == mutatingContent.size() - 1) {
                builder.append(suffix);
            } else {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
