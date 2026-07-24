package com.smartstudy.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LibraryAccessView extends VBox {

    private final Label statusLabel;
    private final Button toggleButton;

    public LibraryAccessView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        getStyleClass().add("card");
        setMaxHeight(Region.USE_PREF_SIZE);

        Label heading = new Label("Accesso Biblioteca");
        heading.getStyleClass().add("card-heading");

        statusLabel = new Label("Stato: non presente in biblioteca");
        statusLabel.getStyleClass().add("hint-label");

        toggleButton = new Button("Entra in biblioteca");
        toggleButton.getStyleClass().add("btn-primary");
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
