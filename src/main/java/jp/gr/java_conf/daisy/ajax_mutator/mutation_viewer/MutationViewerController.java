package jp.gr.java_conf.daisy.ajax_mutator.mutation_viewer;

import com.google.common.base.Joiner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationListManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MutationViewerController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MutationViewerController.class);
    private final String pathToBaseDir;
    @FXML
    private ToggleButton toggleButtonAll;
    @FXML
    private ToggleButton toggleButtonUnkilled;
    @FXML
    private Button saveButton;
    @FXML
    private TreeView mutationTreeView;
    @FXML
    private Label fileInfo;
    @FXML
    private WebView mutationDetail;
    @FXML
    private ScrollPane mutationDetailScrollPane;
    @FXML
    private AnchorPane mutationDetailAnchorPane;
    private UnifiedDiffParser.Mutation mutation;
    private List<String> originalFileContents;

    // JavaFX doesn't provide filter for TreeView, we need to modify the data structure directory.
    // list below is for storing original data removed during filter-like operation.
    private final MutationListManager mutationListManager;
    private List<DeletionUnit> deletions = new ArrayList<DeletionUnit>();

    public MutationViewerController(String pathToMutantsDirectory, String pathToBaseDirectory) {
        mutationListManager = new MutationListManager(pathToMutantsDirectory);
        mutationListManager.readExistingMutationListFile();
        this.pathToBaseDir = pathToBaseDirectory;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initToggleButtons();

        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mutationListManager.generateMutationListFile();
            }
        });

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
        setupDetailWebView();
        mutationTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<CellItem>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<CellItem>> observableValue,
                                TreeItem<CellItem> oldValue, TreeItem<CellItem> newValue) {
                if (newValue == null || newValue.getValue() == null) {
                    mutationDetail.getEngine().load("about:blank");
                    return;
                }
                if (newValue.getValue() instanceof CellItemForMutationCategory) {
                    fileInfo.setText(newValue.getValue().getDisplayName());
                    mutationDetail.getEngine().load("about:blank");
                    return;
                }
                CellItemForMutant item = (CellItemForMutant) newValue.getValue();
                String content = item.getContent();
                UnifiedDiffParser parser = new UnifiedDiffParser();
                mutation = parser.parse(Arrays.asList(content.split(System.lineSeparator())));
                fileInfo.setText(getFileInfo(mutation));
                originalFileContents = new ArrayList<String>();
                try {
                    File originalFile = new File(
                            pathToBaseDir + File.separator + mutation.getFileName());
                    Scanner scanner = new Scanner(new FileInputStream(originalFile));
                    while (scanner.hasNext()) {
                        originalFileContents.add(scanner.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    LOGGER.error("Fail to open file " + mutation.getFileName() + " under " + pathToBaseDir);
                }
                mutationDetail.getEngine().load(getClass().getResource(
                        "/mutation_viewer/mutation_detail_template.html").toExternalForm() + "#originalContentHeader");
            }
        });

        mutationDetailScrollPane.viewportBoundsProperty().addListener(
                (ChangeListener<? super Bounds>) new ChangeListener<Bounds>() {
                    @Override public void changed(ObservableValue<? extends Bounds> observableValue,
                                                  Bounds oldBounds, Bounds newBounds) {
                        mutationDetailAnchorPane.setPrefSize(newBounds.getWidth(), newBounds.getHeight());
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

    private String getFileInfo(UnifiedDiffParser.Mutation mutation) {
        StringBuilder builder = new StringBuilder();
        builder.append(mutation.getFileName()).append("    line").append(mutation.getLines()).append(" ");
        if (mutation.getOriginalLines().size() == 1) {
            builder.append("is");
        } else {
            builder.append("are");
        }
        return builder.append(" mutated").toString();
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

    private void setupDetailWebView() {
        final WebEngine engine = mutationDetail.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State newState) {
                LOGGER.info("state changed" +  newState + engine.getLocation());
                if (newState == Worker.State.SUCCEEDED ) {
                    List<String> contentsBeforeMutation = originalFileContents.subList(
                            0,
                            mutation.getStartLine() - 1);
                    List<String> contentsAfterMutation = originalFileContents.subList(
                            mutation.getStartLine() + mutation.getOriginalLines().size() - 1,
                            originalFileContents.size());
                    engine.executeScript(
                            "var myTask = setInterval(function() {"
                            + "if (document.readyState !== 'complete') return;"
                            + "clearInterval(myTask);       "
                            + "new MutationDetailViewer()"
                            + ".setFileName(\"" + mutation.getFileName() + "\")"
                            + ".setLines(\"" + mutation.getLines() + "\")"
                            + ".setOriginal(\"" + escapeAndJoin(mutation.getOriginalLines())+ "\")"
                            + ".setMutated(\"" + escapeAndJoin(mutation.getMutatedLines()) + "\")"
                            + ".setContentBeforeMutation(\"" + escapeAndJoin(contentsBeforeMutation) + "\")"
                            + ".setContentAfterMutation(\"" + escapeAndJoin(contentsAfterMutation) + "\");"
                            + "document.body.style.visibility = 'visible';"
                            + "}, 100);");
                }
            }
        });
    }

    private String escapeAndJoin(List<String> jsSentences) {
        return StringEscapeUtils.escapeEcmaScript(Joiner.on("<br>").join(jsSentences));
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
