package com.smartstudy.view;

import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.StudyArea;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Supplier;

public class AdminView extends BorderPane {

    private final StackPane contentArea;

    public AdminView(Runnable onLogout) {
        setStyle("-fx-background-color: #f4f6f8;");

        setTop(buildHeader(onLogout));
        setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(buildPresencePanel());
        setCenter(contentArea);
    }

    private Node buildHeader(Runnable onLogout) {
        Label title = new Label("SmartStudy - Area Amministratore");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button logoutButton = new Button("Esci");
        logoutButton.setOnAction(e -> onLogout.run());

        HBox header = new HBox(title, spacer(), logoutButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #1f2937;");
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
        Button openReportsBtn = navButton("Segnalazioni Aperte", () -> buildReportsPanel("Segnalazioni aperte"));
        Button inChargeReportsBtn = navButton("Segnalazioni in Carico", () -> buildReportsPanel("Segnalazioni in carico"));
        Button closedReportsBtn = navButton("Segnalazioni Chiuse", () -> buildReportsPanel("Segnalazioni chiuse"));

        VBox sidebar = new VBox(8, presenceBtn, seatsBtn, areasBtn, openReportsBtn, inChargeReportsBtn, closedReportsBtn);
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

    private Node buildPresencePanel() {
        return new LibraryAccessView();
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
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessun posto da mostrare"));
        table.setPrefHeight(320);

        HBox actions = new HBox(10, new Button("Segna disponibile"), new Button("Segna rotto"));

        box.getChildren().addAll(table, actions);
        return box;
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
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessuna area studio da mostrare"));
        table.setPrefHeight(320);

        box.getChildren().add(table);
        return box;
    }

    private Node buildReportsPanel(String title) {
        VBox box = panelContainer(title);

        TableView<AbandonmentReport> table = new TableView<>();
        TableColumn<AbandonmentReport, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCreatedAt())));
        TableColumn<AbandonmentReport, String> descCol = new TableColumn<>("Descrizione");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        TableColumn<AbandonmentReport, String> statusCol = new TableColumn<>("Stato");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStatus())));
        table.getColumns().addAll(dateCol, descCol, statusCol);
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Nessuna segnalazione da mostrare"));
        table.setPrefHeight(280);

        HBox actions = new HBox(10, new Button("Prendi in carico"), new Button("Conferma"), new Button("Rifiuta"));

        box.getChildren().addAll(table, actions);
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
