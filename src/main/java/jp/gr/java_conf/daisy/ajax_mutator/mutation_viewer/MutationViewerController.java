package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationListManager;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MutationViewerController implements Initializable {
    @FXML
    private ToggleButton toggleButtonAll;
    @FXML
    private ToggleButton toggleButtonUnkilled;
    @FXML
    private TreeView mutationTreeView;
    @FXML
    private Label mutationDetail;

    private final String pathToMutantsDirectory;
    // JavaFX doesn't provide filter for TreeView, we need to modify the data structure directory.
    // list below is for storing original data removed during filter-like operation.
    private List<DeletionUnit> deletions = new ArrayList<DeletionUnit>();

    public MutationViewerController(String pathToMutantsDirectory) {
        this.pathToMutantsDirectory = pathToMutantsDirectory;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initToggleButtons();

        MutationListManager mutationListManager = new MutationListManager(pathToMutantsDirectory);
        mutationListManager.readExistingMutationListFile();
        TreeItem<CellItem> root = new TreeItem<CellItem>();
        root.setExpanded(true);
        for (Map.Entry<String, List<MutationFileInformation>> entry: mutationListManager.getMutationFileInformationList().entrySet()) {
            if (entry.getValue().size() == 0) {
                continue;
            }

            TreeItem<CellItem> category = new TreeItem<CellItem>(
                    new CellItemForMutationCategory(entry.getKey(), entry.getValue()));
            root.getChildren().add(category);
            for (MutationFileInformation info: entry.getValue()) {
                category.getChildren().add(new TreeItem<CellItem>(new CellItemForMutant(info)));
            }
            category.setExpanded(true);
        }
        mutationTreeView.setShowRoot(false);
        mutationTreeView.setRoot(root);
        mutationTreeView.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public MutationListCell call(TreeView treeView) {
                return new MutationListCell();
            }
        });
        mutationTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mutationTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<CellItem>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<CellItem>> observableValue,
                                TreeItem<CellItem> oldValue, TreeItem<CellItem> newValue) {
                if (newValue == null || newValue.getValue() == null) {
                    mutationDetail.setText("");
                    return;
                }
                if (newValue.getValue() instanceof CellItemForMutationCategory) {
                    mutationDetail.setText(newValue.getValue().getDisplayName());
                    return;
                }
                mutationDetail.setText(((CellItemForMutant) newValue.getValue()).getContent());
            }
        });
    }

    private void initToggleButtons() {
        ToggleGroup group = new ToggleGroup();
        toggleButtonAll.setToggleGroup(group);
        toggleButtonUnkilled.setToggleGroup(group);
        toggleButtonAll.setSelected(true);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
                // Making sure one button is always selected.
                if (newToggle == null) {
                    oldToggle.setSelected(true);
                }

                if (toggleButtonAll.isSelected()) {
                    restoreLastDeletion();
                } else if (toggleButtonUnkilled.isSelected()) {
                    applyFilterByDeletion();
                }
            }
        });
    }

    synchronized private void restoreLastDeletion() {
        TreeItem root = mutationTreeView.getRoot();
        mutationTreeView.setRoot(null);
        for (int i = deletions.size() - 1; i >= 0; i--) {
            DeletionUnit deletion = deletions.get(i);
            deletion.deletedFrom.getChildren().add(deletion.wasIndex, deletion.deleted);
        }
        deletions.clear();
        mutationTreeView.setRoot(root);
    }

    synchronized private void applyFilterByDeletion() {
        TreeItem root = mutationTreeView.getRoot();
        List<TreeItem> categories = root.getChildren();
        Iterator<TreeItem> categoriesItr = categories.iterator();
        int indexOfCategory = 0;
        while (categoriesItr.hasNext()) {
            TreeItem category = categoriesItr.next();
            Iterator<TreeItem> itemItr = category.getChildren().iterator();
            int indexOfItem = 0;
            while (itemItr.hasNext()){
                TreeItem item = itemItr.next();
                if (item.getValue() instanceof CellItemForMutant &&
                        ((CellItemForMutant) item.getValue()).getState() != MutationFileInformation.State.NON_EQUIVALENT_LIVE) {
                    deletions.add(new DeletionUnit(item, category, indexOfItem));
                    itemItr.remove();
                } else {
                    indexOfItem++;
                }
            }
            if (category.getChildren().size() == 0) {
                deletions.add(new DeletionUnit(category, root, indexOfCategory));
                categoriesItr.remove();
            } else {
                indexOfCategory++;
            }
        }
    }

    private class MutationListCell extends TreeCell<CellItem> {
        @Override
        protected void updateItem(CellItem cellItem, boolean isEmpty) {
            super.updateItem(cellItem, isEmpty);
            if (cellItem instanceof CellItemForMutationCategory) {
                MutationCategoryCellController controller
                        = new MutationCategoryCellController((CellItemForMutationCategory) cellItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mutation_viewer/cell_category.fxml"));
                loader.setController(controller);
                try {
                    loader.load();
                } catch (IOException e) {

                }
                setGraphic(controller.getContainer());
            } else if (cellItem instanceof CellItemForMutant) {
                MutantCellController controller
                        = new MutantCellController((CellItemForMutant) cellItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mutation_viewer/cell_mutant.fxml"));
                loader.setController(controller);
                try {
                    loader.load();
                } catch (IOException e) {

                }
                setGraphic(controller.getContainer());
            }
        }
    }

    private class DeletionUnit {
        private final TreeItem deleted;
        private final TreeItem deletedFrom;
        private final int wasIndex;

        private DeletionUnit(TreeItem deleted, TreeItem deletedFrom, int wasIndex) {
            this.deleted = deleted;
            this.deletedFrom = deletedFrom;
            this.wasIndex = wasIndex;
        }
    }
}
