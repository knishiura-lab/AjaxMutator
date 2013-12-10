package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MutationCategoryCellController implements Initializable {
    @FXML
    private HBox container;
    @FXML
    private Label categoryName;
    @FXML
    private Label numOfMutants;
    private final CellItemForMutationCategory categoryCell;

    public MutationCategoryCellController(CellItemForMutationCategory categoryCell) {
        this.categoryCell = categoryCell;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryName.setText(categoryCell.getDisplayName());
    }

    public HBox getContainer() {
        return container;
    }
}
