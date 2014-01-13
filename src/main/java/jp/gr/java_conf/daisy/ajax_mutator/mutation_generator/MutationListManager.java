package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import java.io.File;
import java.util.*;

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
     * @return return of names that represents classes of mutations.
     */
    public List<String> getListOfMutationName() {
        return mutationTitles;
    }

    /**
     * @param name String that represents an class of mutation. See {@link #getListOfMutationName()}.
     * @return List of mutation file which belong to given class.
     */
    public List<MutationFileInformation> getMutationFileInformationList(String name) {
        return mutationFiles.get(name);
    }

    public int getNumberOfUnkilledMutants() {
        int total = 0;
        for (List<MutationFileInformation> fileInfoList: mutationFiles.values()) {
            for (MutationFileInformation fileInfo: fileInfoList) {
                if (fileInfo.getState() == MutationFileInformation.State.NON_EQUIVALENT_LIVE) {
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

    public boolean generateMutationListFile() {
        return Util.writeToFile(
                getMutationListFilePath(),
                generateContentsOfMutationReport()
        );
    }

    public void readExistingMutationListFile() {
        clear();
        List<String> lines = Util.readFromFile(getMutationListFilePath());
        String title = null;
        for (String line: lines) {
            String[] elms = line.split(",");
            if (elms.length == 2) {
                title = elms[0];
                mutationTitles.add(title);
                mutationFiles.put(title, new ArrayList<MutationFileInformation>());
                continue;
            }
            mutationFiles.get(title).add(new MutationFileInformation(
                    elms[0], elms[2], MutationFileInformation.State.fromString(elms[1])));
        }
    }

    private void clear() {
        mutationTitles.clear();
        mutationFiles.clear();
    }

    private String getMutationListFilePath() {
        return reportOutputDir + File.separator + MUTATION_LIST_FILE_NAME;
    }
}
