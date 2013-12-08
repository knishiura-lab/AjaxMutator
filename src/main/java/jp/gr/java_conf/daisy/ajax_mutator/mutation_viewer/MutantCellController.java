package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MutantCellController implements Initializable {
    @FXML
    private HBox container;
    @FXML
    private Label mutantName;
    @FXML
    private Label status;
    private final CellItemForMutant mutant;

    public MutantCellController(CellItemForMutant mutant) {
        this.mutant = mutant;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mutantName.setText(mutant.getDisplayName());
    }

    public HBox getContainer() {
        return container;
    }
}
