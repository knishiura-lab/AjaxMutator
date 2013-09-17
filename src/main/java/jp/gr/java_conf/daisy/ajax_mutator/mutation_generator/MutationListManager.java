package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kazuki Nishiura
 */
public class MutationListManager {
    private static final String MUTATION_LIST_FILE_NAME = "mutation_list.csv";

    private List<String> mutationTitles = new ArrayList<String>();
    private Map<String, List<MutationFileInformation>> mutationFiles
            = new HashMap<String, List<MutationFileInformation>>();
    private String reportOutputDir;

    public MutationListManager(String reportOutputDir) {
        this.reportOutputDir = reportOutputDir;
    }

    public void addMutationFileInformation(
            String title, MutationFileInformation fileInformation) {
        if (!mutationFiles.containsKey(title)) {
            mutationTitles.add(title);
            mutationFiles.put(title, new ArrayList<MutationFileInformation>());
        }
        mutationFiles.get(title).add(fileInformation);
    }

    /**
     * @return Map whose key is description of the class of mutation, and whose
     * value is a list of files. Each file corresponds to one mutation in that
     * class.
     */
    public Map<String, List<MutationFileInformation>> getMutationFileInformationList() {
        return mutationFiles;
    }

    public int getNumberOfUnkilledMutants() {
        int total = 0;
        for (List<MutationFileInformation> fileInfoList: mutationFiles.values()) {
            for (MutationFileInformation fileInfo: fileInfoList) {
                if (!fileInfo.isKilled()) {
                    total++;
                }
            }
        }
        return total;
    }

    private String generateContentsOfMutationReport() {
        StringBuilder builder = new StringBuilder();
        for (String title: mutationTitles) {
            builder.append(title).append(',')
                    .append(mutationFiles.get(title).size())
                    .append(System.lineSeparator());
            for (MutationFileInformation info: mutationFiles.get(title)) {
                builder.append(info.getFileName()).append(',')
                        .append(info.getKilledStatusAsString()).append(',')
                        .append(info.getAbsolutePath())
                        .append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public String getGeneratedFileName() {
        return MUTATION_LIST_FILE_NAME;
    }

    public boolean generateMutationListFile() {
        return Util.writeToFile(
                reportOutputDir + File.separator + MUTATION_LIST_FILE_NAME,
                generateContentsOfMutationReport()
        );
    }
}
