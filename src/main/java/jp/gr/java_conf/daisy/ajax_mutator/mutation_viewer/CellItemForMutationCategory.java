package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;

import java.util.List;

public class CellItemForMutationCategory implements CellItem {
    private final String categoryName;
    private List<MutationFileInformation> info;

    public CellItemForMutationCategory(String categoryName, List<MutationFileInformation> info) {
        this.categoryName = categoryName;
        this.info = info;
    }

    @Override
    public String getDisplayName() {
        return categoryName + " (" + info.size() + ")";
    }
}
