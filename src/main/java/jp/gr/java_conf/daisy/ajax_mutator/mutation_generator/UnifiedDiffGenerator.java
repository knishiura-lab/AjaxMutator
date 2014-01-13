package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import com.google.common.annotations.VisibleForTesting;
import org.mozilla.javascript.ast.AstNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Helper class for generating unified diff style strings for given file and
 * given mutation requirement.
 *
 * @author Kazuki Nishiura
 */
class UnifiedDiffGenerator {
    private String fileName;
    private long targetFileLastModifiedMillis;
    private List<String> contentsOfOriginalFile;
    // number of chars for each line in js file, including newline code
    private List<Integer> numOfCharsForLine;

    protected UnifiedDiffGenerator(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("specified file: '"
                     + file + "' does not exist.");
        }
        fileName = file.getName();
        targetFileLastModifiedMillis = file.lastModified();

        try {
            readTargetFileByScanner(new Scanner(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    protected UnifiedDiffGenerator(String fileName, long fileLastModified, InputStream stream) {
        this.fileName = fileName;
        targetFileLastModifiedMillis = fileLastModified;
        readTargetFileByScanner(new Scanner(stream));
    }

    private void readTargetFileByScanner(Scanner scanner) {
        contentsOfOriginalFile = new ArrayList<String>();
        numOfCharsForLine = new ArrayList<Integer>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            contentsOfOriginalFile.add(line);
            numOfCharsForLine.add(line.length());
        }
    }

    /**
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
    @VisibleForTesting
    protected String generateUnifiedDiffBodyFromExactIndices(
            int mutationStartLine, int numOfLinesForMutation,
            int positionOfStartPoint, int positionOfEndPoint,
            List<String> mutatingContent)
    {
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
                builder.append(suffix).append(System.lineSeparator());
            } else {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    /**
     * @param mutatedNode AstNode that represents mutation target
     * @param mutatingContent a new content that may include some comment
     *                        denoting here is auto-assigned code. Each element
     *                        of the list corresponds to an each line.
     * @return unified diff-formatted string that representing mutation
     */
    @VisibleForTesting
    protected String generateUnifiedDiffBody(
            AstNode mutatedNode, List<String> mutatingContent) {
        int absolutePosition = mutatedNode.getAbsolutePosition();
        int startLine = 0;
        int sumLength = 0;
        while (sumLength + numOfCharsForLine.get(startLine) < absolutePosition) {
            sumLength += numOfCharsForLine.get(startLine);
            sumLength += System.lineSeparator().length();
            startLine++;
        }
        int startIndex = absolutePosition - sumLength;

        int endLine = startLine;
        int absoluteEndPosition = absolutePosition + mutatedNode.getLength();
        while (sumLength + numOfCharsForLine.get(endLine) < absoluteEndPosition) {
            sumLength += numOfCharsForLine.get(endLine);
            sumLength += System.lineSeparator().length();
            endLine++;
        }
        int endIndex = absoluteEndPosition - sumLength;

        return generateUnifiedDiffBodyFromExactIndices(startLine + 1,
                endLine - startLine + 1, startIndex, endIndex, mutatingContent);
    }

    private String generateUnifiedDiffHeader() {
        StringBuilder builder = new StringBuilder();
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format
                = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss.SSSS00000");
        builder.append("--- ").append(fileName).append("\t")
                .append(format.format(targetFileLastModifiedMillis)).append(' ')
                .append(System.lineSeparator());
        builder.append("+++ ").append(fileName).append("\t")
                .append(format.format(currentTimeMillis)).append(' ')
                .append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * @param mutatedNode AstNode that represents mutation target
     * @param mutatingContent a new content that may include some comment
     *                        denoting here is auto-assigned code. Each element
     *                        of the list corresponds to an each line.
     * @return unified diff-formatted string that representing mutation
     */
    protected String generateUnifiedDiff(
            AstNode mutatedNode, List<String> mutatingContent) {
        return generateUnifiedDiffHeader()
                + generateUnifiedDiffBody(mutatedNode, mutatingContent);
    }
}
