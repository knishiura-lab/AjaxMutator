package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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
    private CheckBox equivalentCheckBox;
    @FXML
    private Label isKilledText;

    private final CellItemForMutant mutant;

    public MutantCellController(CellItemForMutant mutant) {
        this.mutant = mutant;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mutantName.setText(mutant.getDisplayName());
        switch (mutant.getState()) {
            case EQUIVALENT:
                equivalentCheckBox.setSelected(true);
                equivalentCheckBox.setDisable(true);
                isKilledText.setVisible(false);
                break;
            case KILLED:
                equivalentCheckBox.setVisible(false);
                isKilledText.setText("killed");
                isKilledText.getStyleClass().add("killed");
                break;
            case NON_EQUIVALENT_LIVE:
                isKilledText.setText("unkilled");
                isKilledText.getStyleClass().add("unkilled");
                break;
        }
        equivalentCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                // If mutant is equivalent, we don't care if it's killed or not.
                isKilledText.setVisible(!newValue);
            }
        });
    }

    public HBox getContainer() {
        return container;
    }
}
