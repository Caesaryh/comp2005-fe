package com.caesaryh.comp2005fe;

import com.caesaryh.comp2005fe.utils.MaternityApiClient;
import com.caesaryh.comp2005fe.utils.models.Patient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Dashboard extends Application {
    private static final String CSS_STYLE = """
            .root-pane {
                -fx-background-color: #f5f5f5;
            }
            .card {
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
                -fx-padding: 20;
            }
            .title {
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #2c3e50;
            }
            .error-label {
                -fx-text-fill: #e74c3c;
                -fx-font-weight: bold;
            }
            .patient-item {
                -fx-padding: 10 0;
                -fx-border-color: #ecf0f1;
                -fx-border-width: 0 0 1 0;
            }
        """;

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createPatientTab("Patients not discharged", MaternityApiClient.getNeverDischargedPatients()),
                createPatientTab("Readmission within 7", MaternityApiClient.getReadmittedWithin7Days()),
                createPatientTab("Maximum number of employee patients", MaternityApiClient.getMultiStaffPatients()),
                createBusyMonthTab()
        );

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(createHeader(), tabPane);
        mainLayout.getStyleClass().add("root-pane");

        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.getStylesheets().add("data:text/css," + CSS_STYLE);

        primaryStage.setTitle("Hospital data analysis");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        Label title = new Label("Hospital data analysis");
        title.getStyleClass().add("title");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Tab createPatientTab(String title, CompletableFuture<List<Patient>> future) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label loadingLabel = new Label("Loading...");
        ProgressIndicator progress = new ProgressIndicator();
        VBox loadingBox = new VBox(10, progress, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);

        ListView<Patient> listView = new ListView<>();
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getForename() + " " + item.getSurname() + " (NHS: " + item.getNhsNumber() + ")");
                    getStyleClass().add("patient-item");
                }
            }
        });

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        content.getChildren().addAll(loadingBox, listView, errorLabel);
        VBox card = wrapInCard(content);

        future.thenAcceptAsync(patients -> {
            Platform.runLater(() -> {
                loadingBox.setVisible(false);
                listView.getItems().setAll(patients);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingBox.setVisible(false);
                errorLabel.setText("Error: " + ex.getCause().getMessage());
                errorLabel.setVisible(true);
            });
            return null;
        });

        return createTab(title, card);
    }

    private Tab createBusyMonthTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        ProgressIndicator progress = new ProgressIndicator();
        Label loadingLabel = new Label("Loading...");
        VBox loadingBox = new VBox(10, progress, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);

        Label monthLabel = new Label();
        monthLabel.getStyleClass().add("title");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        content.getChildren().addAll(loadingBox, monthLabel, errorLabel);
        VBox card = wrapInCard(content);

        MaternityApiClient.getBusiestMonth().thenAcceptAsync(month -> {
            Platform.runLater(() -> {
                loadingBox.setVisible(false);
                monthLabel.setText("Busiest Month: " + month);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingBox.setVisible(false);
                errorLabel.setText("Error: " + ex.getCause().getMessage());
                errorLabel.setVisible(true);
            });
            return null;
        });

        return createTab("Busiest Month", card);
    }

    private VBox wrapInCard(VBox content) {
        VBox card = new VBox(content);
        card.getStyleClass().add("card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private Tab createTab(String title, VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        Tab tab = new Tab(title);
        tab.setContent(scrollPane);
        tab.setClosable(false);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}