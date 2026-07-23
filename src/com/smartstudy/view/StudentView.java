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
        setStyle("-fx-background-color: #f4f6f8;");

        setTop(buildHeader(onLogout));
        setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(buildAccessPanel());
        setCenter(contentArea);
    }

    private Node buildHeader(Runnable onLogout) {
        Label title = new Label("SmartStudy - Area Studente");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button logoutButton = new Button("Esci");
        logoutButton.setOnAction(e -> onLogout.run());

        HBox header = new HBox(title, spacer(), logoutButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2d6cdf;");
        return header;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private Node buildSidebar() {
        Button accessBtn = navButton("Accesso Biblioteca", this::buildAccessPanel);
        Button scanBtn = navButton("Prenota Posto", this::buildScanPanel);
        Button reservationBtn = navButton("Prenotazione Attiva", this::buildActiveReservationPanel);
        Button leaveBtn = navButton("Pausa Temporanea", this::buildTemporaryLeavePanel);
        Button reportBtn = navButton("Segnalazioni", this::buildReportsPanel);
        Button historyBtn = navButton("Storico Prenotazioni", this::buildHistoryPanel);

        VBox sidebar = new VBox(8, accessBtn, scanBtn, reservationBtn, leaveBtn, reportBtn, historyBtn);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setPrefWidth(210);
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");
        for (Node node : sidebar.getChildren()) {
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }
        return sidebar;
    }

    private Button navButton(String text, Supplier<Node> panelSupplier) {
        Button button = new Button(text);
        button.setOnAction(e -> showPanel(panelSupplier.get()));
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    private void showPanel(Node panel) {
        contentArea.getChildren().setAll(panel);
    }

    // --- Pannelli ---

    private Node buildAccessPanel() {
        return new LibraryAccessView();
    }

    private Node buildScanPanel() {
        VBox box = panelContainer("Prenota un posto");

        TextField qrField = new TextField();
        qrField.setPromptText("Codice QR del posto");
        qrField.setMaxWidth(300);

        Button scanButton = new Button("Cerca posto");

        GridPane seatInfo = new GridPane();
        seatInfo.setHgap(10);
        seatInfo.setVgap(8);
        seatInfo.setPadding(new Insets(15, 0, 0, 0));
        seatInfo.addRow(0, new Label("Tipo:"), new Label("-"));
        seatInfo.addRow(1, new Label("Area studio:"), new Label("-"));
        seatInfo.addRow(2, new Label("Stato:"), new Label("-"));

        Button reserveButton = new Button("Prenota questo posto");
        reserveButton.setStyle("-fx-background-color: #2d6cdf; -fx-text-fill: white;");

        box.getChildren().addAll(qrField, scanButton, seatInfo, reserveButton);
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

        HBox actions = new HBox(10,
                new Button("Richiedi pausa"),
                new Button("Segnala abbandono"),
                new Button("Termina prenotazione")
        );

        box.getChildren().addAll(info, actions);
        return box;
    }

    private Node buildTemporaryLeavePanel() {
        VBox box = panelContainer("Pause temporanee");

        TableView<TemporaryLeave> table = new TableView<>();
        TableColumn<TemporaryLeave, String> startCol = new TableColumn<>("Inizio");
        startCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStartTime())));
        TableColumn<TemporaryLeave, String> endCol = new TableColumn<>("Fine prevista");
        endCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getExpectedEndTime())));
        table.getColumns().addAll(startCol, endCol);
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessuna pausa registrata"));
        table.setPrefHeight(250);

        Button startLeaveButton = new Button("Inizia una pausa");
        startLeaveButton.setStyle("-fx-background-color: #2d6cdf; -fx-text-fill: white;");

        box.getChildren().addAll(table, startLeaveButton);
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
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox box = new VBox(15, heading);
        box.setPadding(new Insets(10));
        return box;
    }
}
