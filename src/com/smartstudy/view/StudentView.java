package com.smartstudy.view;

import DTO.AbandonmentReportDTO;
import DTO.StudentSession;
import com.smartstudy.controller.ControllerResult;
import com.smartstudy.controller.StudentDashboardController;
import com.smartstudy.domainModel.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

public class StudentView extends BorderPane {

    private final StackPane contentArea;
    private final Label resultLabel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Button reservationBtn;

    private final StudentDashboardController dashboardController;

    private final StudentSession userSession;

    public StudentView(Runnable onLogout, StudentDashboardController dashboardController, StudentSession userSession) {
        this.dashboardController = dashboardController;
        this.userSession = userSession;
        resultLabel = new Label();
        resultLabel.setVisible(false);
        resultLabel.setStyle("-fx-font-weight: bold");
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
        logoutButton.setOnAction(e ->
            onLogout.run()
        );

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
        reservationBtn = navButton("Prenotazione Attiva", this::buildActiveReservationPanel);
        Button reportBtn = navButton("Segnalazioni", this::buildReportsPanel);
        Button historyBtn = navButton("Storico Prenotazioni", this::buildHistoryPanel);

        if(!userSession.isPresent()){
            disableWithReason(scanBtn, "Lo studente non è in biblioteca");
            disableWithReason(reservationBtn, "Lo studente non è in biblioteca");
        }

        updateSidebar();

        VBox sidebar = new VBox(6, homeBtn, scanBtn, reservationBtn, reportBtn, historyBtn);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);
        for (Node node : sidebar.getChildren()) {
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }
        return sidebar;
    }

    private void updateSidebar() {
        if(!userSession.isPresent() || userSession.getReservation() == null){
            disableWithReason(reservationBtn, "l'utente non ha una prenotazione attiva o non è in biblioteca");
        }else{
            reservationBtn.setDisable(false);
        }

    }

    private void disableWithReason(Button button, String reason) {
        button.setDisable(true);
        Tooltip.install(button, new Tooltip(reason));
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
        VBox box = panelContainer("Benvenuto, " + userSession.getUser().getName() + " " + userSession.getUser().getSurname());
        Label hint = new Label("Da qui puoi prenotare un posto, gestire la tua prenotazione attiva, le segnalazioni e lo storico.");
        hint.setWrapText(true);
        hint.getStyleClass().add("hint-label");
        Label presenceLabel = new Label(userSession.isPresent()? "Lo studente è in biblioteca": "Lo studente non è in bibloteca, alcune funzioni sono limitate");
        presenceLabel.getStyleClass().add("status-badge");
        presenceLabel.getStyleClass().add(userSession.isPresent()?"present":"absent");
        box.getChildren().addAll(hint, presenceLabel);
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
        Label idSeat = new Label("-");
        Label seatType = new Label("-");
        Label seatArea = new Label("-");
        Label seatStatus = new Label("-");
        seatInfo.addRow(0, new Label("Id:"), idSeat);
        seatInfo.addRow(1, new Label("Tipo:"), seatType);
        seatInfo.addRow(2, new Label("Area studio:"), seatArea);
        seatInfo.addRow(3, new Label("Stato:"), seatStatus);

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

        hideResult();

        scanButton.setOnAction(e -> {
                ControllerResult<Seat> res = dashboardController.scanSeat(userSession.getUser(), qrField.getText());
                if(res.isSuccess()){
                    idSeat.setText(String.valueOf(res.getResult().getId()));
                    seatType.setText(String.valueOf(res.getResult().getType()));
                    seatArea.setText(res.getResult().getStudyArea().getName());
                    seatStatus.setText(String.valueOf(res.getResult().getStatus()));
                    hideResult();
                }else
                    showResult(res.getMessage(), false);
        });

        reserveButton.setOnAction(e ->{
            try{
                long seatId = Long.parseLong(idSeat.getText());
                ControllerResult<Reservation> res = dashboardController.reserveSeat(userSession.getUser(), seatId);
                if(res.isSuccess()){
                    userSession.setReservation(res.getResult());
                    updateSidebar();
                    hideResult();
                    showPanel(buildActiveReservationPanel());
                }else{
                    showResult(res.getMessage(), false);
                }
            }catch (IllegalArgumentException exception){
                showResult("Inserire un seat valido", false);
            }
        });
        reportButton.setOnAction(e -> {
            try{
                long seatId = Long.parseLong(idSeat.getText());
                ControllerResult<Void> res = dashboardController.createAbandonmentReport(userSession.getUser(), reportField.getText(), seatId);
                showResult(res.getMessage(), res.isSuccess());
            }catch (NumberFormatException exception){
                showResult("Inserire un seat valido", false);
            }
        });

        box.getChildren().addAll(qrField, scanButton, seatInfo, reserveButton, reportBox, resultLabel);
        return box;
    }

    private Node buildActiveReservationPanel() {
        VBox box = panelContainer("Prenotazione attiva");

        GridPane info = new GridPane();
        info.setHgap(10);
        info.setVgap(8);
        Label seat;
        Label studyArea;
        Label startTime;
        Label reservationStatus;
        if(userSession.getReservation() == null){
            seat = new Label("-");
            studyArea = new Label("-");
            startTime = new Label("-");
            reservationStatus = new Label("-");
        }else{
            seat = new Label(userSession.getReservation().getSeat().getQrCode());
            studyArea = new Label(userSession.getReservation().getSeat().getStudyArea().getName());
            startTime = new Label(userSession.getReservation().getStartTime().format(formatter));
            reservationStatus = new Label(userSession.getReservation().getStatus().name());
        }

        info.addRow(0, new Label("Posto:"), seat);
        info.addRow(1, new Label("Area studio:"), studyArea);
        info.addRow(2, new Label("Inizio:"), startTime);
        info.addRow(3, new Label("Stato:"), reservationStatus);

        Button endButton = new Button("Termina prenotazione");
        endButton.getStyleClass().add("btn-danger");

        hideResult();

        endButton.setOnAction(e -> {
            ControllerResult<Void> res = dashboardController.closeReservation(userSession.getUser(), userSession.getReservation());
            if(res.isSuccess()) {
               userSession.setReservation(null);
               updateSidebar();
               showPanel(buildScanPanel());
            }else{
                showResult(res.getMessage(), false);
            }
        });

        HBox actions = new HBox(10, endButton);

        Label pausesHeading = new Label("Pause");
        pausesHeading.getStyleClass().add("section-subheading");
        TableView<TemporaryLeave> pausesTable = new TableView<>();
        TableColumn<TemporaryLeave, String> startCol = new TableColumn<>("Inizio");
        startCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime().format(formatter)));
        TableColumn<TemporaryLeave, String> endCol = new TableColumn<>("Fine prevista");
        endCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getExpectedEndTime().format(formatter)));
        pausesTable.getColumns().addAll(startCol, endCol);
        pausesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        pausesTable.setItems(FXCollections.observableArrayList());
        pausesTable.setPlaceholder(new Label("Nessuna pausa registrata"));
        pausesTable.setPrefHeight(180);
        updateLeavesTable(pausesTable);
        Button startLeaveButton = new Button("Inizia una pausa");
        startLeaveButton.getStyleClass().add("btn-primary");
        startLeaveButton.setOnAction(e -> {
            ControllerResult<Reservation> result = dashboardController.createLeave(userSession.getUser(), userSession.getReservation());
            if(result.isSuccess()) {
                userSession.setReservation(result.getResult());
                updateLeavesTable(pausesTable);
                hideResult();
            }else {
                showResult(result.getMessage(), false);
            }
        });
        box.getChildren().addAll(info, actions, pausesHeading, pausesTable, startLeaveButton, resultLabel);
        return box;
    }

    private void updateLeavesTable(TableView<TemporaryLeave> pausesTable) {
        if(userSession.getReservation() != null) {
            pausesTable.setItems(FXCollections.observableArrayList(userSession.getReservation().getTemporaryLeaves()));
        }
    }

    private Node buildReportsPanel() {
        VBox box = panelContainer("Le mie segnalazioni");

        TableView<AbandonmentReportDTO> table = new TableView<>();
        TableColumn<AbandonmentReportDTO, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReport().getCreatedAt())));
        TableColumn<AbandonmentReportDTO, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getDescription()));
        TableColumn<AbandonmentReportDTO, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReport().getStatus())));
        TableColumn<AbandonmentReportDTO, String> libraryCol = new TableColumn<>("Libreria");
        libraryCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getLibraryName())));
        table.getColumns().addAll(dateCol, descCol, statusCol, libraryCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        hideResult();
        ControllerResult<List<AbandonmentReportDTO>> items = dashboardController.getAllReports(userSession.getUser());
        if(items.isSuccess()) {
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            hideResult();
        }else {
            showResult(items.getMessage(), false);
        }
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
        startCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime().format(formatter)));
        TableColumn<Reservation, String> endCol = new TableColumn<>("Fine");
        endCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndTime().format(formatter)));
        TableColumn<Reservation, String> libraryCol = new TableColumn<>("Fine");
        libraryCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getSeat().getStudyArea().getLibrary().getName())));
        TableColumn<Reservation, String> numLeavesCol = new TableColumn<>("Numero pause");
        numLeavesCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTemporaryLeaves().size())));
        table.getColumns().addAll(seatCol, startCol, endCol, libraryCol, numLeavesCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        hideResult();
        ControllerResult<List<Reservation>> items = dashboardController.getAllReservations(userSession.getUser());
        if(items.isSuccess()) {
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            hideResult();
        }else{
            showResult(items.getMessage(), false);
        }
        table.setPlaceholder(new Label("Nessuna prenotazione passata"));
        table.setPrefHeight(300);

        box.getChildren().addAll(table, resultLabel);
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

    private void showResult(String message, boolean success) {
        resultLabel.setText(message);
        resultLabel.getStyleClass().removeAll("error-label", "hint-label");
        resultLabel.getStyleClass().add(success ? "hint-label" : "error-label");
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }

    private void hideResult() {
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
    }
}


