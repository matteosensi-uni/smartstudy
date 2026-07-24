package com.smartstudy.view;

import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.TemporaryLeave;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Supplier;

public class StudentView extends BorderPane {

    private final StackPane contentArea;

    public StudentView(Runnable onLogout) {
        getStyleClass().add("root");

        setTop(buildHeader(onLogout));
        setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setAlignment(Pos.TOP_LEFT);
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(buildHomePanel());
        setCenter(contentArea);
    }

    private Node buildHeader(Runnable onLogout) {
        Label title = new Label("SmartStudy - Area Studente");
        title.getStyleClass().add("title-label");

        Button logoutButton = new Button("Esci");
        logoutButton.getStyleClass().add("btn-secondary");
        logoutButton.setOnAction(e -> onLogout.run());

        HBox header = new HBox(12, title, spacer(), logoutButton);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private Node buildSidebar() {
        Button homeBtn = navButton("Home", this::buildHomePanel);
        Button scanBtn = navButton("Prenota Posto", this::buildScanPanel);
        Button reservationBtn = navButton("Prenotazione Attiva", this::buildActiveReservationPanel);
        Button reportBtn = navButton("Segnalazioni", this::buildReportsPanel);
        Button historyBtn = navButton("Storico Prenotazioni", this::buildHistoryPanel);

        VBox sidebar = new VBox(6, homeBtn, scanBtn, reservationBtn, reportBtn, historyBtn);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);
        for (Node node : sidebar.getChildren()) {
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }
        return sidebar;
    }

    private Button navButton(String text, Supplier<Node> panelSupplier) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setOnAction(e -> showPanel(panelSupplier.get()));
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    private void showPanel(Node panel) {
        contentArea.getChildren().setAll(panel);
    }

    // --- Pannelli ---

    private Node buildHomePanel() {
        VBox box = panelContainer("Benvenuto");
        Label hint = new Label("Da qui puoi prenotare un posto, gestire la tua prenotazione attiva, le segnalazioni e lo storico.");
        hint.setWrapText(true);
        hint.getStyleClass().add("hint-label");
        box.getChildren().add(hint);
        return box;
    }

    private Node buildScanPanel() {
        VBox box = panelContainer("Prenota un posto");

        TextField qrField = new TextField();
        qrField.setPromptText("Codice QR del posto");
        qrField.setMaxWidth(300);
        qrField.getStyleClass().add("field");

        Button scanButton = new Button("Cerca posto");
        scanButton.getStyleClass().add("btn-secondary");

        GridPane seatInfo = new GridPane();
        seatInfo.setHgap(10);
        seatInfo.setVgap(8);
        seatInfo.setPadding(new Insets(15, 0, 0, 0));
        seatInfo.addRow(0, new Label("Tipo:"), new Label("-"));
        seatInfo.addRow(1, new Label("Area studio:"), new Label("-"));
        seatInfo.addRow(2, new Label("Stato:"), new Label("-"));

        Button reserveButton = new Button("Prenota questo posto");
        reserveButton.getStyleClass().add("btn-primary");
        Label reportLabel = new Label("Segnala il posto:");
        reportLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10");
        TextField reportField = new TextField();
        reportField.setPromptText("Descrizione");
        reportField.setStyle("-fx-padding: 10");
        reportField.setMinWidth(300);
        Button reportButton = new Button("Segnala Prenotazione");
        reportButton.getStyleClass().add("btn-danger");
        HBox reportBox = new HBox(10, reportLabel, reportField, reportButton);

        box.getChildren().addAll(qrField, scanButton, seatInfo, reserveButton, reportBox);
        return box;
    }

    private Node buildActiveReservationPanel() {
        VBox box = panelContainer("Prenotazione attiva");

        GridPane info = new GridPane();
        info.setHgap(10);
        info.setVgap(8);
        info.addRow(0, new Label("Posto:"), new Label("-"));
        info.addRow(1, new Label("Area studio:"), new Label("-"));
        info.addRow(2, new Label("Inizio:"), new Label("-"));
        info.addRow(3, new Label("Stato:"), new Label("-"));

        Button endButton = new Button("Termina prenotazione");
        endButton.getStyleClass().add("btn-danger");

        HBox actions = new HBox(10, endButton);

        Label pausesHeading = new Label("Pause");
        pausesHeading.getStyleClass().add("section-subheading");

        TableView<TemporaryLeave> pausesTable = new TableView<>();
        TableColumn<TemporaryLeave, String> startCol = new TableColumn<>("Inizio");
        startCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStartTime())));
        TableColumn<TemporaryLeave, String> endCol = new TableColumn<>("Fine prevista");
        endCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getExpectedEndTime())));
        pausesTable.getColumns().addAll(startCol, endCol);
        pausesTable.setItems(FXCollections.observableArrayList());
        pausesTable.setPlaceholder(new Label("Nessuna pausa registrata"));
        pausesTable.setPrefHeight(180);

        Button startLeaveButton = new Button("Inizia una pausa");
        startLeaveButton.getStyleClass().add("btn-primary");

        box.getChildren().addAll(info, actions, pausesHeading, pausesTable, startLeaveButton);
        return box;
    }

    private Node buildReportsPanel() {
        VBox box = panelContainer("Le mie segnalazioni");

        TableView<AbandonmentReport> table = new TableView<>();
        TableColumn<AbandonmentReport, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCreatedAt())));
        TableColumn<AbandonmentReport, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        TableColumn<AbandonmentReport, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStatus())));
        table.getColumns().addAll(dateCol, descCol, statusCol);
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessuna segnalazione aperta"));
        table.setPrefHeight(300);

        box.getChildren().add(table);
        return box;
    }

    private Node buildHistoryPanel() {
        VBox box = panelContainer("Storico prenotazioni");

        TableView<Reservation> table = new TableView<>();
        TableColumn<Reservation, String> seatCol = new TableColumn<>("Posto");
        seatCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSeat().getQrCode()));
        TableColumn<Reservation, String> startCol = new TableColumn<>("Inizio");
        startCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStartTime())));
        TableColumn<Reservation, String> endCol = new TableColumn<>("Fine");
        endCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getEndTime())));
        table.getColumns().addAll(seatCol, startCol, endCol);
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessuna prenotazione passata"));
        table.setPrefHeight(300);

        box.getChildren().add(table);
        return box;
    }

    private VBox panelContainer(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().add("card-heading");
        VBox box = new VBox(15, heading);
        box.getStyleClass().add("card");
        box.setMaxHeight(Region.USE_PREF_SIZE);
        return box;
    }
}
