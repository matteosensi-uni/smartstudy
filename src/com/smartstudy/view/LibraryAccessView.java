package com.smartstudy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LibraryAccessView extends VBox {

    private final Label statusLabel;
    private final Button toggleButton;

    public LibraryAccessView() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);
        setPadding(new Insets(10));

        Label heading = new Label("Accesso Biblioteca");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        statusLabel = new Label("Stato: non presente in biblioteca");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        toggleButton = new Button("Entra in biblioteca");
        toggleButton.setStyle("-fx-background-color: #2d6cdf; -fx-text-fill: white; -fx-font-weight: bold;");
        toggleButton.setMaxWidth(220);

        getChildren().addAll(heading, statusLabel, toggleButton);
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public Button getToggleButton() {
        return toggleButton;
    }
}
