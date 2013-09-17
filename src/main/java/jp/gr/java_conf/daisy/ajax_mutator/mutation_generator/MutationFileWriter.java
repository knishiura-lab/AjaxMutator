package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.ast.AstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Class that actually create a file that represents mutation.
 *
 * @author Kazuki Nishiura
 */
public class MutationFileWriter {
    private static final String DEFAULT_FILE_NAME_PREFIX = "mutant";
    private static final String DEFAULT_FOLDER_NAME = "mutants";
    private static final String EXTENSION = ".diff";
    private static final Logger LOGGER
            = LoggerFactory.getLogger(MutationFileWriter.class);

    private UnifiedDiffGenerator diffGenerator;
    private String fileNamePrefix = DEFAULT_FILE_NAME_PREFIX;
    private static int generatedFileID = 0;
    private String destinationDirectory;

    /**
     * @param file File that is used as an original file for mutation.
     */
    public MutationFileWriter(File file) {
        diffGenerator = new UnifiedDiffGenerator(file);
        destinationDirectory
                = file.getParent() + File.separator + DEFAULT_FOLDER_NAME;
    }

    /**
     * Create a file that represents mutation.
     * @param mutation see {@link Mutation}.
     * @return File object for the generated file if it was successfully
     * created, otherwise, null is returned.
     */

    public File writeToFile(Mutation mutation) {
        AstNode originalNode = mutation.getOriginalNode();
        String mutatingContent = mutation.getMutatingContent();
        String generatedFilePath = destinationDirectory + File.separator
                + fileNamePrefix + generatedFileID + EXTENSION;
        File file = createNewFile(generatedFilePath);
        if (file == null) {
            return null;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(diffGenerator.generateUnifiedDiff(
                    originalNode,
                    Arrays.asList(mutatingContent.split(System.lineSeparator()))
            ));
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Cannot write to " + generatedFilePath, e);
            return null;
        }

        generatedFileID++;
        return file;
    }

    /**
     * Try to create file with given path. If file with given path already
     * exists, just delete and create new one.
     */
    private File createNewFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            LOGGER.warn("File {} exists, override it.", path);
            file.delete();
        }
        try {
            FileUtils.forceMkdir(file.getParentFile());
            file.createNewFile();
        } catch (IOException e) {
            LOGGER.error("Failed to create file at {}", path);
            return null;
        }
        return file;
    }

    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
}
