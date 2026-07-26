package com.smartstudy.view;

import com.smartstudy.DTO.AbandonmentReportDTO;
import com.smartstudy.controller.AdminDashboardController;
import com.smartstudy.controller.ControllerResult;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.StudyArea;

import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.domainModel.enums.StudyAreaType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class AdminView extends BorderPane {

    private final StackPane contentArea;
    private final Label resultLabel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AdminDashboardController dashboardController;
    private final Admin user;


    public AdminView(Runnable onLogout, AdminDashboardController  dashboardController, Admin user) {
        this.dashboardController = dashboardController;
        this.user = user;
        resultLabel = new Label();
        resultLabel.setVisible(false);
        resultLabel.getStyleClass().add("btn-danger");
        getStyleClass().add("root");

        setTop(buildHeader(onLogout));
        setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setAlignment(Pos.TOP_LEFT);
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(buildPresencePanel());
        setCenter(contentArea);
    }

    private Node buildHeader(Runnable onLogout) {
        Label title = new Label("SmartStudy - Area Amministratore");
        title.getStyleClass().add("title-label");

        Button logoutButton = new Button("Esci");
        logoutButton.getStyleClass().add("btn-secondary");
        logoutButton.setOnAction(e -> onLogout.run());

        HBox header = new HBox(title, spacer(), logoutButton);
        header.getStyleClass().addAll("app-header", "admin");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private Node buildSidebar() {
        Button presenceBtn = navButton("Presenza", this::buildPresencePanel);
        Button seatsBtn = navButton("Posti", this::buildSeatsPanel);
        Button areasBtn = navButton("Aree Studio", this::buildStudyAreasPanel);
        Button openReportsBtn = navButton("Segnalazioni Aperte", this::buildOpenReportsPanel);
        Button inChargeReportsBtn = navButton("Segnalazioni in Carico",this::buildPendingReportsPanel);
        Button closedReportsBtn = navButton("Segnalazioni Chiuse", this::buildClosedReportsPanel);


        if(!user.isPresent()){
            disableWithReason(seatsBtn);
            disableWithReason(areasBtn);
            disableWithReason(openReportsBtn);
            disableWithReason(inChargeReportsBtn);
        }

        VBox sidebar = new VBox(6, presenceBtn, seatsBtn, areasBtn, openReportsBtn, inChargeReportsBtn, closedReportsBtn);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);
        for (Node node : sidebar.getChildren()) {
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }
        return sidebar;
    }

    private void disableWithReason(Button button) {
        button.setDisable(true);
        Tooltip.install(button, new Tooltip("L'admin non è in biblioteca"));
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

    private Node buildPresencePanel() {
        VBox box = panelContainer("Benvenuto "+ user.getName() + " " + user.getSurname());
        Label hint = new Label("Da qui puoi prenotare un posto, gestire la tua prenotazione attiva, le segnalazioni e lo storico.");
        hint.setWrapText(true);
        hint.getStyleClass().add("hint-label");
        Label presenceLabel = new Label(user.isPresent()? "L'admin è in biblioteca": "L'admin non è in bibloteca, alcune funzioni sono limitate");
        presenceLabel.getStyleClass().add("status-badge");
        presenceLabel.getStyleClass().add(user.isPresent()?"present":"absent");
        box.getChildren().addAll(hint, presenceLabel);
        return box;
    }

    private Node buildSeatsPanel() {
        VBox box = panelContainer("Gestione posti");

        TableView<Seat> table = new TableView<>();
        TableColumn<Seat, String> qrCol = new TableColumn<>("QR Code");
        qrCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQrCode()));
        TableColumn<Seat, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getType())));
        TableColumn<Seat, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStatus())));
        table.getColumns().addAll(qrCol, typeCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("Nessun posto da mostrare"));
        table.setPrefHeight(320);
        refreshSeatTable(table);
        Button availableButton = new Button("Segna disponibile");
        availableButton.getStyleClass().add("btn-secondary");

        Button brokenButton = new Button("Segna rotto");
        brokenButton.getStyleClass().add("btn-danger");
        HBox actions = new HBox(10, availableButton, brokenButton);

        Label typeLabel = new Label("Modifica il tipo di posto");
        typeLabel.setStyle("-fx-padding: 10; -fx-font-weight: bold");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(Arrays.stream(SeatType.values()).map(SeatType::name).toList());
        typeBox.setValue(typeBox.getItems().getFirst());
        Button saveTypeButton = new Button("Modifica");
        saveTypeButton.getStyleClass().add("btn-primary");
        typeBox.getStyleClass().add("btn-secondary");
        typeBox.setStyle("-fx-padding: 5");
        HBox changeTypeBox = new HBox(5, typeLabel, typeBox, saveTypeButton);

        availableButton.setOnAction(e -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Seleziona un posto valido");
            } else {
                ControllerResult<Void> res =dashboardController.updateSeatStatus(user, table.getSelectionModel().getSelectedItem(), SeatStatus.AVAILABLE.name());
                if(res.isSuccess()){
                    hideResult();
                    refreshSeatTable(table);
                }else {
                    showResult(res.getMessage());
                }
            }
        });
        brokenButton.setOnAction(e -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Seleziona un posto valido");
            }else {
                ControllerResult<Void> res = dashboardController.updateSeatStatus(user, table.getSelectionModel().getSelectedItem(), SeatStatus.BROKEN.name());
                if(res.isSuccess()) {
                    refreshSeatTable(table);
                    hideResult();
                }else {
                    showResult(res.getMessage());
                }
            }
        });
        saveTypeButton.setOnAction(e -> {
                if(table.getSelectionModel().getSelectedItem() == null){
                    showResult("Seleziona un posto valido");
                }else {
                    ControllerResult<Void> res = dashboardController.updateSeatType(user, table.getSelectionModel().getSelectedItem(), typeBox.getValue());
                    if(res.isSuccess()) {
                        refreshSeatTable(table);
                        hideResult();
                    }else {
                        showResult(res.getMessage());
                    }
                }
        });
        box.getChildren().addAll(table, actions, changeTypeBox, resultLabel);
        return box;
    }

    private void refreshSeatTable(TableView<Seat> table) {
        ControllerResult<List<Seat>> items = dashboardController.getAllSeats(user);
        if(items.isSuccess()){
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            table.getSelectionModel().clearSelection();
            hideResult();
        }else{
            showResult(items.getMessage());
        }
    }

    private Node buildStudyAreasPanel() {
        VBox box = panelContainer("Gestione aree studio");

        TableView<StudyArea> table = new TableView<>();
        TableColumn<StudyArea, String> nameCol = new TableColumn<>("Nome");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        TableColumn<StudyArea, String> floorCol = new TableColumn<>("Piano");
        floorCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getFloor())));
        TableColumn<StudyArea, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getType())));
        TableColumn<StudyArea, String> policyCol = new TableColumn<>("Regola pause");
        policyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimePolicy().getName()));
        table.getColumns().addAll(nameCol, floorCol, typeCol, policyCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        refreshStudyAreaTable(table);
        table.setPlaceholder(new Label("Nessuna area studio da mostrare"));
        table.setPrefHeight(320);

        Label setNameLabel = new Label("Modifica il nome dell'area studio");
        setNameLabel.setStyle("-fx-padding: 10; -fx-font-weight: bold");
        TextField nameField = new TextField();
        nameField.setPromptText("Nome dell'area studio");
        nameField.getStyleClass().add("field");
        Button saveNameButton = new Button("Modifica");
        saveNameButton.getStyleClass().add("btn-primary");
        HBox changeNameBox = new HBox(5, setNameLabel, nameField, saveNameButton);

        Label typeLabel = new Label("Modifica il tipo di area studio");
        typeLabel.setStyle("-fx-padding: 10; -fx-font-weight: bold");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(Arrays.stream(StudyAreaType.values()).map(StudyAreaType::name).toList());
        typeBox.setValue(typeBox.getItems().getFirst()); // Valore predefinito
        Button saveTypeButton = new Button("Modifica");
        saveTypeButton.getStyleClass().add("btn-primary");
        typeBox.getStyleClass().add("btn-secondary");
        typeBox.setStyle("-fx-padding: 5");
        HBox changeTypeBox = new HBox(5, typeLabel, typeBox, saveTypeButton);

        Label policyLabel = new Label("Modifica la regola dell'area studio");
        policyLabel.setStyle("-fx-padding: 10; -fx-font-weight: bold");
        ComboBox<String> policyBox = new ComboBox<>();
        ControllerResult<List<String>> items = dashboardController.getAllTimePolicies();
        if(items.isSuccess()){
            policyBox.getItems().addAll((items.getResult()));
        }else {
            showResult(items.getMessage());
        }
        policyBox.setValue(policyBox.getItems().getFirst()); // Valore predefinito
        policyBox.getStyleClass().add("btn-secondary");
        policyBox.setStyle("-fx-padding: 5");
        Button savePolicyButton = new Button("Modifica");
        savePolicyButton.getStyleClass().add("btn-primary");
        HBox changePolicyBox = new HBox(5, policyLabel, policyBox, savePolicyButton);

        savePolicyButton.setOnAction(e -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una area studio valida");
            }
            else {
                ControllerResult<Void> res = dashboardController.updateStudyAreaPolicy(user, table.getSelectionModel().getSelectedItem(), policyBox.getValue());
                if(res.isSuccess()){
                    hideResult();
                    refreshStudyAreaTable(table);
                }else{
                    showResult(res.getMessage());
                }
            }

        });
        saveNameButton.setOnAction(e -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una area studio valida");
            }
            else {
                ControllerResult<Void> res = dashboardController.updateStudyAreaName(user, nameField.getText(), table.getSelectionModel().getSelectedItem());
                if(res.isSuccess()) {
                    hideResult();
                    refreshStudyAreaTable(table);
                }else {
                    showResult(res.getMessage());
                }
            }
        });
        saveTypeButton.setOnAction(e -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una area studio valida");
            }
            else {
                ControllerResult<Void> res = dashboardController.updateStudyAreaType(user, typeBox.getValue(), table.getSelectionModel().getSelectedItem());
                if(res.isSuccess()) {
                    hideResult();
                    refreshStudyAreaTable(table);
                }else {
                    showResult(res.getMessage());
                }

            }

        });

        box.getChildren().addAll(table, changeTypeBox, changeNameBox, changePolicyBox, resultLabel);
        return box;
    }

    private void refreshStudyAreaTable(TableView<StudyArea> table) {
        ControllerResult<List<StudyArea>> items = dashboardController.getAllStudyAreas(user);
        if(items.isSuccess()){
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            table.getSelectionModel().clearSelection();
            hideResult();
        }else {
            showResult(items.getMessage());
        }
    }

    private Node buildOpenReportsPanel() {
        VBox box = panelContainer("Segnalazioni aperte");
        TableView<AbandonmentReportDTO> table = new TableView<>();
        TableColumn<AbandonmentReportDTO, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getCreatedAt().format(formatter)));
        TableColumn<AbandonmentReportDTO, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getDescription()));
        TableColumn<AbandonmentReportDTO, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReport().getStatus())));
        TableColumn<AbandonmentReportDTO, String> authorCol = new TableColumn<>("Studente");
        authorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getAuthor().getName() + " " + d.getValue().getReport().getAuthor().getSurname()));
        TableColumn<AbandonmentReportDTO, String> seatIdCol = new TableColumn<>("Id posto");
        seatIdCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getSeatId())));
        TableColumn<AbandonmentReportDTO, String> areaNameCol = new TableColumn<>("Nome area");
        areaNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudyAreaName()));
        table.getColumns().addAll(dateCol, descCol, statusCol,  authorCol,  seatIdCol, areaNameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ControllerResult<List<AbandonmentReportDTO>> items = dashboardController.getOpenAbandonmentReports(user);
        if(items.isSuccess()){
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            hideResult();
        }else
            showResult(items.getMessage());
        table.getSelectionModel().clearSelection();
        table.setPlaceholder(new Label("Nessuna segnalazione da mostrare"));
        table.setPrefHeight(280);

        Button takeButton = new Button("Prendi in carico");
        takeButton.getStyleClass().add("btn-secondary");
        Button confirmButton = new Button("Conferma");
        confirmButton.getStyleClass().add("btn-primary");
        Button rejectButton = new Button("Rifiuta");
        rejectButton.getStyleClass().add("btn-danger");
        HBox actions = new HBox(10, takeButton, confirmButton, rejectButton);

        takeButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una segnalazione valida");
            }else{
                ControllerResult<Void> res = dashboardController.takeInChargeReport(user , table.getSelectionModel().getSelectedItem().getReport());
                if(res.isSuccess()) {
                    table.getItems().remove(table.getFocusModel().getFocusedItem());
                    hideResult();
                }else {
                    showResult(res.getMessage());
                }
            }
        });
        confirmButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una segnalazione valida");
            }else{
                ControllerResult<Void> res = dashboardController.directConfirmReport(user , table.getSelectionModel().getSelectedItem().getReport());
                if(res.isSuccess()) {
                    table.getItems().remove(table.getFocusModel().getFocusedItem());
                    hideResult();
                }else{
                    showResult(res.getMessage());
                }
            }

        });
        rejectButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una segnalazione valida");
            }else{
                ControllerResult<Void> res = dashboardController.directRejectReport(user , table.getSelectionModel().getSelectedItem().getReport());
                if(res.isSuccess()) {
                    table.getItems().remove(table.getFocusModel().getFocusedItem());
                    hideResult();
                }else{
                    showResult(res.getMessage());
                }
            }

        });
        box.getChildren().addAll(table, actions, resultLabel);
        return box;
    }

    private Node buildPendingReportsPanel() {
        VBox box = panelContainer("Segnalazioni prese in carico");
        TableView<AbandonmentReportDTO> table = new TableView<>();
        TableColumn<AbandonmentReportDTO, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getCreatedAt().format(formatter)));
        TableColumn<AbandonmentReportDTO, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getDescription()));
        TableColumn<AbandonmentReportDTO, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReport().getStatus())));
        TableColumn<AbandonmentReportDTO, String> authorCol = new TableColumn<>("Studente");
        authorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReport().getAuthor().getName() + " " + d.getValue().getReport().getAuthor().getSurname()));
        TableColumn<AbandonmentReportDTO, String> seatIdCol = new TableColumn<>("Id posto");
        seatIdCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getSeatId())));
        TableColumn<AbandonmentReportDTO, String> areaNameCol = new TableColumn<>("Nome area");
        areaNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudyAreaName()));
        table.getColumns().addAll(dateCol, descCol, statusCol,  authorCol,  seatIdCol, areaNameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ControllerResult<List<AbandonmentReportDTO>> items = dashboardController.getPendingAbandonmentReports(user);
        if(items.isSuccess()){
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            hideResult();
        }else
            showResult(items.getMessage());
        table.getSelectionModel().clearSelection();

        table.setPlaceholder(new Label("Nessuna segnalazione da mostrare"));
        table.setPrefHeight(280);

        Button confirmButton = new Button("Conferma");
        confirmButton.getStyleClass().add("btn-primary");
        Button rejectButton = new Button("Rifiuta");
        rejectButton.getStyleClass().add("btn-danger");
        HBox actions = new HBox(10, confirmButton, rejectButton);

        rejectButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una segnalazione valida");
            }else{
                ControllerResult<Void> res = dashboardController.rejectReport(user , table.getSelectionModel().getSelectedItem().getReport());
                if(res.isSuccess()) {
                    table.getItems().remove(table.getFocusModel().getFocusedItem());
                    hideResult();
                }else {
                    showResult(res.getMessage());
                }
            }
        });
        confirmButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem() == null){
                showResult("Scegliere una segnalazione valida");
            }else{
                ControllerResult<Void> res = dashboardController.confirmReport(user , table.getSelectionModel().getSelectedItem().getReport());
                if(res.isSuccess()) {
                    table.getItems().remove(table.getFocusModel().getFocusedItem());
                    hideResult();
                }else{
                    showResult(res.getMessage());
                }
            }
        });

        box.getChildren().addAll(table, actions, resultLabel);
        return box;
    }

    private Node buildClosedReportsPanel() {
        VBox box = panelContainer("Segnalazioni gestite");
        TableView<AbandonmentReport> table = new TableView<>();
        TableColumn<AbandonmentReport, String> dateCol = new TableColumn<>("Data Apertura");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreatedAt().format(formatter)));
        TableColumn<AbandonmentReport, String> closedDateCol = new TableColumn<>("Data Apertura");
        closedDateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getResolvedAt().format(formatter)));
        TableColumn<AbandonmentReport, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        TableColumn<AbandonmentReport, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStatus())));
        table.getColumns().addAll(dateCol, descCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ControllerResult<List<AbandonmentReport>> items = dashboardController.getAbandonmentReportHandled(user);
        if(items.isSuccess()){
            table.setItems(FXCollections.observableArrayList(items.getResult()));
            hideResult();
        }else
            showResult(items.getMessage());
        table.getSelectionModel().clearSelection();
        table.setPlaceholder(new Label("Nessuna segnalazione da mostrare"));
        table.setPrefHeight(280);


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

    private void showResult(String message) {
        resultLabel.setText(message);
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }

    private void hideResult() {
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
    }
}
