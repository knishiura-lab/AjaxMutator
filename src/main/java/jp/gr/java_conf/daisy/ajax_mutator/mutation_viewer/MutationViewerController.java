package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import com.google.common.io.Files;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationListManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
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
        Map<String, List<MutationFileInformation>> mutationFileInformationAsMap
                = mutationListManager.getMutationFileInformationList();
        List<MutationFileInformation> mutationFileInformation = new ArrayList<MutationFileInformation>();
        for (List<MutationFileInformation> info: mutationFileInformationAsMap.values()) {
            mutationFileInformation.addAll(info);
        }
        ObservableList<MutationFileInformation> items
                = FXCollections.observableArrayList(mutationFileInformation);
        mutationList.setItems(items);
        mutationList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<MutationFileInformation>() {
                    public void changed(ObservableValue<? extends MutationFileInformation> observableValue,
                                        MutationFileInformation oldValue, MutationFileInformation newValue) {
                        String content;
                        try {
                            content = Files.toString(new File(newValue.getAbsolutePath()), Charset.defaultCharset());
                        } catch (IOException e) {
                            content = "Failed to load " + observableValue.getValue();
                        }
                        mutationDetail.setText(content);
                    }
                });
        mutationList.getSelectionModel().selectFirst();
    }
}
