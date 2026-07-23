package com.smartstudy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LibraryAccessDialogView extends VBox {

    private final TextField studentIdField;
    private final TextField libraryIdField;
    private final Label resultLabel;
    private final Button toggleButton;

    public LibraryAccessDialogView() {
        getStyleClass().add("root");
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(30));

        Label title = new Label("Ingresso / Uscita Biblioteca");
        title.getStyleClass().add("login-title");
        title.setStyle("-fx-font-size: 22px;");

        Label subtitle = new Label("Inserisci il tuo ID studente e l'ID della biblioteca");
        subtitle.getStyleClass().add("login-subtitle");

        studentIdField = new TextField();
        studentIdField.setPromptText("ID studente");
        studentIdField.setMaxWidth(260);
        studentIdField.getStyleClass().add("field");

        libraryIdField = new TextField();
        libraryIdField.setPromptText("ID biblioteca");
        libraryIdField.setMaxWidth(260);
        libraryIdField.getStyleClass().add("field");

        resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(260);
        resultLabel.setManaged(false);
        resultLabel.setVisible(false);

        toggleButton = new Button("Registra ingresso/uscita");
        toggleButton.setMaxWidth(260);
        toggleButton.setDefaultButton(true);
        toggleButton.getStyleClass().add("btn-primary");
        toggleButton.setOnAction(e -> handleToggle());

        VBox form = new VBox(10, studentIdField, libraryIdField, toggleButton, resultLabel);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(260);

        getChildren().addAll(title, subtitle, form);
    }

    private void handleToggle() {
        Long studentId = parseId(studentIdField.getText());
        Long libraryId = parseId(libraryIdField.getText());
        if (studentId == null || libraryId == null) {
            showResult("Inserisci ID validi per studente e biblioteca", false);
            return;
        }
        showResult("Operazione registrata correttamente", true);
    }

    private Long parseId(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showResult(String message, boolean success) {
        resultLabel.setText(message);
        resultLabel.getStyleClass().removeAll("error-label", "hint-label");
        resultLabel.getStyleClass().add(success ? "hint-label" : "error-label");
        resultLabel.setManaged(true);
        resultLabel.setVisible(true);
    }
}
