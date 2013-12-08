package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationListManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MutationViewerController implements Initializable {
    @FXML
    private ListView mutationList;
    @FXML
    private Label mutationDetail;

    private final String pathToMutantsDirectory;

    public MutationViewerController(String pathToMutantsDirectory) {
        this.pathToMutantsDirectory = pathToMutantsDirectory;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MutationListManager mutationListManager = new MutationListManager(pathToMutantsDirectory);
        mutationListManager.readExistingMutationListFile();
        List<CellItem> cellItems = new ArrayList<CellItem>();
        for (Map.Entry<String, List<MutationFileInformation>> entry: mutationListManager.getMutationFileInformationList().entrySet()) {
            if (entry.getValue().size() == 0) {
                continue;
            }
            cellItems.add(new CellItemForMutationCategory(entry.getKey(), entry.getValue()));
            for (MutationFileInformation info: entry.getValue()) {
                cellItems.add(new CellItemForMutant(info));
            }
        }
        mutationList.setItems(FXCollections.observableArrayList(cellItems));
        mutationList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<CellItem>() {
                    public void changed(ObservableValue<? extends CellItem> observableValue,
                                        CellItem oldValue, CellItem newValue) {
                        if (newValue instanceof CellItemForMutationCategory) {
                            mutationList.getSelectionModel().select(mutationList.getSelectionModel().getSelectedIndex());
                            return;
                        }

                        mutationDetail.setText(((CellItemForMutant) newValue).getContent());
                    }
                });
        mutationList.getSelectionModel().selectFirst();
    }
}
