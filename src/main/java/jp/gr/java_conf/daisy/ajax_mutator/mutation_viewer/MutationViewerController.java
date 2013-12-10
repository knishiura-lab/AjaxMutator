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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    }

    private class MutationListCell extends TreeCell<CellItem> {
        @Override
        protected void updateItem(CellItem cellItem, boolean isEmpty) {
            super.updateItem(cellItem, isEmpty);
            if (cellItem instanceof CellItemForMutationCategory) {
                MutationCategoryCellController controller
                        = new MutationCategoryCellController((CellItemForMutationCategory) cellItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cell_category.fxml"));
                loader.setController(controller);
                try {
                    loader.load();
                } catch (IOException e) {

                }
                setGraphic(controller.getContainer());
            } else if (cellItem instanceof CellItemForMutant) {
                MutantCellController controller
                        = new MutantCellController((CellItemForMutant) cellItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cell_mutant.fxml"));
                loader.setController(controller);
                try {
                    loader.load();
                } catch (IOException e) {

                }
                setGraphic(controller.getContainer());
            }
        }
    }
}
